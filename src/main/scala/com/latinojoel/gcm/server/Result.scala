package com.latinojoel.gcm.server

import java.lang.StringBuilder

/**
 * Result of a GCM message request that returned HTTP status code 200.
 *
 * <p>
 * If the message is successfully created, the {@link #getMessageId()} returns
 * the message id and {@link #getErrorCodeName()} returns {@literal null};
 * otherwise, {@link #getMessageId()} returns {@literal null} and
 * {@link #getErrorCodeName()} returns the code of the error.
 *
 * <p>
 * There are cases when a request is accept and the message successfully
 * created, but GCM has a canonical registration id for that device. In this
 * case, the server should update the registration id to avoid rejected requests
 * in the future.
 *
 * <p>
 * In a nutshell, the workflow to handle a result is:
 * <pre>
 *   - Call {@link #getMessageId()}:
 *     - {@literal null} means error, call {@link #getErrorCodeName()}
 *     - non-{@literal null} means the message was created:
 *       - Call {@link #getCanonicalRegistrationId()}
 *         - if it returns {@literal null}, do nothing.
 *         - otherwise, update the server datastore with the new id.
 * </pre>
 */
@serializable
class Result(builder: Result#Builder) {

  private var messageId: String = _
  private var canonicalRegistrationId: String = _
  private var errorCode: String = _

   class Builder {

    // optional parameters
    var messageId: String = _
    var canonicalRegistrationId: String = _
    var errorCode: String = _

    def canonicalRegistrationId(value: String): Builder = {
      canonicalRegistrationId = value
      this
    }

    def messageId(value: String): Builder = {
      messageId = value
      this;
    }

    def errorCode(value: String): Builder = {
      errorCode = value
      this
    }

    def build(): Result = {
      new Result(this)
    }
  }

  def Result(builder: Result#Builder) = {
    canonicalRegistrationId = builder.canonicalRegistrationId
    messageId = builder.messageId
    errorCode = builder.errorCode
  }

  /**
   * Gets the message id, if any.
   */
  def getMessageId(): String = messageId

  /**
   * Gets the canonical registration id, if any.
   */
  def getCanonicalRegistrationId(): String = canonicalRegistrationId

  /**
   * Gets the error code, if any.
   */
  def getErrorCodeName(): String = errorCode

  override def toString(): String = {
    var builder: StringBuilder = new StringBuilder("[");
    if (messageId != null) {
      builder.append(" messageId=").append(messageId)
    }
    if (canonicalRegistrationId != null) {
      builder.append(" canonicalRegistrationId=")
        .append(canonicalRegistrationId)
    }
    if (errorCode != null) {
      builder.append(" errorCode=").append(errorCode)
    }
    builder.append(" ]").toString()
  }

}