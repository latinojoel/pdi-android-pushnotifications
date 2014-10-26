package com.latinojoel.gcm.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a GCM Multicast message request.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
public final class MulticastResult implements Serializable {

  /** The serial Version. **/
  private static final long serialVersionUID = 3644618269694693013L;

  private final int success;
  private final int failure;
  private final int canonicalIds;
  private final long multicastId;
  private final List<Result> results;
  private final List<Long> retryMulticastIds;

  /**
   * The GCM MulticastResult builder class.
   * 
   * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
   * @version $Revision: 666 $
   * 
   */
  public static final class Builder {

    private final List<Result> results = new ArrayList<Result>();

    // required parameters
    private final int success;
    private final int failure;
    private final int canonicalIds;
    private final long multicastId;

    // optional parameters
    private List<Long> retryMulticastIds;

    public Builder(int success, int failure, int canonicalIds, long multicastId) {
      this.success = success;
      this.failure = failure;
      this.canonicalIds = canonicalIds;
      this.multicastId = multicastId;
    }

    /**
     * Adds result.
     * 
     * @param result the result.
     * @return the MulticastResult builder.
     */
    public Builder addResult(Result result) {
      results.add(result);
      return this;
    }

    /**
     * Adds retry multicast ids.
     * 
     * @param retryMulticastIds the list of retry multicast ids.
     * @return the MulticastResult builder.
     */
    public Builder retryMulticastIds(List<Long> retryMulticastIds) {
      this.retryMulticastIds = retryMulticastIds;
      return this;
    }

    /**
     * 
     * Builds the GCM MulticastResult.
     * 
     * @return the GCM MulticastResult.
     */
    public MulticastResult build() {
      return new MulticastResult(this);
    }
  }

  private MulticastResult(Builder builder) {
    success = builder.success;
    failure = builder.failure;
    canonicalIds = builder.canonicalIds;
    multicastId = builder.multicastId;
    results = Collections.unmodifiableList(builder.results);
    List<Long> tmpList = builder.retryMulticastIds;
    if (tmpList == null) {
      tmpList = Collections.emptyList();
    }
    retryMulticastIds = Collections.unmodifiableList(tmpList);
  }

  /**
   * Gets the multicast id.
   * 
   * @return the multicast id.
   */
  public long getMulticastId() {
    return multicastId;
  }

  /**
   * Gets the number of successful messages.
   * 
   * @return the number of successful messages.
   */
  public int getSuccess() {
    return success;
  }

  /**
   * Gets the total number of messages sent, regardless of the status.
   * 
   * @return the total number of messages sent.
   */
  public int getTotal() {
    return success + failure;
  }

  /**
   * Gets the number of failed messages.
   * 
   * @return the number of failed messages.
   */
  public int getFailure() {
    return failure;
  }

  /**
   * Gets the number of successful messages that also returned a canonical registration id.
   * 
   * @return the canonical registration id.
   */
  public int getCanonicalIds() {
    return canonicalIds;
  }

  /**
   * Gets the results of each individual message, which is immutable.
   * 
   * @return the results of each individual message.
   */
  public List<Result> getResults() {
    return results;
  }

  /**
   * Gets additional ids if more than one multicast message was sent.
   * 
   * @return the multicast ids.
   */
  public List<Long> getRetryMulticastIds() {
    return retryMulticastIds;
  }

  @Override
  public String toString() {
    final StringBuilder builder =
        new StringBuilder("MulticastResult(").append("multicast_id=").append(multicastId)
            .append(",").append("total=").append(getTotal()).append(",").append("success=")
            .append(success)
            .append(",").append("failure=").append(failure).append(",").append("canonical_ids=")
            .append(canonicalIds).append(",");
    if (!results.isEmpty()) {
      builder.append("results: " + results);
    }
    return builder.toString();
  }

}
