package com.latinojoel.gcm.server

/**
 * Exception thrown when GCM returned an error due to an invalid request.
 * <p>
 * This is equivalent to GCM posts that return an HTTP error different of 200.
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 */
class InvalidRequestException(message: String, nestedException: Throwable) extends Exception(message, nestedException) {

  var status: Int = _;
  var description: String = null;

  def this(message: String) = this(message, null)

  def this(nestedException: Throwable) = this("", nestedException)

  def InvalidRequestException(status: Int) = new InvalidRequestException(status, null)

  def this(status: Int, description: String) {
    this({
      var base: StringBuilder = new StringBuilder("HTTP Status Code: ").append(status);
      if (description != null) {
        base.append("(").append(description).append(")");
      }
      base.toString();
    });
    this.status = status;
    this.description = description;
  }

  /**
   * Gets the HTTP Status Code.
   */
  def getHttpStatusCode(): Int = status

  /**
   * Gets the error description.
   */
  def getDescription(): String = description
}