package com.latinojoel.gcm.server

import scala.collection.JavaConversions._
import java.io.Serializable
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.Map
import java.util.Collections
import java.lang.Integer

/**
 * GCM message.
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 */
@serializable
class Message {

  private var collapseKey: String = _
  private var delayWhileIdle: Boolean = _
  private var timeToLive: Int = _
  private var data: Map[String, String] = new LinkedHashMap[String, String]()
  private var dryRun: Boolean = _
  private var restrictedPackageName: String = _

  /**
   * Gets the collapse key.
   */
  def getCollapseKey(): String = collapseKey

  /**
   * Sets the collapseKey property.
   */
  def setCollapseKey(value: String) = collapseKey = value

  /**
   * Gets the delayWhileIdle flag.
   */
  def isDelayWhileIdle(): Boolean = delayWhileIdle

  /**
   * Sets the delayWhileIdle property (default value is {@literal false}).
   */
  def setDelayWhileIdle(value: Boolean) = delayWhileIdle = value

  /**
   * Gets the time to live (in seconds).
   */
  def getTimeToLive(): Int = timeToLive

  /**
   * Sets the time to live, in seconds.
   */
  def setTimeToLive(value: Int) = timeToLive = value

  /**
   * Gets the dryRun flag.
   */
  def isDryRun(): Boolean = dryRun

  /**
   * Sets the dryRun property (default value is {@literal false}).
   */
  def setDryRun(value: Boolean) = dryRun = value

  /**
   * Gets the restricted package name.
   */
  def getRestrictedPackageName(): String = restrictedPackageName

  /**
   * Sets the restrictedPackageName property.
   */
  def setRestrictedPackageName(value: String) = restrictedPackageName = value

  /**
   * Gets the payload data, which is immutable.
   */
  def getData(): Map[String, String] = data

  /**
   * Adds a key/value pair to the payload data.
   */
  def addData(key: String, value: String) = data.put(key, value)

  override def toString(): String = {
    var builder: StringBuilder = new StringBuilder("Message(")
    if (collapseKey != null || collapseKey.equals(""))
      builder.append("collapseKey=").append(collapseKey).append(", ")
    if (timeToLive != 0)
      builder.append("timeToLive=").append(timeToLive).append(", ")
    if (delayWhileIdle)
      builder.append("delayWhileIdle=").append(delayWhileIdle).append(", ")
    if (dryRun)
      builder.append("dryRun=").append(dryRun).append(", ")
    if (restrictedPackageName != null)
      builder.append("restrictedPackageName=").append(restrictedPackageName).append(", ")
    if (!data.isEmpty) {
      builder.append("data: {")
      for (entry <- data.entrySet) {
        builder.append(entry.getKey()).append("=").append(entry.getValue())
          .append(",")
      }
      builder.delete(builder.length - 1, builder.length)
      builder.append("}")
    }
    if (builder.charAt(builder.length - 1) == ' ') {
      builder.delete(builder.length - 2, builder.length);
    }
    builder.append(")")
    return builder.toString()
  }

}