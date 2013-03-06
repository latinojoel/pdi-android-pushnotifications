package com.latinojoel.gcm.server

import java.lang.StringBuilder

/**
 * Result of a GCM message request that returned HTTP status code 200.
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
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