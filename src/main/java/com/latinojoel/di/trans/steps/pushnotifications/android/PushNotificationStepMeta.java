package com.latinojoel.di.trans.steps.pushnotifications.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

/**
 * This class is responsible for implementing functionality regarding step meta. All Kettle steps
 * have an extension of this where private fields have been added with public accessors.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
@Step(id = "AndroidPushNotification", name = "AndroidPushNotification.Step.Name",
    description = "AndroidPushNotification.Step.Description",
    categoryDescription = "AndroidPushNotification.Step.Category",
    image = "com/latinojoel/di/trans/steps/pushnotifications/android/push.png",
    i18nPackageName = "com.latinojoel.di.trans.steps.pushnotifications.android",
    casesUrl = "https://github.com/latinojoel", documentationUrl = "https://github.com/latinojoel",
    forumUrl = "https://github.com/latinojoel")
public class PushNotificationStepMeta extends BaseStepMeta implements StepMetaInterface {
  /** for i18n purposes. **/
  private static final Class<?> PKG = PushNotificationStepMeta.class;

  private String registrationId = null;
  private String collapseKey = null;
  private boolean delayWhileIdle = false;
  private String timeToLive = null;
  private String restrictedPackageName = null;
  private boolean dryRun = false;

  /** Fields containing the values in the input stream to send push notification. **/
  private List<String> fieldStream = null;

  /** Data fields to send push notification. **/
  private List<String> dataFieldPush = null;

  private String apiKey = null;
  private String responseField = null;
  private String pushEncoding = null;
  private boolean retrying = false;
  private String retryNumber = null;
  private String delayBeforeLastRetry = null;

  public String getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }

  public String getCollapseKey() {
    return collapseKey;
  }

  public void setCollapseKey(String collapseKey) {
    this.collapseKey = collapseKey;
  }

  public boolean isDelayWhileIdle() {
    return delayWhileIdle;
  }

  public void setDelayWhileIdle(boolean delayWhileIdle) {
    this.delayWhileIdle = delayWhileIdle;
  }

  public String getTimeToLive() {
    return timeToLive;
  }

  public void setTimeToLive(String timeToLive) {
    this.timeToLive = timeToLive;
  }

  public String getRestrictedPackageName() {
    return restrictedPackageName;
  }

  public void setRestrictedPackageName(String restrictedPackageName) {
    this.restrictedPackageName = restrictedPackageName;
  }

  public boolean isDryRun() {
    return dryRun;
  }

  public void setDryRun(boolean dryRun) {
    this.dryRun = dryRun;
  }

  public List<String> getFieldStream() {
    return fieldStream;
  }

  public void setFieldStream(List<String> fieldStream) {
    this.fieldStream = fieldStream;
  }

  public List<String> getDataFieldPush() {
    return dataFieldPush;
  }

  public void setDataFieldPush(List<String> dataFieldPush) {
    this.dataFieldPush = dataFieldPush;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getResponseField() {
    return responseField;
  }

  public void setResponseField(String responseField) {
    this.responseField = responseField;
  }

  public String getPushEncoding() {
    return pushEncoding;
  }

  public void setPushEncoding(String pushEncoding) {
    this.pushEncoding = pushEncoding;
  }

  public boolean isRetrying() {
    return retrying;
  }

  public void setRetrying(boolean retrying) {
    this.retrying = retrying;
  }

  public String getRetryNumber() {
    return retryNumber;
  }

  public void setRetryNumber(String retryNumber) {
    this.retryNumber = retryNumber;
  }

  public String getDelayBeforeLastRetry() {
    return delayBeforeLastRetry;
  }

  public void setDelayBeforeLastRetry(String delayBeforeLastRetry) {
    this.delayBeforeLastRetry = delayBeforeLastRetry;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getXML() {
    final StringBuilder retval = new StringBuilder();
    retval.append("    " + XMLHandler.addTagValue("registrationId", registrationId));
    retval.append("    " + XMLHandler.addTagValue("collapseKey", collapseKey));
    retval.append("    " + XMLHandler.addTagValue("delayWhileIdle", delayWhileIdle));
    retval.append("    " + XMLHandler.addTagValue("timeToLive", timeToLive));
    retval.append("    " + XMLHandler.addTagValue("restrictedPackageName", restrictedPackageName));
    retval.append("    " + XMLHandler.addTagValue("dryRun", dryRun));
    retval.append("    " + XMLHandler.addTagValue("apiKey", apiKey));
    retval.append("    " + XMLHandler.addTagValue("ResponseField", responseField));
    retval.append("    " + XMLHandler.addTagValue("pushEncoding", pushEncoding));
    retval.append("    " + XMLHandler.addTagValue("retrying", retrying));
    retval.append("    " + XMLHandler.addTagValue("retryNumber", retryNumber));
    retval.append("    " + XMLHandler.addTagValue("delayBeforeLastRetry", delayBeforeLastRetry));

    retval.append("    <fields>").append(Const.CR);
    for (int i = 0; i < dataFieldPush.size(); i++) {
      retval.append("        <field>").append(Const.CR);
      retval.append("          ").append(
          XMLHandler.addTagValue("dataFieldPush", dataFieldPush.get(i)));
      retval.append("          ").append(XMLHandler.addTagValue("fieldStream", fieldStream.get(i)));
      retval.append("        </field>").append(Const.CR);
    }
    retval.append("    </fields>").append(Const.CR);

    return retval.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KettleException
   */
  @Override
  public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases,
      Map<String, Counter> counters)
      throws KettleException {
    try {
      registrationId = rep.getStepAttributeString(idStep, "registrationId");
      collapseKey = rep.getStepAttributeString(idStep, "collapseKey");
      delayWhileIdle = rep.getStepAttributeBoolean(idStep, "delayWhileIdle");
      timeToLive = rep.getStepAttributeString(idStep, "timeToLive");
      restrictedPackageName = rep.getStepAttributeString(idStep, "restrictedPackageName");
      dryRun = rep.getStepAttributeBoolean(idStep, "dryRun");
      apiKey = rep.getStepAttributeString(idStep, "apiKey");
      responseField = rep.getStepAttributeString(idStep, "ResponseField");
      pushEncoding = rep.getStepAttributeString(idStep, "pushEncoding");
      retrying = rep.getStepAttributeBoolean(idStep, "retrying");
      retryNumber = rep.getStepAttributeString(idStep, "retryNumber");
      delayBeforeLastRetry = rep.getStepAttributeString(idStep, "delayBeforeLastRetry");

      final int nrCols = rep.countNrStepAttributes(idStep, "dataFieldPush");
      final int nrStreams = rep.countNrStepAttributes(idStep, "fieldStream");
      final int nrRows = (nrCols < nrStreams) ? nrStreams : nrCols;
      fieldStream = new ArrayList<String>();
      dataFieldPush = new ArrayList<String>();
      for (int i = 0; i < nrRows; i++) {
        fieldStream.add(Const.NVL(rep.getStepAttributeString(idStep, i, "fieldStream"), ""));
        dataFieldPush.add(Const.NVL(rep.getStepAttributeString(idStep, i, "dataFieldPush"), ""));
      }
    } catch (Exception e) {
      throw new KettleException(BaseMessages.getString(PKG,
          "AndroidPushNotification.Exception.UnexpectedErrorInReadingStepInfo"), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KettleException
   */
  @Override
  public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep)
      throws KettleException {
    try {
      rep.saveStepAttribute(idTransformation, idStep, "registrationId", registrationId);
      rep.saveStepAttribute(idTransformation, idStep, "collapseKey", collapseKey);
      rep.saveStepAttribute(idTransformation, idStep, "delayWhileIdle", delayWhileIdle);
      rep.saveStepAttribute(idTransformation, idStep, "timeToLive", timeToLive);
      rep.saveStepAttribute(idTransformation, idStep, "restrictedPackageName",
          restrictedPackageName);
      rep.saveStepAttribute(idTransformation, idStep, "dryRun", dryRun);
      rep.saveStepAttribute(idTransformation, idStep, "apiKey", apiKey);
      rep.saveStepAttribute(idTransformation, idStep, "ResponseField", responseField);
      rep.saveStepAttribute(idTransformation, idStep, "pushEncoding", pushEncoding);
      rep.saveStepAttribute(idTransformation, idStep, "retrying", retrying);
      rep.saveStepAttribute(idTransformation, idStep, "retryNumber", retryNumber);
      rep.saveStepAttribute(idTransformation, idStep, "delayBeforeLastRetry", delayBeforeLastRetry);

      final int nrRows =
          fieldStream.size() < dataFieldPush.size() ? dataFieldPush.size() : fieldStream.size();
      for (int i = 0; i < nrRows; i++) {
        rep.saveStepAttribute(idTransformation, idStep, i, "fieldStream",
            i < fieldStream.size() ? fieldStream.get(i) : "");
        rep.saveStepAttribute(idTransformation, idStep, i, "dataFieldPush",
            i < dataFieldPush.size() ? dataFieldPush.get(i) : "");
      }
    } catch (Exception e) {
      throw new KettleException(BaseMessages.getString(PKG,
          "TemplateStep.Exception.UnableToSaveStepInfoToRepository") + idStep, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info,
      StepMeta nextStep,
      VariableSpace space) {
    // Just add the response field...
    if (responseField != null) {
      final ValueMetaInterface key = new ValueMeta(space.environmentSubstitute(responseField),
          ValueMetaInterface.TYPE_STRING);
      key.setOrigin(origin);
      r.addValueMeta(key);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    return super.clone();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KettleXMLException
   */
  @Override
  public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
      throws KettleXMLException {
    readData(stepnode);
  }

  /**
   * Reads data from XML transformation file.
   * 
   * @param stepnode the step XML node.
   * @throws KettleXMLException the kettle XML exception.
   */
  public void readData(Node stepnode) throws KettleXMLException {
    try {
      registrationId = XMLHandler.getTagValue(stepnode, "registrationId");
      collapseKey = XMLHandler.getTagValue(stepnode, "collapseKey");
      delayWhileIdle = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "delayWhileIdle"));
      timeToLive = XMLHandler.getTagValue(stepnode, "timeToLive");
      restrictedPackageName = XMLHandler.getTagValue(stepnode, "restrictedPackageName");
      dryRun = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "dryRun"));
      apiKey = XMLHandler.getTagValue(stepnode, "apiKey");
      responseField = XMLHandler.getTagValue(stepnode, "ResponseField");
      pushEncoding = XMLHandler.getTagValue(stepnode, "pushEncoding");
      retrying = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "retrying"));
      retryNumber = XMLHandler.getTagValue(stepnode, "retryNumber");
      delayBeforeLastRetry = XMLHandler.getTagValue(stepnode, "delayBeforeLastRetry");

      final Node fields = XMLHandler.getSubNode(stepnode, "fields");
      final int nrRows = XMLHandler.countNodes(fields, "field");
      fieldStream = new ArrayList<String>();
      dataFieldPush = new ArrayList<String>();
      for (int i = 0; i < nrRows; i++) {
        final Node knode = XMLHandler.getSubNodeByNr(fields, "field", i);
        fieldStream.add(XMLHandler.getTagValue(knode, "fieldStream"));
        dataFieldPush.add(XMLHandler.getTagValue(knode, "dataFieldPush"));
      }
    } catch (Exception e) {
      throw new KettleXMLException(BaseMessages.getString(PKG,
          "AndroidPushNotification.Exception.UnexpectedErrorInReadingStepInfo"), e);
    }
  }

  /**
   * Sets the default values.
   */
  public void setDefault() {
    this.apiKey = "";
    this.collapseKey = "";
    this.dataFieldPush = new ArrayList<String>();
    this.delayBeforeLastRetry = "";
    this.delayWhileIdle = false;
    this.dryRun = false;
    this.fieldStream = new ArrayList<String>();
    this.responseField = "Response";
    this.pushEncoding = "UTF-8";
    this.registrationId = "";
    this.restrictedPackageName = "";
    this.retrying = false;
    this.retryNumber = "";
    this.timeToLive = "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta,
      RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
    CheckResult cr = null;
    if (prev == null || prev.size() == 0) {
      cr = new CheckResult(CheckResultInterface.TYPE_RESULT_WARNING, BaseMessages.getString(PKG,
          "AndroidPushNotification.CheckResult.NotReceivingFields"), stepMeta);
      remarks.add(cr);
    } else {
      cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG,
          "AndroidPushNotification.CheckResult.StepRecevingData", prev.size() + ""), stepMeta);
      remarks.add(cr);
      if (prev.indexOfValue(registrationId) < 0) {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG,
            "AndroidPushNotification.CheckResult.NoRegistrationIdFound"), stepMeta);
        remarks.add(cr);
      }
      if (retrying && "0".equals(retryNumber)) {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG,
            "AndroidPushNotification.CheckResult.NoRetryNumberDefined"), stepMeta);
        remarks.add(cr);
      }
      if (retrying && "0".equals(delayBeforeLastRetry)) {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG,
            "AndroidPushNotification.CheckResult.NoDelayBeforeLastRetryDefined"), stepMeta);
        remarks.add(cr);
      }

      String errorMessage = "";
      boolean errorFound = false;
      // Starting from selected fields in ...
      for (String f : fieldStream) {
        if (prev.indexOfValue(f) < 0) {
          errorMessage += "\t\t" + f + Const.CR;
          errorFound = true;
        }
      }
      if (errorFound) {
        errorMessage =
            BaseMessages.getString(PKG, "AndroidPushNotification.CheckResult.FieldsFound",
                errorMessage);
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, errorMessage, stepMeta);
        remarks.add(cr);
      } else {
        if (fieldStream.size() > 0) {
          cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG,
              "AndroidPushNotification.CheckResult.AllFieldsFound"), stepMeta);
          remarks.add(cr);
        } else {
          cr =
              new CheckResult(CheckResultInterface.TYPE_RESULT_WARNING, BaseMessages.getString(PKG,
                  "AndroidPushNotification.CheckResult.NoFieldsEntered"), stepMeta);
          remarks.add(cr);
        }
      }

      // See if we have input streams leading to this step!
      if (input.length > 0) {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG,
            "AndroidPushNotification.CheckResult.StepRecevingData2"), stepMeta);
        remarks.add(cr);
      } else {
        cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG,
            "AndroidPushNotification.CheckResult.NoInputReceivedFromOtherSteps"), stepMeta);
        remarks.add(cr);
      }
    }
  }

  /**
   * Get the Step dialog, needs for configure the step.
   * 
   * @param shell the shell.
   * @param meta the associated base step metadata.
   * @param transMeta the associated transformation metadata.
   * @param name the step name
   * @return The appropriate StepDialogInterface class.
   */
  public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta,
      String name) {
    return new PushNotificationStepDialog(shell, (BaseStepMeta) meta, transMeta, name);
  }

  /**
   * Get the executing step, needed by Trans to launch a step.
   * 
   * @param stepMeta The step info.
   * @param stepDataInterface the step data interface linked to this step. Here the step can store
   *        temporary data, database connections, etc.
   * @param cnr The copy nr to get.
   * @param transMeta The transformation info.
   * @param disp The launching transformation.
   * @return The appropriate StepInterface class.
   */
  public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr,
      TransMeta transMeta,
      Trans disp) {
    return new PushNotificationStep(stepMeta, stepDataInterface, cnr, transMeta, disp);
  }

  /**
   * Get a new instance of the appropriate data class. This data class implements the
   * StepDataInterface. It basically contains the persisting data that needs to live on, even if a
   * worker thread is terminated.
   * 
   * @return The appropriate StepDataInterface class.
   */
  public StepDataInterface getStepData() {
    return new PushNotificationStepData();
  }

}
