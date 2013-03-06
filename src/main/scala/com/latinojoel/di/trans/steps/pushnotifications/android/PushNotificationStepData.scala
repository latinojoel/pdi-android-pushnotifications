package com.latinojoel.di.trans.steps.pushnotifications.android

import org.pentaho.di.trans.step.BaseStepData
import org.pentaho.di.trans.step.StepDataInterface
import org.pentaho.di.core.row.RowMetaInterface
import scala.collection.mutable.ListBuffer

/**
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 */
class PushNotificationStepData() extends BaseStepData() with StepDataInterface {
  var outputRowMeta: RowMetaInterface = _
  var insertRowMeta: RowMetaInterface = _
  var valuenrs: ListBuffer[Int] = _
  var fieldnr: Int = _
  var NrPrevFields: Int = 0

  var indexOfRegistrationIdField: Int = -1
  var collapseKey: String = _
  var restrictedPackageName: String = _
  var apiKey: String = _
  var timeToLive: Int = 0
  var retryNumber: Int = 0
  var delayBeforeLastRetry: Int = 0
}