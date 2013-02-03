package com.latinojoel.gcm.server

import com.latinojoel.gcm.server.Constants.GCM_SEND_ENDPOINT
import com.latinojoel.gcm.server.Constants.JSON_CANONICAL_IDS
import com.latinojoel.gcm.server.Constants.JSON_ERROR
import com.latinojoel.gcm.server.Constants.JSON_FAILURE
import com.latinojoel.gcm.server.Constants.JSON_MESSAGE_ID
import com.latinojoel.gcm.server.Constants.JSON_MULTICAST_ID
import com.latinojoel.gcm.server.Constants.JSON_PAYLOAD
import com.latinojoel.gcm.server.Constants.JSON_REGISTRATION_IDS
import com.latinojoel.gcm.server.Constants.JSON_RESULTS
import com.latinojoel.gcm.server.Constants.JSON_SUCCESS
import com.latinojoel.gcm.server.Constants.PARAM_COLLAPSE_KEY
import com.latinojoel.gcm.server.Constants.PARAM_DELAY_WHILE_IDLE
import com.latinojoel.gcm.server.Constants.PARAM_DRY_RUN
import com.latinojoel.gcm.server.Constants.PARAM_PAYLOAD_PREFIX
import com.latinojoel.gcm.server.Constants.PARAM_REGISTRATION_ID
import com.latinojoel.gcm.server.Constants.PARAM_RESTRICTED_PACKAGE_NAME
import com.latinojoel.gcm.server.Constants.PARAM_TIME_TO_LIVE
import com.latinojoel.gcm.server.Constants.TOKEN_CANONICAL_REG_ID
import com.latinojoel.gcm.server.Constants.TOKEN_ERROR
import com.latinojoel.gcm.server.Constants.TOKEN_MESSAGE_ID
import scala.util.Random
import java.util.logging.Logger
import java.util.logging.Level
import java.io.IOException
import java.net.HttpURLConnection
import java.util.ArrayList
import java.util.List
import java.util.HashMap
import java.util.Map
import org.json.simple.JSONValue
import org.json.simple.parser.JSONParser
import org.json.simple.JSONObject
import org.json.simple.parser.ParseException
import java.io.Closeable
import java.io.OutputStream
import java.net.URL
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URLEncoder
import scala.collection.JavaConversions._

/**
 * Helper class to send messages to the GCM service using an API Key.
 *
 * @param key API key obtained through the Google API Console.
 */
class Sender(APIKey: String) {

  var encoding: String = "UTF-8"

  val logger: Logger = Logger.getLogger(classOf[Sender].getName())

  var key: String = NonNull.nonNull(APIKey)

  /**
   * Sends a message to one device, retrying in case of unavailability.
   *
   * <p>
   * <strong>Note: </strong> this method uses exponential back-off to retry in
   * case of service unavailability and hence could block the calling thread
   * for many seconds.
   *
   * @param message message to be sent, including the device's registration id.
   * @param registrationId device where the message will be sent.
   * @param retries number of retries in case of service unavailability errors.
   *
   * @return result of the request (see its javadoc for more details).
   *
   * @throws IllegalArgumentException if registrationId is {@literal null}.
   * @throws InvalidRequestException if GCM didn't returned a 200 or 5xx status.
   * @throws IOException if message could not be sent.
   */
  def send(message: Message, registrationId: String, retries: Int, backoffDelay: Int): String = {
    var attempt: Int = 0
    var result: String = null
    var tryAgain: Boolean = false
    do {
      attempt += 1
      if (logger.isLoggable(Level.FINE))
        logger.fine("Attempt #" + attempt + " to send message " + message + " to regIds " + registrationId)

      result = sendNoRetry(message, registrationId);
      tryAgain = result == null && attempt <= retries
      if (tryAgain) {
        sleep(backoffDelay)
      }
    } while (tryAgain)
    if (result == null)
      throw new IOException("Could not send message after " + attempt + " attempts")

    return result
  }

  /**
   * Sends a message without retrying in case of service unavailability. See
   * {@link #send(Message, String, int)} for more info.
   *
   * @return result of the post, or {@literal null} if the GCM service was
   *         unavailable or any network exception caused the request to fail.
   *
   * @throws InvalidRequestException if GCM didn't returned a 200 or 5xx status.
   * @throws IllegalArgumentException if registrationId is {@literal null}.
   */
  def sendNoRetry(message: Message, registrationId: String): String = {
    val body: StringBuilder = newBody(PARAM_REGISTRATION_ID, registrationId)
    val delayWhileIdle: Boolean = message.isDelayWhileIdle()
    addParameter(body, PARAM_DELAY_WHILE_IDLE, if (delayWhileIdle) "1" else "0")
    val dryRun: Boolean = message.isDryRun()
    addParameter(body, PARAM_DRY_RUN, if (dryRun) "1" else "0")
    val collapseKey: String = message.getCollapseKey()
    if (collapseKey != null)
      addParameter(body, PARAM_COLLAPSE_KEY, collapseKey)

    val restrictedPackageName: String = message.getRestrictedPackageName()
    if (restrictedPackageName != null)
      addParameter(body, PARAM_RESTRICTED_PACKAGE_NAME, restrictedPackageName)

    val timeToLive: Int = message.getTimeToLive()
    addParameter(body, PARAM_TIME_TO_LIVE, Integer.toString(timeToLive))
    for (entry <- message.getData().keys) {
      var key: String = entry
      val value: String = message.getData().get(key).get
      if (key == null || value == null)
        logger.warning("Ignoring payload entry thas has null: " + entry)
      else {
        key = PARAM_PAYLOAD_PREFIX + key
        addParameter(body, key, URLEncoder.encode(value, encoding))
      }
    }
    val requestBody: String = body.toString()
    logger.finest("Request body: " + requestBody)
    var conn: HttpURLConnection = null
    var status: Int = 0
    try {
      conn = post(GCM_SEND_ENDPOINT, requestBody)
      status = conn.getResponseCode()
    } catch {
      case e: IOException => {
        logger.log(Level.FINE, "IOException posting to GCM", e)
        return "IOException posting to GCM"
      }
    }
    if (status / 100 == 5) {
      logger.fine("GCM service is unavailable (status " + status + ")")
      return "GCM service is unavailable (status " + status + ")"
    }
    var responseBody: String = ""
    if (status != 200) {
      try {
        responseBody = getAndClose(conn.getErrorStream())
        logger.finest("Plain post error response: " + responseBody)
      } catch {
        // ignore the exception since it will thrown an InvalidRequestException
        // anyways
        case e: IOException => {
          responseBody = "N/A"
          logger.log(Level.FINE, "Exception reading response: ", e)
        }
      }
      throw new InvalidRequestException(status, responseBody)
    } else {
      try {
        responseBody = getAndClose(conn.getInputStream())
      } catch {
        case e: IOException => {
          logger.log(Level.WARNING, "Exception reading response: ", e)
          // return null so it can retry
          return "Exception reading response: " + e.getMessage()
        }
      }
    }
    return responseBody;
    //    var lines: Array[String] = responseBody.split("\n")
    //    if (lines.length == 0 || lines.->(0).equals(""))
    //      throw new IOException("Received empty response from GCM service.")
    //
    //    val firstLine: String = lines.apply(0)
    //    var responseParts: Array[String] = split(firstLine)
    //    var token: String = responseParts.apply(0)
    //    var value: String = responseParts.apply(1)
    //    if (token.equals(TOKEN_MESSAGE_ID)) {
    //      val resultInit: Result = new Result(null)
    //      var builder: Result#Builder = new resultInit.Builder().messageId(value)
    //      // check for canonical registration id
    //      if (lines.length > 1) {
    //        val secondLine: String = lines.apply(1)
    //        responseParts = split(secondLine)
    //        token = responseParts.apply(0)
    //        value = responseParts.apply(1)
    //        if (token.equals(TOKEN_CANONICAL_REG_ID))
    //          builder.canonicalRegistrationId(value)
    //        else
    //          logger.warning("Invalid response from GCM: " + responseBody)
    //
    //      }
    //      val result: Result = builder.build()
    //      if (logger.isLoggable(Level.FINE))
    //        logger.fine("Message created succesfully (" + result + ")")
    //      result
    //    } else if (token.equals(TOKEN_ERROR)) {
    //      val resultInit: Result = new Result(null)
    //      new resultInit.Builder().errorCode(value).build()
    //    } else
    //      throw new IOException("Invalid response from GCM: " + responseBody)
  }

  def newIoException(responseBody: String, e: Exception): IOException = {
    // log exception, as IOException constructor that takes a message and cause
    // is only available on Java 6
    val msg: String = "Error parsing JSON response (" + responseBody + ")"
    logger.log(Level.WARNING, msg, e)
    new IOException(msg + ":" + e)
  }

  def close(closeable: Closeable) = {
    if (closeable != null) {
      try {
        closeable.close()
      } catch {
        // ignore error
        case e: IOException => logger.log(Level.FINEST, "IOException closing stream", e)
      }
    }
  }

  /**
   * Sets a JSON field, but only if the value is not {@literal null}.
   */
  def setJsonField(json: Map[Object, Object], field: String, value: Object) = { if (value != null) json.put(field, value) }

  def getNumber(json: Map[AnyRef, AnyRef], field: String): Number = {
    val value: Object = json.get(field)
    if (value == null)
      throw new CustomParserException("Missing field: " + field)
    if (!(value.isInstanceOf[Number]))
      throw new CustomParserException("Field " + field + " does not contain a number: " + value)
    value.asInstanceOf[Number]
  }

  class CustomParserException(message: String) extends RuntimeException(message: String) {}

  def split(line: String): Array[String] = {
    val split: Array[String] = line.split("=", 2)
    if (split.length != 2)
      throw new IOException("Received invalid response line from GCM: " + line)
    return split
  }

  /**
   * Make an HTTP post to a given URL.
   *
   * @return HTTP response.
   */
  def post(url: String, body: String): HttpURLConnection = post(url, "application/x-www-form-urlencoded;charset=UTF-8", body)

  /**
   * Makes an HTTP POST request to a given endpoint.
   *
   * <p>
   * <strong>Note: </strong> the returned connected should not be disconnected,
   * otherwise it would kill persistent connections made using Keep-Alive.
   *
   * @param url endpoint to post the request.
   * @param contentType type of request.
   * @param body body of the request.
   *
   * @return the underlying connection.
   *
   * @throws IOException propagated from underlying methods.
   */
  def post(url: String, contentType: String, body: String): HttpURLConnection = {
    if (url == null || body == null)
      throw new IllegalArgumentException("arguments cannot be null")
    if (!url.startsWith("https://"))
      logger.warning("URL does not use https: " + url)
    logger.fine("Sending POST to " + url)
    logger.finest("POST body: " + body)
    val bytes: Array[Byte] = body.getBytes()
    val conn: HttpURLConnection = getConnection(url)
    conn.setDoOutput(true)
    conn.setUseCaches(false)
    conn.setFixedLengthStreamingMode(bytes.length)
    conn.setRequestMethod("POST")
    conn.setRequestProperty("Content-Type", contentType)
    conn.setRequestProperty("Authorization", "key=" + key)
    val out: OutputStream = conn.getOutputStream()
    try {
      out.write(bytes)
    } finally {
      close(out)
    }
    return conn
  }

  /**
   * Creates a map with just one key-value pair.
   */
  def newKeyValues(key: String, value: String): Map[String, String] = {
    val keyValues: Map[String, String] = new HashMap[String, String](1)
    keyValues.put(NonNull.nonNull(key), NonNull.nonNull(value))
    return keyValues
  }

  /**
   * Creates a {@link StringBuilder} to be used as the body of an HTTP POST.
   *
   * @param name initial parameter for the POST.
   * @param value initial value for that parameter.
   * @return StringBuilder to be used an HTTP POST body.
   */
  def newBody(name: String, value: String): StringBuilder = new StringBuilder(NonNull.nonNull(name)).append('=').append(NonNull.nonNull(value))

  /**
   * Adds a new parameter to the HTTP POST body.
   *
   * @param body HTTP POST body.
   * @param name parameter's name.
   * @param value parameter's value.
   */
  def addParameter(body: StringBuilder, name: String,
    value: String) = NonNull.nonNull(body).append('&').append(NonNull.nonNull(name)).append('=').append(NonNull.nonNull(value))

  /**
   * Gets an {@link HttpURLConnection} given an URL.
   */
  def getConnection(url: String): HttpURLConnection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]

  /**
   * Convenience method to convert an InputStream to a String.
   * <p>
   * If the stream ends in a newline character, it will be stripped.
   * <p>
   * If the stream is {@literal null}, returns an empty string.
   */
  def getString(stream: InputStream): String = {
    if (stream == null)
      return ""
    val reader: BufferedReader = new BufferedReader(new InputStreamReader(stream))
    val content: StringBuilder = new StringBuilder()
    var newLine: String = null
    do {
      newLine = reader.readLine();
      if (newLine != null)
        content.append(newLine).append('\n')
    } while (newLine != null)
    if (content.length() > 0)
      // strip last newline
      content.setLength(content.length() - 1)
    return content.toString()
  }

  def getAndClose(stream: InputStream): String = {
    try {
      getString(stream)
    } finally {
      if (stream != null) {
        close(stream)
      }
    }
  }
  object NonNull {
    def nonNull[T](argument: T): T = {
      if (argument == null) {
        throw new IllegalArgumentException("argument cannot be null")
      }
      return argument
    }
  }

  def sleep(millis: Long) {
    try {
      Thread.sleep(millis)
    } catch {
      case e: InterruptedException => Thread.currentThread().interrupt()
    }
  }

}