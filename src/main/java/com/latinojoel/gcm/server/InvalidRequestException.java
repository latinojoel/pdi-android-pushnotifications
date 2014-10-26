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

import java.io.IOException;

/**
 * Exception thrown when GCM returned an error due to an invalid request.
 * <p>
 * This is equivalent to GCM posts that return an HTTP error different of 200.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
public final class InvalidRequestException extends IOException {

  /** The serial Version. **/
  private static final long serialVersionUID = 8339582610185567958L;
  private final int status;
  private final String description;

  public InvalidRequestException(int status) {
    this(status, null);
  }

  public InvalidRequestException(int status, String description) {
    super(getMessage(status, description));
    this.status = status;
    this.description = description;
  }

  /**
   * Gets exception message.
   * 
   * @param status the HTTP status code.
   * @param description the description message.
   * @return the exception message.
   */
  private static String getMessage(int status, String description) {
    final StringBuilder base = new StringBuilder("HTTP Status Code: ").append(status);
    if (description != null) {
      base.append("(").append(description).append(")");
    }
    return base.toString();
  }

  /**
   * Gets the HTTP Status Code.
   * 
   * @return the HTTP status code.
   */
  public int getHttpStatusCode() {
    return status;
  }

  /**
   * Gets the error description.
   * 
   * @return the description.
   */
  public String getDescription() {
    return description;
  }
}
