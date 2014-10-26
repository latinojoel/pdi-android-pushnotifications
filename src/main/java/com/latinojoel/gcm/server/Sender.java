package com.latinojoel.gcm.server;

import static com.latinojoel.gcm.server.Constants.GCM_SEND_ENDPOINT;
import static com.latinojoel.gcm.server.Constants.PARAM_COLLAPSE_KEY;
import static com.latinojoel.gcm.server.Constants.PARAM_DELAY_WHILE_IDLE;
import static com.latinojoel.gcm.server.Constants.PARAM_DRY_RUN;
import static com.latinojoel.gcm.server.Constants.PARAM_PAYLOAD_PREFIX;
import static com.latinojoel.gcm.server.Constants.PARAM_REGISTRATION_ID;
import static com.latinojoel.gcm.server.Constants.PARAM_RESTRICTED_PACKAGE_NAME;
import static com.latinojoel.gcm.server.Constants.PARAM_TIME_TO_LIVE;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to send messages to the GCM service using an API Key.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
public class Sender {

  protected static final String UTF8 = "UTF-8";

  protected static final Logger LOGGER = Logger.getLogger(Sender.class.getName());

  private final String key;

  /**
   * Default constructor.
   * 
   * @param apiKey API key obtained through the Google API Console.
   */
  public Sender(String apiKey) {
    this.key = nonNull(apiKey);
  }

  /**
   * Sends a message to one device, retrying in case of unavailability.
   * 
   * @param message message to be sent, including the device's registration id.
   * @param registrationId device where the message will be sent.
   * @param retries number of retries in case of service unavailability errors.
   * @param backoffDelay the number of milliseconds back off delay.
   * 
   * @return result of the request
   * @throws IOException the IO exception.
   */
  public String send(Message message, String registrationId, Integer retries, Integer backoffDelay)
      throws IOException {
    Integer attempt = 0;
    String result = null;
    boolean tryAgain = false;
    do {
      attempt++;
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.fine("Attempt #" + attempt + " to send message " + message + " to regIds "
            + registrationId);
      }
      result = sendNoRetry(message, registrationId);
      tryAgain = result == null && attempt <= retries;
      if (tryAgain) {
        sleep(backoffDelay);
      }
    } while (tryAgain);
    if (result == null) {
      throw new IOException("Could not send message after " + attempt + " attempts");
    }

    return result;
  }

  /**
   * Sends a message without retrying in case of service unavailability.
   * 
   * @param message the GCM message.
   * @param registrationId the registration id.
   * @return the response body.
   * @throws UnsupportedEncodingException the unsupported encoding exception.
   * @throws InvalidRequestException the invalid request exception.
   */
  public String sendNoRetry(Message message, String registrationId)
      throws UnsupportedEncodingException,
      InvalidRequestException {
    final StringBuilder body = newBody(PARAM_REGISTRATION_ID, registrationId);
    final boolean delayWhileIdle = message.isDelayWhileIdle();
    addParameter(body, PARAM_DELAY_WHILE_IDLE, delayWhileIdle ? "1" : "0");
    final boolean dryRun = message.isDryRun();
    addParameter(body, PARAM_DRY_RUN, dryRun ? "1" : "0");
    final String collapseKey = message.getCollapseKey();
    if (collapseKey != null) {
      addParameter(body, PARAM_COLLAPSE_KEY, collapseKey);
    }

    final String restrictedPackageName = message.getRestrictedPackageName();
    if (restrictedPackageName != null) {
      addParameter(body, PARAM_RESTRICTED_PACKAGE_NAME, restrictedPackageName);
    }

    final Integer timeToLive = message.getTimeToLive();
    addParameter(body, PARAM_TIME_TO_LIVE, Integer.toString(timeToLive));
    for (Entry<String, String> entry : message.getData().entrySet()) {
      String key = entry.getKey();
      final String value = entry.getValue();
      if (key == null || value == null) {
        LOGGER.warning("Ignoring payload entry thas has null: " + entry);
      } else {
        key = PARAM_PAYLOAD_PREFIX + key;
        addParameter(body, key, URLEncoder.encode(value, UTF8));
      }
    }
    final String requestBody = body.toString();
    LOGGER.finest("Request body: " + requestBody);
    HttpURLConnection conn = null;
    Integer status = 0;
    try {
      conn = post(GCM_SEND_ENDPOINT, requestBody);
      status = conn.getResponseCode();
    } catch (IOException e) {
      LOGGER.log(Level.FINE, "IOException posting to GCM", e);
      return null;
    }
    if (status / 100 == 5) {
      LOGGER.fine("GCM service is unavailable (status " + status + ")");
      return "GCM service is unavailable (status " + status + ")";
    }
    String responseBody = "";
    if (status != 200) {
      try {
        responseBody = getAndClose(conn.getErrorStream());
        LOGGER.finest("Plain post error response: " + responseBody);
      } catch (IOException e) {
        // ignore the exception since it will thrown an
        // InvalidRequestException
        // anyways
        responseBody = "N/A";
        LOGGER.log(Level.FINE, "Exception reading response: ", e);
      }
      throw new InvalidRequestException(status, responseBody);
    } else {
      try {
        responseBody = getAndClose(conn.getInputStream());
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Exception reading response: ", e);
        // return null so it can retry
        return "Exception reading response: " + e.getMessage();
      }
    }

    return responseBody;
  }

  /**
   * Close the connection.
   * 
   * @param closeable the closeable.
   */
  private static void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        // ignore error
        LOGGER.log(Level.FINEST, "IOException closing stream", e);
      }
    }
  }

  /**
   * Make an HTTP post to a given URL.
   * 
   * @param url the URL.
   * @param body the body of HTTP post.
   * @return HTTP response.
   * @throws IOException the IO exception.
   */
  protected HttpURLConnection post(String url, String body) throws IOException {
    return post(url, "application/x-www-form-urlencoded;charset=UTF-8", body);
  }

  /**
   * Makes an HTTP POST request to a given endpoint.
   * 
   * <p>
   * <strong>Note: </strong> the returned connected should not be disconnected, otherwise it would
   * kill persistent connections made using Keep-Alive.
   * 
   * @param url endpoint to post the request.
   * @param contentType type of request.
   * @param body body of the request.
   * 
   * @return the underlying connection.
   * 
   * @throws IOException propagated from underlying methods.
   */
  protected HttpURLConnection post(String url, String contentType, String body) throws IOException {
    if (url == null || body == null) {
      throw new IllegalArgumentException("arguments cannot be null");
    }
    if (!url.startsWith("https://")) {
      LOGGER.warning("URL does not use https: " + url);
    }
    LOGGER.fine("Sending POST to " + url);
    LOGGER.finest("POST body: " + body);
    final byte[] bytes = body.getBytes();
    final HttpURLConnection conn = getConnection(url);
    conn.setDoOutput(true);
    conn.setUseCaches(false);
    conn.setFixedLengthStreamingMode(bytes.length);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", contentType);
    conn.setRequestProperty("Authorization", "key=" + key);
    final OutputStream out = conn.getOutputStream();
    try {
      out.write(bytes);
    } finally {
      close(out);
    }
    return conn;
  }

  /**
   * Creates a map with just one key-value pair.
   * 
   * @param key the key.
   * @param value the value.
   * @return the map.
   */
  protected static final Map<String, String> newKeyValues(String key, String value) {
    final Map<String, String> keyValues = new HashMap<String, String>(1);
    keyValues.put(nonNull(key), nonNull(value));
    return keyValues;
  }

  /**
   * Creates a {@link StringBuilder} to be used as the body of an HTTP POST.
   * 
   * @param name initial parameter for the POST.
   * @param value initial value for that parameter.
   * @return StringBuilder to be used an HTTP POST body.
   */
  protected static StringBuilder newBody(String name, String value) {
    return new StringBuilder(nonNull(name)).append('=').append(nonNull(value));
  }

  /**
   * Adds a new parameter to the HTTP POST body.
   * 
   * @param body HTTP POST body.
   * @param name parameter's name.
   * @param value parameter's value.
   */
  protected static void addParameter(StringBuilder body, String name, String value) {
    nonNull(body).append('&').append(nonNull(name)).append('=').append(nonNull(value));
  }

  /**
   * Gets an {@link HttpURLConnection} given an URL.
   * 
   * @param url the URL.
   * @return the HTTP connection.
   * @throws IOException the IO exception.
   */
  protected HttpURLConnection getConnection(String url) throws IOException {
    final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    return conn;
  }

  /**
   * Convenience method to convert an InputStream to a String.
   * <p>
   * If the stream ends in a newline character, it will be stripped.
   * <p>
   * If the stream is {@literal null}, returns an empty string.
   * 
   * @param stream the stream.
   * @return the string of stream.
   * @throws IOException the IO exception.
   */
  protected static String getString(InputStream stream) throws IOException {
    if (stream == null) {
      return "";
    }
    final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    final StringBuilder content = new StringBuilder();
    String newLine = null;
    do {
      newLine = reader.readLine();
      if (newLine != null) {
        content.append(newLine).append('\n');
      }
    } while (newLine != null);
    if (content.length() > 0) {
      // strip last newline
      content.setLength(content.length() - 1);
    }
    return content.toString();
  }

  /**
   * Gets the string of stream and close the stream.
   * 
   * @param stream the stream.
   * @return the string of stream.
   * @throws IOException the IO exception.
   */
  private static String getAndClose(InputStream stream) throws IOException {
    try {
      return getString(stream);
    } finally {
      if (stream != null) {
        close(stream);
      }
    }
  }

  /**
   * Checks the argument is not null.
   * 
   * @param argument the argument.
   * @param <T> the generic object.
   * @return the argument checked.
   */
  static <T> T nonNull(T argument) {
    if (argument == null) {
      throw new IllegalArgumentException("argument cannot be null");
    }
    return argument;
  }

  /**
   * Threads sleeps by a number of milliseconds.
   * 
   * @param millis the milliseconds .
   */
  void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}
