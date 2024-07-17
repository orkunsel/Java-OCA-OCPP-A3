package eu.chargetime.ocpp.model.core;

import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Validatable;
import eu.chargetime.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
 * ChargeTime.eu - Java-OCA-OCPP
 *
 * MIT License
 *
 * Copyright (C) 2016-2018 Thomas Volden <tv@chargetime.eu>
 * Copyright (C) 2019 Kevin Raddatz <kevin.raddatz@valtech-mobility.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@XmlRootElement
@XmlType(
    propOrder = {
      "chargingProfileId",
      "transactionId",
      "stackLevel",
      "chargingProfilePurpose",
      "chargingProfileKind",
      "recurrencyKind",
      "validFrom",
      "validTo",
      "chargingSchedule"
    })
public class ChargingProfile implements Validatable {
  private Integer chargingProfileId;
  private Integer transactionId;
  private Integer stackLevel;
  private ChargingProfilePurposeType chargingProfilePurpose;
  private ChargingProfileKindType chargingProfileKind;
  private RecurrencyKindType recurrencyKind;
  private ZonedDateTime validFrom;
  private ZonedDateTime validTo;
  private ChargingSchedule chargingSchedule;

  private ChargingProfile(Builder builder) {
    this.chargingProfileId = builder.chargingProfileId;
    this.transactionId = builder.transactionId;
    this.stackLevel = builder.stackLevel;
    this.chargingProfilePurpose = builder.chargingProfilePurpose;
    this.chargingProfileKind = builder.chargingProfileKind;
    this.recurrencyKind = builder.recurrencyKind;
    this.validFrom = builder.validFrom;
    this.validTo = builder.validTo;
    this.chargingSchedule = builder.chargingSchedule;
  }

  @Override
  public boolean validate() {
    boolean valid = chargingProfileId != null;
    valid &= (stackLevel != null && stackLevel >= 0);
    valid &= chargingProfilePurpose != null;
    valid &=
        (transactionId == null || chargingProfilePurpose == ChargingProfilePurposeType.TxProfile);
    valid &= chargingProfileKind != null;
    valid &= (chargingSchedule != null && chargingSchedule.validate());
    return valid;
  }

  public static class Builder {
    private Integer chargingProfileId;
    private Integer transactionId;
    private Integer stackLevel;
    private ChargingProfilePurposeType chargingProfilePurpose;
    private ChargingProfileKindType chargingProfileKind;
    private RecurrencyKindType recurrencyKind;
    private ZonedDateTime validFrom;
    private ZonedDateTime validTo;
    private ChargingSchedule chargingSchedule;

    public Builder withChargingProfileId(Integer chargingProfileId) {
      if (chargingProfileId == null) {
        throw new PropertyConstraintException(null, "chargingProfileId must be present");
      }
      this.chargingProfileId = chargingProfileId;
      return this;
    }

    public Builder withTransactionId(Integer transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    public Builder withStackLevel(Integer stackLevel) {
      if (stackLevel == null || stackLevel < 0) {
        throw new PropertyConstraintException(stackLevel, "stackLevel must be >= 0");
      }
      this.stackLevel = stackLevel;
      return this;
    }

    public Builder withChargingProfilePurpose(ChargingProfilePurposeType chargingProfilePurpose) {
      this.chargingProfilePurpose = chargingProfilePurpose;
      return this;
    }

    public Builder withChargingProfileKind(ChargingProfileKindType chargingProfileKind) {
      this.chargingProfileKind = chargingProfileKind;
      return this;
    }

    public Builder withRecurrencyKind(RecurrencyKindType recurrencyKind) {
      this.recurrencyKind = recurrencyKind;
      return this;
    }

    public Builder withValidFrom(ZonedDateTime validFrom) {
      this.validFrom = validFrom;
      return this;
    }

    public Builder withValidTo(ZonedDateTime validTo) {
      this.validTo = validTo;
      return this;
    }

    public Builder withChargingSchedule(ChargingSchedule chargingSchedule) {
      this.chargingSchedule = chargingSchedule;
      return this;
    }

    public ChargingProfile build() {
      return new ChargingProfile(this);
    }
  }

  // Getters, setters, equals, hashCode, and toString methods remain the same
  // ...

  // Example of the toString method implementation
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("chargingProfileId", chargingProfileId)
        .add("transactionId", transactionId)
        .add("stackLevel", stackLevel)
        .add("chargingProfilePurpose", chargingProfilePurpose)
        .add("chargingProfileKind", chargingProfileKind)
        .add("recurrencyKind", recurrencyKind)
        .add("validFrom", validFrom)
        .add("validTo", validTo)
        .add("chargingSchedule", chargingSchedule)
        .add("isValid", validate())
        .toString();
  }
}
