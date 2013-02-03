package com.latinojoel.di.trans.steps.pushnotifications.android

import org.pentaho.di.trans.step.StepMetaInterface
import org.pentaho.di.trans.step.BaseStepMeta
import org.pentaho.di.core.Const
import org.pentaho.di.core.row.RowMetaInterface
import org.pentaho.di.core.variables.VariableSpace
import org.pentaho.di.trans.step.StepMeta
import org.pentaho.di.core.row.ValueMetaInterface
import org.pentaho.di.core.row.ValueMeta
import org.w3c.dom.Node
import org.pentaho.di.core.Counter
import org.pentaho.di.core.database.DatabaseMeta
import org.pentaho.di.core.xml.XMLHandler
import org.pentaho.di.core.exception.KettleXMLException
import org.pentaho.di.core.CheckResultInterface._
import org.pentaho.di.trans.TransMeta
import org.pentaho.di.core.CheckResult
import org.pentaho.di.trans.step.StepDialogInterface
import org.eclipse.swt.widgets.Shell
import org.pentaho.di.trans.Trans
import org.pentaho.di.trans.step.StepDataInterface
import org.pentaho.di.trans.step.StepInterface
import org.pentaho.di.repository.ObjectId
import org.pentaho.di.repository.Repository
import org.pentaho.di.core.exception.KettleException
import org.pentaho.di.i18n.BaseMessages
import java.util.Map
import scala.collection.mutable.ListBuffer
import java.util.List
import org.pentaho.di.core.CheckResultInterface

class PushNotificationStepMeta extends BaseStepMeta with StepMetaInterface {
  def PKG: Class[PushNotificationStepMeta] = classOf[PushNotificationStepMeta] // for i18n purposes
  private var registrationId: String = _
  private var collapseKey: String = _
  private var delayWhileIdle: Boolean = false
  private var timeToLive: String = _
  private var restrictedPackageName: String = _
  private var dryRun: Boolean = false
  /** Fields containing the values in the input stream to send push notification */
  private var fieldStream: ListBuffer[String] = _
  /** Data fields to send push notification */
  private var dataFieldPush: ListBuffer[String] = _
  private var apiKey: String = _
  private var jsonResponseField: String = _
  private var pushEncoding: String = _
  private var retrying: Boolean = false
  private var retryNumber: String = _
  private var delayBeforeLastRetry: String = _

  def getRegistrationId(): String = this.registrationId
  def setRegistrationId(registrationId: String) = this.registrationId = registrationId

  def getCollapseKey(): String = this.collapseKey
  def setCollapseKey(collapseKey: String) = this.collapseKey = collapseKey

  def isDelayWhileIdle(): Boolean = this.delayWhileIdle
  def setDelayWhileIdle(delayWhileIdle: Boolean) = this.delayWhileIdle = delayWhileIdle

  def getTimeToLive(): String = this.timeToLive
  def setTimeToLive(timeToLive: String) = this.timeToLive = timeToLive

  def getRestrictedPackageName(): String = this.restrictedPackageName
  def setRestrictedPackageName(restrictedPackageName: String) = this.restrictedPackageName = restrictedPackageName

  def isDryRun(): Boolean = this.dryRun
  def setDryRun(dryRun: Boolean) = this.dryRun = dryRun

  def getFieldStream(): ListBuffer[String] = this.fieldStream
  def setFieldStream(fieldStream: ListBuffer[String]) = this.fieldStream = fieldStream

  def getDataFieldPush(): ListBuffer[String] = this.dataFieldPush
  def setDataFieldPush(dataFieldPush: ListBuffer[String]) = this.dataFieldPush = dataFieldPush

  def getApiKey(): String = this.apiKey
  def setApiKey(apiKey: String) = this.apiKey = apiKey

  def getJsonResponseField(): String = this.jsonResponseField
  def setJsonResponseField(jsonResponseField: String) = this.jsonResponseField = jsonResponseField

  def getPushEncoding(): String = this.pushEncoding
  def setPushEncoding(pushEncoding: String) = this.pushEncoding = pushEncoding

  def isRetrying(): Boolean = this.retrying
  def setRetrying(retrying: Boolean) = this.retrying = retrying

  def getRetryNumber(): String = this.retryNumber
  def setRetryNumber(retryNumber: String) = this.retryNumber = retryNumber

  def getDelayBeforeLastRetry(): String = this.delayBeforeLastRetry
  def setDelayBeforeLastRetry(delayBeforeLastRetry: String) = this.delayBeforeLastRetry = delayBeforeLastRetry

  override def getXML(): String = {
    val retval: StringBuilder = new StringBuilder()
    retval.append("    " + XMLHandler.addTagValue("registrationId", registrationId))
    retval.append("    " + XMLHandler.addTagValue("collapseKey", collapseKey))
    retval.append("    " + XMLHandler.addTagValue("delayWhileIdle", delayWhileIdle))
    retval.append("    " + XMLHandler.addTagValue("timeToLive", timeToLive))
    retval.append("    " + XMLHandler.addTagValue("restrictedPackageName", restrictedPackageName))
    retval.append("    " + XMLHandler.addTagValue("dryRun", dryRun))
    retval.append("    " + XMLHandler.addTagValue("apiKey", apiKey))
    retval.append("    " + XMLHandler.addTagValue("jsonResponseField", jsonResponseField))
    retval.append("    " + XMLHandler.addTagValue("pushEncoding", pushEncoding))
    retval.append("    " + XMLHandler.addTagValue("retrying", retrying))
    retval.append("    " + XMLHandler.addTagValue("retryNumber", retryNumber))
    retval.append("    " + XMLHandler.addTagValue("delayBeforeLastRetry", delayBeforeLastRetry))

    retval.append("    <fields>").append(Const.CR)
    var i: Int = 0
    while (i < dataFieldPush.length) {
      retval.append("        <field>").append(Const.CR)
      retval.append("          ").append(XMLHandler.addTagValue("dataFieldPush", dataFieldPush.apply(i)))
      retval.append("          ").append(XMLHandler.addTagValue("fieldStream", fieldStream.apply(i)))
      retval.append("        </field>").append(Const.CR)
      i += 1
    }
    retval.append("    </fields>").append(Const.CR)

    retval.toString()
  }

  def readRep(rep: Repository, id_step: ObjectId, databases: List[DatabaseMeta], counters: Map[String, Counter]) = {
    try {
      registrationId = rep.getStepAttributeString(id_step, "registrationId")
      collapseKey = rep.getStepAttributeString(id_step, "collapseKey")
      delayWhileIdle = rep.getStepAttributeBoolean(id_step, "delayWhileIdle")
      timeToLive = rep.getStepAttributeString(id_step, "timeToLive")
      restrictedPackageName = rep.getStepAttributeString(id_step, "restrictedPackageName")
      dryRun = rep.getStepAttributeBoolean(id_step, "dryRun")
      apiKey = rep.getStepAttributeString(id_step, "apiKey")
      jsonResponseField = rep.getStepAttributeString(id_step, "jsonResponseField")
      pushEncoding = rep.getStepAttributeString(id_step, "pushEncoding")
      retrying = rep.getStepAttributeBoolean(id_step, "retrying")
      retryNumber = rep.getStepAttributeString(id_step, "retryNumber")
      delayBeforeLastRetry = rep.getStepAttributeString(id_step, "delayBeforeLastRetry")

      val nrCols: Int = rep.countNrStepAttributes(id_step, "dataFieldPush")
      val nrStreams: Int = rep.countNrStepAttributes(id_step, "fieldStream")
      val nrRows: Int = if (nrCols < nrStreams) nrStreams else nrCols
      fieldStream = new ListBuffer[String]
      dataFieldPush = new ListBuffer[String]
      var i: Int = 0
      while (i < nrRows) {
        fieldStream += Const.NVL(rep.getStepAttributeString(id_step, i, "fieldStream"), "")
        dataFieldPush += Const.NVL(rep.getStepAttributeString(id_step, i, "dataFieldPush"), "")
        i += 1
      }
    } catch {
      case e: Exception => throw new KettleException(BaseMessages.getString(PKG, "AndroidPushNotification.Exception.UnexpectedErrorInReadingStepInfo"), e)
    }
  }

  def saveRep(rep: Repository, id_transformation: ObjectId, id_step: ObjectId) = {
    try {
      rep.saveStepAttribute(id_transformation, id_step, "registrationId", registrationId)
      rep.saveStepAttribute(id_transformation, id_step, "collapseKey", collapseKey)
      rep.saveStepAttribute(id_transformation, id_step, "delayWhileIdle", delayWhileIdle)
      rep.saveStepAttribute(id_transformation, id_step, "timeToLive", timeToLive)
      rep.saveStepAttribute(id_transformation, id_step, "restrictedPackageName", restrictedPackageName)
      rep.saveStepAttribute(id_transformation, id_step, "dryRun", dryRun)
      rep.saveStepAttribute(id_transformation, id_step, "apiKey", apiKey)
      rep.saveStepAttribute(id_transformation, id_step, "jsonResponseField", jsonResponseField)
      rep.saveStepAttribute(id_transformation, id_step, "pushEncoding", pushEncoding)
      rep.saveStepAttribute(id_transformation, id_step, "retrying", retrying)
      rep.saveStepAttribute(id_transformation, id_step, "retryNumber", retryNumber)
      rep.saveStepAttribute(id_transformation, id_step, "delayBeforeLastRetry", delayBeforeLastRetry)

      val nrRows: Int = if (fieldStream.length < dataFieldPush.length) dataFieldPush.length else fieldStream.length
      var i: Int = 0
      while (i < nrRows) {
        rep.saveStepAttribute(id_transformation, id_step, i, "fieldStream", if (i < fieldStream.length) fieldStream.apply(i) else "")
        rep.saveStepAttribute(id_transformation, id_step, i, "dataFieldPush", if (i < dataFieldPush.length) dataFieldPush.apply(i) else "")
        i += 1
      }
    } catch {
      case e: Exception => throw new KettleException(BaseMessages.getString(PKG, "TemplateStep.Exception.UnableToSaveStepInfoToRepository") + id_step, e)
    }
  }

  override def getFields(r: RowMetaInterface, origin: String, info: Array[RowMetaInterface], nextStep: StepMeta, space: VariableSpace) = {
    // Just add the json response field...
    if (jsonResponseField != null) {
      val key: ValueMetaInterface = new ValueMeta(space.environmentSubstitute(jsonResponseField), ValueMetaInterface.TYPE_STRING);
      key.setOrigin(origin);
      r.addValueMeta(key);
    }
  }

  override def clone(): Object = super.clone()

  def loadXML(stepnode: Node, databases: List[DatabaseMeta], counters: Map[String, Counter]) = readData(stepnode)

  def readData(stepnode: Node) = {
    try {
      registrationId = XMLHandler.getTagValue(stepnode, "registrationId")
      collapseKey = XMLHandler.getTagValue(stepnode, "collapseKey")
      delayWhileIdle = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "delayWhileIdle"))
      timeToLive = XMLHandler.getTagValue(stepnode, "timeToLive")
      restrictedPackageName = XMLHandler.getTagValue(stepnode, "restrictedPackageName")
      dryRun = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "dryRun"))
      apiKey = XMLHandler.getTagValue(stepnode, "apiKey")
      jsonResponseField = XMLHandler.getTagValue(stepnode, "jsonResponseField")
      pushEncoding = XMLHandler.getTagValue(stepnode, "pushEncoding")
      retrying = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "retrying"))
      retryNumber = XMLHandler.getTagValue(stepnode, "retryNumber")
      delayBeforeLastRetry = XMLHandler.getTagValue(stepnode, "delayBeforeLastRetry")

      val fields: Node = XMLHandler.getSubNode(stepnode, "fields")
      val nrRows: Int = XMLHandler.countNodes(fields, "field")
      fieldStream = new ListBuffer[String]
      dataFieldPush = new ListBuffer[String]
      var i: Int = 0
      while (i < nrRows) {
        val knode: Node = XMLHandler.getSubNodeByNr(fields, "field", i)
        fieldStream += XMLHandler.getTagValue(knode, "fieldStream")
        dataFieldPush += XMLHandler.getTagValue(knode, "dataFieldPush")
        i += 1
      }
    } catch {
      case e: Exception => throw new KettleException(BaseMessages.getString(PKG, "AndroidPushNotification.Exception.UnexpectedErrorInReadingStepInfo"), e)
    }
  }

  def setDefault() = {
    this.apiKey = ""
    this.collapseKey = ""
    this.dataFieldPush = new ListBuffer[String]
    this.delayBeforeLastRetry = ""
    this.delayWhileIdle = false
    this.dryRun = false
    this.fieldStream = new ListBuffer[String]
    this.jsonResponseField = "jsonResponse"
    this.pushEncoding = "UTF-8"
    this.registrationId = ""
    this.restrictedPackageName = ""
    this.retrying = false
    this.retryNumber = ""
    this.timeToLive = ""
  }

  override def check(remarks: List[CheckResultInterface], transmeta: TransMeta, stepMeta: StepMeta, prev: RowMetaInterface, input: Array[String], output: Array[String], info: RowMetaInterface): Unit = {
    var cr: CheckResult = null
    if (prev == null || prev.size() == 0) {
      cr = new CheckResult(CheckResultInterface.TYPE_RESULT_WARNING, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.NotReceivingFields"), stepMeta)
      remarks.add(cr)
    } else {
      cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.StepRecevingData", prev.size() + ""), stepMeta)
      remarks.add(cr)
      if (prev.indexOfValue(registrationId) < 0) {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.NoRegistrationIdFound"), stepMeta)
        remarks.add(cr)
      }
      if (retrying && retryNumber.equals(0)) {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.NoRetryNumberDefined"), stepMeta)
        remarks.add(cr)
      }
      if (retrying && delayBeforeLastRetry.equals(0)) {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.NoDelayBeforeLastRetryDefined"), stepMeta)
        remarks.add(cr)
      }

      var error_message: String = ""
      var error_found: Boolean = false;
      // Starting from selected fields in ...
      for (f <- fieldStream; if prev.indexOfValue(f) < 0) {
        error_message += "\t\t" + f + Const.CR
        error_found = true
      }
      if (error_found) {
        error_message = BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.FieldsFound", error_message)
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, error_message, stepMeta)
        remarks.add(cr)
      } else {
        if (fieldStream.length > 0) {
          cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.AllFieldsFound"), stepMeta)
          remarks.add(cr)
        } else {
          cr = new CheckResult(CheckResultInterface.TYPE_RESULT_WARNING, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.NoFieldsEntered"), stepMeta)
          remarks.add(cr)
        }
      }
      
      // See if we have input streams leading to this step!
      if (input.length > 0) {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.StepRecevingData2"), stepMeta)
        remarks.add(cr)
      } else {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.NoInputReceivedFromOtherSteps"), stepMeta)
        remarks.add(cr)
      }
    }
  }

  def getDialog(shell: Shell, meta: StepMetaInterface, transMeta: TransMeta, name: String): StepDialogInterface =
    new PushNotificationStepDialog(shell, meta.asInstanceOf[BaseStepMeta], transMeta, name)

  def getStep(stepMeta: StepMeta, stepDataInterface: StepDataInterface, cnr: Int, transMeta: TransMeta, disp: Trans): StepInterface =
    new PushNotificationStep(stepMeta, stepDataInterface, cnr, transMeta, disp)

  def getStepData(): StepDataInterface = new PushNotificationStepData()

}