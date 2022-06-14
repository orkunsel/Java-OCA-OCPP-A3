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
import eu.chargetime.ocpp.model.securityext.GetLogConfirmation;
import eu.chargetime.ocpp.model.securityext.types.LogStatusEnumType;
import eu.chargetime.ocpp.utilities.TestUtilities;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GetLogConfirmationTest {

  @Test
  public void validate_constructor_returnsTrue() {
    // Given
    LogStatusEnumType status = LogStatusEnumType.Accepted;
    GetLogConfirmation confirmation = new GetLogConfirmation(status);

    // When
    boolean actual = confirmation.validate();

    // Then
    assertTrue(actual);
  }

  @Test
  public void validate_setRequired_returnsTrue() {
    // Given
    LogStatusEnumType status = LogStatusEnumType.Accepted;
    GetLogConfirmation confirmation = new GetLogConfirmation(null);
    confirmation.setStatus(status);

    // When
    boolean actual = confirmation.validate();

    // Then
    assertTrue(actual);
  }

  @Test
  public void validate_returnFalse() {
    // Given
    GetLogConfirmation request = new GetLogConfirmation(null);

    // When
    boolean actual = request.validate();

    // Then
    assertFalse(actual);
  }

  @Test
  public void validate_setRequiredAndOptional_returnsTrue() {
    // Given
    LogStatusEnumType status = LogStatusEnumType.Accepted;
    String filename = "file.log";
    GetLogConfirmation confirmation = new GetLogConfirmation(null);
    confirmation.setStatus(status);
    confirmation.setFilename(filename);

    // When
    boolean actual = confirmation.validate();

    // Then
    assertTrue(actual);
  }

  @Test(expected = PropertyConstraintException.class)
  public void setFilename_exceedingLengthString_throwsPropertyConstraintException() {
    // Given
    String filename = TestUtilities.aString(256);
    GetLogConfirmation confirmation = new GetLogConfirmation(null);

    // When
    confirmation.setFilename(filename);

    // Then throws
  }
}
