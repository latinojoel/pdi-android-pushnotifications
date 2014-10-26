package com.latinojoel.di.trans.steps.pushnotifications.android;

import java.util.List;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * This class contains the methods to set and retrieve the status of the step data.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
public class PushNotificationStepData extends BaseStepData implements StepDataInterface {
  RowMetaInterface outputRowMeta = null;
  RowMetaInterface insertRowMeta = null;
  List<Integer> valuenrs = null;
  int fieldnr = 0;
  int nrPrevFields = 0;
  int indexOfRegistrationIdField = -1;
  String collapseKey = null;
  String restrictedPackageName = null;
  String apiKey = null;
  int timeToLive = 0;
  int retryNumber = 0;
  int delayBeforeLastRetry = 0;
}
