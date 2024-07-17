package eu.chargetime.ocpp.model.securityext.test;

/*
   ChargeTime.eu - Java-OCA-OCPP

   MIT License

   Copyright (C) 2022 Mathias Oben <mathias.oben@enervalis.com>

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

import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.securityext.CertificateSignedRequest;
import eu.chargetime.ocpp.utilities.TestUtilities;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CertificateSignedRequestTest {

  @Test
  public void validate_constructor_returnsTrue() {
    // given
    String certificateChain = givenCertificateChain();
    CertificateSignedRequest request = new CertificateSignedRequest(certificateChain);

    // When
    boolean actual = request.validate();

    // Then
    assertTrue(actual);
  }

  @Test(expected = PropertyConstraintException.class)
  public void constructor_nullCertificateChain_throwsPropertyConstraintException() {
    // When
    new CertificateSignedRequest(null);

    // Then throws
  }

  @Test
  public void setCertificateChain_maximumLengthString_nothingThrown() {
    // Given
    String certificateChain = TestUtilities.aString(10000);

    // When
    new CertificateSignedRequest(certificateChain);

    // Then nothing thrown
  }

  @Test(expected = PropertyConstraintException.class)
  public void setCertificateChain_exceedingLengthString_throwsPropertyConstraintException() {
    // Given
    String certificateChain = TestUtilities.aString(10001);

    // When
    new CertificateSignedRequest(certificateChain);

    // Then throws
  }

  private String givenCertificateChain() {
    return "PEM encoded X.509 certificate";
  }
}
