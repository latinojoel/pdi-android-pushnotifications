package com.latinojoel.gcm.server;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GCM message.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
public final class Message implements Serializable {

  /** The serial Version. **/
  private static final long serialVersionUID = 7596924186109046906L;
  private final String collapseKey;
  private final Boolean delayWhileIdle;
  private final Integer timeToLive;
  private final Map<String, String> data;
  private final Boolean dryRun;
  private final String restrictedPackageName;

  /**
   * The GCM message builder class.
   * 
   * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
   * @version $Revision: 666 $
   * 
   */
  public static final class Builder {

    private final Map<String, String> data;

    // optional parameters
    private String collapseKey;
    private Boolean delayWhileIdle;
    private Integer timeToLive;
    private Boolean dryRun;
    private String restrictedPackageName;

    public Builder() {
      this.data = new LinkedHashMap<String, String>();
    }

    /**
     * Sets the collapseKey property.
     * 
     * @param value the collapseKey property.
     * @return the message builder.
     */
    public Builder collapseKey(String value) {
      collapseKey = value;
      return this;
    }

    /**
     * Sets the delayWhileIdle property (default value is {@literal false}).
     * 
     * @param value the delayWhileIdle property.
     * @return the message builder.
     */
    public Builder delayWhileIdle(boolean value) {
      delayWhileIdle = value;
      return this;
    }

    /**
     * Sets the time to live, in seconds.
     * 
     * @param value the time to live.
     * @return the message builder.
     */
    public Builder timeToLive(int value) {
      timeToLive = value;
      return this;
    }

    /**
     * Adds a key/value pair to the payload data.
     * 
     * @param key the key.
     * @param value the value.
     * @return the message builder.
     */
    public Builder addData(String key, String value) {
      data.put(key, value);
      return this;
    }

    /**
     * Sets the dryRun property (default value is {@literal false}).
     * 
     * @param value the dryRun property.
     * @return the message builder.
     */
    public Builder dryRun(boolean value) {
      dryRun = value;
      return this;
    }

    /**
     * Sets the restrictedPackageName property.
     * 
     * @param value the restrictedPackageName property.
     * @return the message builder.
     */
    public Builder restrictedPackageName(String value) {
      restrictedPackageName = value;
      return this;
    }

    /**
     * Builds the GCM message.
     * 
     * @return the GCM message.
     */
    public Message build() {
      return new Message(this);
    }

  }

  private Message(Builder builder) {
    collapseKey = builder.collapseKey;
    delayWhileIdle = builder.delayWhileIdle;
    data = Collections.unmodifiableMap(builder.data);
    timeToLive = builder.timeToLive;
    dryRun = builder.dryRun;
    restrictedPackageName = builder.restrictedPackageName;
  }

  /**
   * Gets the collapse key.
   * 
   * @return the collapse key.
   */
  public String getCollapseKey() {
    return collapseKey;
  }

  /**
   * Gets the delayWhileIdle flag.
   * 
   * @return the delayWhileIdle flag.
   */
  public Boolean isDelayWhileIdle() {
    return delayWhileIdle;
  }

  /**
   * Gets the time to live (in seconds).
   * 
   * @return the time to live (in seconds).
   */
  public Integer getTimeToLive() {
    return timeToLive;
  }

  /**
   * Gets the dryRun flag.
   * 
   * @return the dryRun flag.
   */
  public Boolean isDryRun() {
    return dryRun;
  }

  /**
   * Gets the restricted package name.
   * 
   * @return the restricted package name.
   */
  public String getRestrictedPackageName() {
    return restrictedPackageName;
  }

  /**
   * Gets the payload data, which is immutable.
   * 
   * @return the payload data.
   */
  public Map<String, String> getData() {
    return data;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder("Message(");
    if (collapseKey != null) {
      builder.append("collapseKey=").append(collapseKey).append(", ");
    }
    if (timeToLive != null) {
      builder.append("timeToLive=").append(timeToLive).append(", ");
    }
    if (delayWhileIdle != null) {
      builder.append("delayWhileIdle=").append(delayWhileIdle).append(", ");
    }
    if (dryRun != null) {
      builder.append("dryRun=").append(dryRun).append(", ");
    }
    if (restrictedPackageName != null) {
      builder.append("restrictedPackageName=").append(restrictedPackageName).append(", ");
    }
    if (!data.isEmpty()) {
      builder.append("data: {");
      for (Map.Entry<String, String> entry : data.entrySet()) {
        builder.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
      }
      builder.delete(builder.length() - 1, builder.length());
      builder.append("}");
    }
    if (builder.charAt(builder.length() - 1) == ' ') {
      builder.delete(builder.length() - 2, builder.length());
    }
    builder.append(")");
    return builder.toString();
  }

}
