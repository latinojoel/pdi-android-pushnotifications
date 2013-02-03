package com.latinojoel.di.trans.steps.pushnotifications.android

import org.pentaho.di.trans.step.BaseStep
import org.pentaho.di.trans.step.StepAttributesInterface
import org.pentaho.di.trans.step.StepDataInterface
import org.pentaho.di.trans.TransMeta
import org.pentaho.di.trans.Trans
import org.pentaho.di.trans.step.StepMeta
import org.pentaho.di.trans.step.StepMetaInterface
import org.pentaho.di.trans.step.StepInterface
import org.pentaho.di.core.row.RowDataUtil
import org.pentaho.di.core.Const
import scala.collection.mutable.ListBuffer
import org.pentaho.di.i18n.BaseMessages
import org.pentaho.di.core.exception.KettleStepException
import org.pentaho.di.core.row.RowMeta
import org.pentaho.di.core.row.ValueMetaInterface
import org.pentaho.di.core.row.RowMetaInterface
import com.latinojoel.gcm.server.Message
import com.latinojoel.gcm.server.Sender
import org.pentaho.di.core.exception.KettleException
import org.pentaho.di.core.row.ValueMeta
import org.pentaho.di.core.RowMetaAndData

class PushNotificationStep(s: StepMeta, stepDataInterface: StepDataInterface, c: Int, t: TransMeta, dis: Trans) extends BaseStep(s: StepMeta, stepDataInterface: StepDataInterface, c: Int, t: TransMeta, dis: Trans) with StepInterface {
  val PKG: Class[PushNotificationStepDialog] = classOf[PushNotificationStepDialog]
  var meta: PushNotificationStepMeta = _
  var data: PushNotificationStepData = _

  override def processRow(smi: StepMetaInterface, sdi: StepDataInterface): Boolean = {
    this.meta = smi.asInstanceOf[PushNotificationStepMeta]
    this.data = sdi.asInstanceOf[PushNotificationStepData]

    var r: Array[Object] = getRow()
    if (r == null) {
      setOutputDone()
      return false
    }

    if (first) {
      first = false
      data.outputRowMeta = super.getInputRowMeta()
      data.NrPrevFields = data.outputRowMeta.size()
      meta.getFields(data.outputRowMeta, getStepname(), null, null, this)

      data.insertRowMeta = new RowMeta()

      data.valuenrs = new ListBuffer[Int]
      var i: Int = 0
      while (i < meta.getDataFieldPush.length) {
        data.valuenrs += getInputRowMeta().indexOfValue(meta.getFieldStream.apply(i));
        if (data.valuenrs.apply(i) < 0) {
          throw new KettleStepException(BaseMessages.getString(PKG, "AndroidPushNotification.Exception.FieldRequired", meta.getFieldStream.apply(i))); //$NON-NLS-1$
        }
        i += 1
      }
    } // end if first

    var outputRow: Array[Object] = RowDataUtil.allocateRowData(data.outputRowMeta.size());
    var i: Int = 0
    while (i < data.NrPrevFields) {
      outputRow(i) = r.apply(i)
      i += 1
    }
    val res: String = sendPush(getInputRowMeta(), r)
    outputRow(data.NrPrevFields) = res

    putRow(data.outputRowMeta, outputRow);

    if (checkFeedback(getLinesRead())) {
      if (log.isBasic) logBasic("Linenr " + getLinesRead()) // Some basic logging
    }
    return true
  }

  private def sendPush(rowMeta: RowMetaInterface, r: Array[Object]): String = {
    if (r == null) // Stop: last line or error encountered 
    {
      if (log.isDetailed()) logDetailed("Last line inserted: stop")
      return null
    }

    // Cache the position of the registration id field
    if (data.indexOfRegistrationIdField < 0) {
      val realRegistrationId: String = environmentSubstitute(meta.getRegistrationId)
      data.indexOfRegistrationIdField = rowMeta.indexOfValue(realRegistrationId);
      if (data.indexOfRegistrationIdField < 0) {
        val message: String = "Unable to find table name field [" + realRegistrationId + "] in input row";
        logError(message);
        throw new KettleStepException(message);
      }
    }

    val message: Message = new Message
    message.setCollapseKey(data.collapseKey)
    message.setDelayWhileIdle(meta.isDelayWhileIdle)
    message.setDryRun(meta.isDryRun)
    message.setRestrictedPackageName(data.restrictedPackageName)
    message.setTimeToLive(data.timeToLive)
    var i: Int = 0
    while (i < meta.getDataFieldPush.length) {
      message.addData(meta.getFieldStream.apply(i), r.apply(data.valuenrs.apply(i)).toString())
      i += 1
    }

    val sender: Sender = new Sender(data.apiKey)
    var result: String = null
    if (meta.isRetrying)
      result = sender.send(message, rowMeta.getString(r, data.indexOfRegistrationIdField), data.delayBeforeLastRetry, data.retryNumber)
    else
      result = sender.sendNoRetry(message, rowMeta.getString(r, data.indexOfRegistrationIdField))
    return result

  }

  override def init(smi: StepMetaInterface, sdi: StepDataInterface): Boolean = {
    meta = smi.asInstanceOf[PushNotificationStepMeta]
    data = sdi.asInstanceOf[PushNotificationStepData]

    if (super.init(smi, sdi)) {
      try {
        if (meta.getTimeToLive ne null)
          data.timeToLive = Integer.parseInt(environmentSubstitute(meta.getTimeToLive))
        if (meta.getRetryNumber ne null)
          data.retryNumber = Integer.parseInt(environmentSubstitute(meta.getRetryNumber))
        if (meta.getDelayBeforeLastRetry != null)
          data.delayBeforeLastRetry = Integer.parseInt(environmentSubstitute(meta.getDelayBeforeLastRetry))
        if ((meta.getCollapseKey ne null) && !meta.getCollapseKey.equals(""))
          data.collapseKey = environmentSubstitute(meta.getCollapseKey)
        if (meta.getRestrictedPackageName != null && !meta.getRestrictedPackageName.equals(""))
          data.restrictedPackageName = environmentSubstitute(meta.getRestrictedPackageName)
        if ((meta.getApiKey ne null) && !meta.getApiKey.equals(""))
          data.apiKey = environmentSubstitute(meta.getApiKey)
        return true;
      } catch {
        case e: KettleException => {
          logError("An error occurred intialising this step: " + e.getMessage())
          stopAll()
          setErrors(1)
        }
      }
    }
    return false;
  }

  override def dispose(smi: StepMetaInterface, sdi: StepDataInterface) = super.dispose(smi, sdi)

  // Run is were the action happens!
  def run() = {
    logBasic("Starting to run...");
    try {
      while (processRow(meta, data) && !isStopped()) {}
    } catch {
      case e: Exception => {
        logError("Unexpected error : " + e.toString());
        logError(Const.getStackTracker(e));
        setErrors(1);
        stopAll();
      }
    } finally {
      dispose(meta, data);
      logBasic("Finished, processing " + getLinesRead() + " rows");
      markStop();
    }
  }
}