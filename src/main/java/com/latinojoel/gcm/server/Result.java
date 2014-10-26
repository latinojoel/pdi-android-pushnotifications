/**
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package com.latinojoel.gcm.server;

import java.io.Serializable;

/**
 * Result of a GCM message request that returned HTTP status code 200.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
public final class Result implements Serializable {

  /** The serial Version. **/
  private static final long serialVersionUID = 8360789618407781082L;

  private final String messageId;
  private final String canonicalRegistrationId;
  private final String errorCode;

  /**
   * The GCM Result builder class.
   * 
   * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
   * @version $Revision: 666 $
   * 
   */
  public static final class Builder {

    // optional parameters
    private String messageId;
    private String canonicalRegistrationId;
    private String errorCode;

    /**
     * Sets the canonical registration id.
     * 
     * @param value the canonical registration id.
     * @return the GCM result builder.
     */
    public Builder canonicalRegistrationId(String value) {
      canonicalRegistrationId = value;
      return this;
    }

    /**
     * Sets the message id.
     * 
     * @param value the message id.
     * @return the GCM result builder.
     */
    public Builder messageId(String value) {
      messageId = value;
      return this;
    }

    /**
     * Sets the code error.
     * 
     * @param value the code error.
     * @return the GCM result builder.
     */
    public Builder errorCode(String value) {
      errorCode = value;
      return this;
    }

    /**
     * Builds the GCM result.
     * 
     * @return the GCM result.
     */
    public Result build() {
      return new Result(this);
    }
  }

  private Result(Builder builder) {
    canonicalRegistrationId = builder.canonicalRegistrationId;
    messageId = builder.messageId;
    errorCode = builder.errorCode;
  }

  /**
   * Gets the message id, if any.
   * 
   * @return the message id.
   */
  public String getMessageId() {
    return messageId;
  }

  /**
   * Gets the canonical registration id, if any.
   * 
   * @return the canonical registration id.
   */
  public String getCanonicalRegistrationId() {
    return canonicalRegistrationId;
  }

  /**
   * Gets the error code, if any.
   * 
   * @return the error code.
   */
  public String getErrorCodeName() {
    return errorCode;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder("[");
    if (messageId != null) {
      builder.append(" messageId=").append(messageId);
    }
    if (canonicalRegistrationId != null) {
      builder.append(" canonicalRegistrationId=").append(canonicalRegistrationId);
    }
    if (errorCode != null) {
      builder.append(" errorCode=").append(errorCode);
    }
    return builder.append(" ]").toString();
  }

}
