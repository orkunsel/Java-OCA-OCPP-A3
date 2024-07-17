package eu.chargetime.ocpp;

import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.wss.WssFactoryBuilder;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiProtocolWebSocketListener implements Listener {
    private static final Logger logger = LoggerFactory.getLogger(MultiProtocolWebSocketListener.class);

    private static final int DEFAULT_WEBSOCKET_WORKER_COUNT = 4;
    private static final String HTTP_HEADER_PROXIED_ADDRESS = "X-Forwarded-For";

    private final MultiProtocolSessionFactory sessionFactory;
    private final List<Draft> drafts;
    private final JSONConfiguration configuration;
    private final Map<WebSocket, WebSocketReceiver> sockets = new ConcurrentHashMap<>();
    private volatile WebSocketServer server;
    private WssFactoryBuilder wssFactoryBuilder;
    private volatile boolean closed = true;
    private boolean handleRequestAsync;  // Ensure this variable is declared and properly used

    public MultiProtocolWebSocketListener(MultiProtocolSessionFactory sessionFactory, JSONConfiguration configuration, Draft... drafts) {
        this.sessionFactory = sessionFactory;
        this.configuration = configuration;
        this.drafts = Arrays.asList(drafts);
    }

    public void open(String hostname, int port, ListenerEvents handler) {
        server = new WebSocketServer(new InetSocketAddress(hostname, port), configuration.getParameter(JSONConfiguration.WEBSOCKET_WORKER_COUNT, DEFAULT_WEBSOCKET_WORKER_COUNT), drafts) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                handleOnOpen(conn, handshake, handler);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                handleClose(conn, code, reason);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                handleMessage(conn, message);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                handleError(conn, ex);
            }

            @Override
            public void onStart() {
                logger.info("WebSocket server started successfully on port: {}", getPort());
            }
        };

        if (wssFactoryBuilder != null) {
            server.setWebSocketFactory(wssFactoryBuilder.build());
        }

        configure();
        server.start();
        closed = false;
    }

    private void handleOnOpen(WebSocket conn, ClientHandshake handshake, ListenerEvents handler) {
        logger.info("WebSocket connection opened with resource descriptor: {}", handshake.getResourceDescriptor());
        WebSocketReceiver receiver = new WebSocketReceiver(new WebSocketReceiverEvents() {
            @Override
            public boolean isClosed() {
                return closed;
            }

            @Override
            public void close() {
                conn.close();
            }

            @Override
            public void relay(String message) {
                conn.send(message);
            }
        });

        sockets.put(conn, receiver);
        SessionInformation information = new SessionInformation.Builder()
            .Identifier(handshake.getResourceDescriptor())
            .InternetAddress(conn.getRemoteSocketAddress())
            .ProtocolVersion(ProtocolVersion.fromSubProtocolName(conn.getProtocol().toString()))  // Correctly convert protocol to String
            .ProxiedAddress(handshake.getFieldValue(HTTP_HEADER_PROXIED_ADDRESS))
            .build();

        handler.newSession(sessionFactory.createSession(new JSONCommunicator(receiver), information.getProtocolVersion()), information);
    }

    private void handleClose(WebSocket conn, int code, String reason) {
        logger.info("WebSocket connection closed: {}, code: {}, reason: {}, remote: {}", conn.getResourceDescriptor(), code, reason);
        WebSocketReceiver receiver = sockets.remove(conn);
        if (receiver != null) {
            receiver.disconnect();
        }
    }

    private void handleMessage(WebSocket conn, String message) {
        logger.debug("Received message from {}: {}", conn.getResourceDescriptor(), message);
        sockets.get(conn).relay(message);
    }

    private void handleError(WebSocket conn, Exception ex) {
        logger.error("Error on connection {}: {}", conn != null ? conn.getResourceDescriptor() : "unknown", ex.getMessage(), ex);
    }

    private void configure() {
        server.setReuseAddr(true);
        server.setTcpNoDelay(false);
        server.setConnectionLostTimeout(60);
    }

    public void enableWSS(WssFactoryBuilder builder) {
        this.wssFactoryBuilder = builder;
    }

    public int getPort() {
        return server.getPort();
    }

    public void close() {
        if (server != null) {
            try {
                server.stop();
                sockets.clear();
                closed = true;
            } catch (Exception e) {
                logger.error("Error closing WebSocket server: {}", e.getMessage(), e);
            } finally {
                server = null;
            }
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void setAsyncRequestHandler(boolean async) {
        this.handleRequestAsync = async;
    }
}
