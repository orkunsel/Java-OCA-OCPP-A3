package eu.chargetime.ocpp;
/*
    ChargeTime.eu - Java-OCA-OCPP
    
    MIT License

    Copyright (C) 2016 Thomas Volden <tv@chargetime.eu>

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */

import com.sun.net.httpserver.HttpServer;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class WebServiceListener implements Listener {

    final private String WSDL_CENTRAL_SYSTEM = "eu/chargetime/ocpp/OCPP_CentralSystemService_1.6.wsdl";

    private ListenerEvents events;
    private String fromUrl = null;

    @Override
    public void open(String hostname, int port, ListenerEvents listenerEvents) {
        events = listenerEvents;
        fromUrl = String.format("http://%s:%d", hostname, port);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
            server.createContext("/", new WSHttpHandler(WSDL_CENTRAL_SYSTEM, new WSHttpEventHandler()));

            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

    private class WSHttpEventHandler implements WSHttpHandlerEvents {

        HashMap<String, SOAPReceiver> chargeboxes;

        public WSHttpEventHandler() {
            chargeboxes = new HashMap<>();
        }

        @Override
        public SOAPMessage incomingRequest(SOAPMessage message) {
            String identity = SOAPSyncHelper.getHeaderValue(message, "chargeBoxIdentity");
            if (!chargeboxes.containsKey(identity)) {
                String toUrl = SOAPSyncHelper.getHeaderValue(message, "From");
                SOAPReceiver soapReceiver = new SOAPReceiver(toUrl);
                SOAPCommunicator communicator = new SOAPCommunicator(identity, fromUrl, soapReceiver);
                communicator.setToUrl(toUrl);
                events.newSession(new Session(communicator, new Queue()));
                chargeboxes.put(identity, soapReceiver);
            }

            SOAPMessage confirmation = null;
            try {
                confirmation = chargeboxes.get(identity).relay(message).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                message.writeTo(out);
                String strMsg = new String(out.toByteArray());
                System.out.print(strMsg);
            } catch (SOAPException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return confirmation;
        }
    }

}
