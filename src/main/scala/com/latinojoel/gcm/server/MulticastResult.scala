package com.latinojoel.gcm.server

import java.util.ArrayList
import java.util.List
import java.util.Collections

/**
 * Result of a GCM multicast message request.
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 */
@serializable
class MulticastResult(builder: MulticastResult#Builder) {

  private var success: Int = _;
  private var failure: Int = _;
  private var canonicalIds: Int = _;
  private var multicastId: Long = _;
  private var results: List[Result] = _;
  private var retryMulticastIds: List[Long] = _;

  class Builder(_success: Int, _failure: Int, _canonicalIds: Int,
      _multicastId: Long) {

    var results: List[Result] = new ArrayList[Result]()

    // required parameters
    var success: Int = _success
    var failure: Int = _failure
    var canonicalIds: Int = _canonicalIds
    var multicastId: Long = _multicastId

    // optional parameters
    var retryMulticastIds: List[Long] = _

    def addResult(result: Result): Builder = {
      results.add(result)
      this
    }

    def retryMulticastIds(retryMulticastIds: List[Long]): Builder = {
      this.retryMulticastIds = retryMulticastIds
      this
    }

    def build(): MulticastResult = new MulticastResult(this)

  }

  def MulticastResult(builder: Builder) = {
    success = builder.success;
    failure = builder.failure;
    canonicalIds = builder.canonicalIds;
    multicastId = builder.multicastId;
    results = Collections.unmodifiableList(builder.results);
    var tmpList: List[Long] = builder.retryMulticastIds;
    if (tmpList == null) {
      tmpList = Collections.emptyList();
    }
    retryMulticastIds = Collections.unmodifiableList(tmpList);
  }

  /**
   * Gets the multicast id.
   */
  def getMulticastId(): Long = multicastId

  /**
   * Gets the number of successful messages.
   */
  def getSuccess(): Int = success

  /**
   * Gets the total number of messages sent, regardless of the status.
   */
  def getTotal(): Int = success + failure

  /**
   * Gets the number of failed messages.
   */
  def getFailure(): Int = failure

  /**
   * Gets the number of successful messages that also returned a canonical
   * registration id.
   */
  def getCanonicalIds(): Int = canonicalIds

  /**
   * Gets the results of each individual message, which is immutable.
   */
  def getResults(): List[Result] = results

  /**
   * Gets additional ids if more than one multicast message was sent.
   */
  def getRetryMulticastIds(): List[Long] = retryMulticastIds

  override def toString(): String = {
    var builder: StringBuilder = new StringBuilder("MulticastResult(")
      .append("multicast_id=").append(multicastId).append(",")
      .append("total=").append(getTotal()).append(",")
      .append("success=").append(success).append(",")
      .append("failure=").append(failure).append(",")
      .append("canonical_ids=").append(canonicalIds).append(",");
    if (!results.isEmpty()) {
      builder.append("results: " + results);
    }
    return builder.toString();
  }

}