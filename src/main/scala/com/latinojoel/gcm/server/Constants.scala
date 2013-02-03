package com.latinojoel.gcm.server

/**
 * Constants used on GCM service communication.
 */
package object Constants {
  /**
   * Endpoint for sending messages.
   */
  val GCM_SEND_ENDPOINT: String =
    "https://android.googleapis.com/gcm/send";

  /**
   * HTTP parameter for registration id.
   */
  val PARAM_REGISTRATION_ID: String = "registration_id";

  /**
   * HTTP parameter for collapse key.
   */
  val PARAM_COLLAPSE_KEY: String = "collapse_key";

  /**
   * HTTP parameter for delaying the message delivery if the device is idle.
   */
  val PARAM_DELAY_WHILE_IDLE: String = "delay_while_idle";

  /**
   * HTTP parameter for telling gcm to validate the message without actually sending it.
   */
  val PARAM_DRY_RUN: String = "dry_run";

  /**
   * HTTP parameter for package name that can be used to restrict message delivery by matching
   * against the package name used to generate the registration id.
   */
  val PARAM_RESTRICTED_PACKAGE_NAME: String = "restricted_package_name";

  /**
   * Prefix to HTTP parameter used to pass key-values in the message payload.
   */
  val PARAM_PAYLOAD_PREFIX: String = "data.";

  /**
   * Prefix to HTTP parameter used to set the message time-to-live.
   */
  val PARAM_TIME_TO_LIVE: String = "time_to_live";

  /**
   * Too many messages sent by the sender. Retry after a while.
   */
  val ERROR_QUOTA_EXCEEDED: String = "QuotaExceeded";

  /**
   * Too many messages sent by the sender to a specific device.
   * Retry after a while.
   */
  val ERROR_DEVICE_QUOTA_EXCEEDED: String =
    "DeviceQuotaExceeded";

  /**
   * Missing registration_id.
   * Sender should always add the registration_id to the request.
   */
  val ERROR_MISSING_REGISTRATION: String = "MissingRegistration";

  /**
   * Bad registration_id. Sender should remove this registration_id.
   */
  val ERROR_INVALID_REGISTRATION: String = "InvalidRegistration";

  /**
   * The sender_id contained in the registration_id does not match the
   * sender_id used to register with the GCM servers.
   */
  val ERROR_MISMATCH_SENDER_ID: String = "MismatchSenderId";

  /**
   * The user has uninstalled the application or turned off notifications.
   * Sender should stop sending messages to this device and delete the
   * registration_id. The client needs to re-register with the GCM servers to
   * receive notifications again.
   */
  val ERROR_NOT_REGISTERED: String = "NotRegistered";

  /**
   * The payload of the message is too big, see the limitations.
   * Reduce the size of the message.
   */
  val ERROR_MESSAGE_TOO_BIG: String = "MessageTooBig";

  /**
   * Collapse key is required. Include collapse key in the request.
   */
  val ERROR_MISSING_COLLAPSE_KEY: String = "MissingCollapseKey";

  /**
   * A particular message could not be sent because the GCM servers were not
   * available. Used only on JSON requests, as in plain text requests
   * unavailability is indicated by a 503 response.
   */
  val ERROR_UNAVAILABLE: String = "Unavailable";

  /**
   * A particular message could not be sent because the GCM servers encountered
   * an error. Used only on JSON requests, as in plain text requests internal
   * errors are indicated by a 500 response.
   */
  val ERROR_INTERNAL_SERVER_ERROR: String =
    "InternalServerError";

  /**
   * Time to Live value passed is less than zero or more than maximum.
   */
  val ERROR_INVALID_TTL: String = "InvalidTtl";

  /**
   * Token returned by GCM when a message was successfully sent.
   */
  val TOKEN_MESSAGE_ID: String = "id";

  /**
   * Token returned by GCM when the requested registration id has a canonical
   * value.
   */
  val TOKEN_CANONICAL_REG_ID: String = "registration_id";

  /**
   * Token returned by GCM when there was an error sending a message.
   */
  val TOKEN_ERROR: String = "Error";

  /**
   * JSON-only field representing the registration ids.
   */
  val JSON_REGISTRATION_IDS: String = "registration_ids";

  /**
   * JSON-only field representing the payload data.
   */
  val JSON_PAYLOAD: String = "data";

  /**
   * JSON-only field representing the number of successful messages.
   */
  val JSON_SUCCESS: String = "success";

  /**
   * JSON-only field representing the number of failed messages.
   */
  val JSON_FAILURE: String = "failure";

  /**
   * JSON-only field representing the number of messages with a canonical
   * registration id.
   */
  val JSON_CANONICAL_IDS: String = "canonical_ids";

  /**
   * JSON-only field representing the id of the multicast request.
   */
  val JSON_MULTICAST_ID: String = "multicast_id";

  /**
   * JSON-only field representing the result of each individual request.
   */
  val JSON_RESULTS: String = "results";

  /**
   * JSON-only field representing the error field of an individual request.
   */
  val JSON_ERROR: String = "error";

  /**
   * JSON-only field sent by GCM when a message was successfully sent.
   */
  val JSON_MESSAGE_ID: String = "message_id";
  
  def Constants() = {
    throw new UnsupportedOperationException();
  }
}