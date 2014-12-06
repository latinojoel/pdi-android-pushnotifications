/**
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package com.latinojoel.di.trans.steps.pushnotifications.android;

import java.io.IOException;
import java.util.ArrayList;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.latinojoel.gcm.server.Message;
import com.latinojoel.gcm.server.Sender;

/**
 * This class is responsible to processing the data rows.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
public class PushNotification extends BaseStep implements StepInterface {

  /** for i18n purposes. **/
  private static final Class<?> PKG = PushNotification.class;
  private PushNotificationMeta meta;
  private PushNotificationData data;

  public PushNotification(
      StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
    super(s, stepDataInterface, c, t, dis);
  }

  /**
   * {@inheritDoc}
   * 
   * @throws KettleException
   */
  @Override
  public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
    this.meta = (PushNotificationMeta) smi;
    this.data = (PushNotificationData) sdi;

    final Object[] r = getRow();
    if (r == null) {
      setOutputDone();
      return false;
    }
    try {
      if (first) {
        first = false;
        data.outputRowMeta = super.getInputRowMeta().clone();
        data.nrPrevFields = data.outputRowMeta.size();
        meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

        data.insertRowMeta = new RowMeta();

        data.valuenrs = new ArrayList<Integer>();
        for (int i = 0; i < meta.getDataFieldPush().size(); i++) {
          data.valuenrs.add(getInputRowMeta().indexOfValue(meta.getFieldStream().get(i)));
          if (data.valuenrs.get(i) < 0) {
            throw new KettleStepException(BaseMessages.getString(PKG,
                "AndroidPushNotification.Exception.FieldRequired", meta.getFieldStream().get(i)));
          }
        }
      } // end if first

      final Object[] outputRow = RowDataUtil.allocateRowData(data.outputRowMeta.size());
      for (int i = 0; i < data.nrPrevFields; i++) {
        outputRow[i] = r[i];
      }
      final String res = sendPush(getInputRowMeta(), r);
      outputRow[data.nrPrevFields] = res;

      putRow(data.outputRowMeta, outputRow);

      if (checkFeedback(getLinesRead())) {
        if (log.isBasic()) {
          logBasic("Linenr " + getLinesRead()); // Some basic logging
        }
      }
    } catch (Exception e) {
      if (getStepMeta().isDoingErrorHandling()) {
        putError(getInputRowMeta(), r, 1L, e.toString(), null, "PUSHAND001");
      } else {
        new KettleStepException(e);
      }
    }
    return true;
  }

  /**
   * Send Push notification.
   * 
   * @param rowMeta the row meta data.
   * @param r the row data.
   * @return the result of push notification.
   * @throws KettleStepException the kettle step exception.
   * @throws KettleValueException the kettle value exception.
   */
  private String sendPush(RowMetaInterface rowMeta, Object[] r) throws KettleStepException,
      KettleValueException {
    if (r == null) { // Stop: last line or error encountered
      if (log.isDetailed()) {
        logDetailed("Last line inserted: stop");
      }
      return null;
    }

    // Cache the position of the registration id field
    if (data.indexOfRegistrationIdField < 0) {
      final String realRegistrationId = environmentSubstitute(meta.getRegistrationId());
      data.indexOfRegistrationIdField = rowMeta.indexOfValue(realRegistrationId);
      if (data.indexOfRegistrationIdField < 0) {
        final String message =
            "Unable to find table name field [" + realRegistrationId + "] in input row";
        // logError(message);
        throw new KettleStepException(message);
      }
    }

    final Message.Builder messageBuilder = new Message.Builder();
    messageBuilder.collapseKey(data.collapseKey);
    messageBuilder.delayWhileIdle(meta.isDelayWhileIdle());
    messageBuilder.dryRun(meta.isDryRun());
    messageBuilder.restrictedPackageName(data.restrictedPackageName);
    messageBuilder.timeToLive(data.timeToLive);
    for (int i = 0; i < meta.getDataFieldPush().size(); i++) {
      messageBuilder.addData(meta.getDataFieldPush().get(i),
          getInputRowMeta().getString(r, data.valuenrs.get(i)));
    }

    final Sender sender = new Sender(data.apiKey);
    String result = null;
    try {
      if (meta.isRetrying()) {
        result =
            sender.send(messageBuilder.build(),
                rowMeta.getString(r, data.indexOfRegistrationIdField),
                data.delayBeforeLastRetry, data.retryNumber);

      } else {
        result = sender.sendNoRetry(messageBuilder.build(),
            rowMeta.getString(r, data.indexOfRegistrationIdField));
      }
    } catch (IOException e) {
      new KettleStepException(e);
    }
    return result;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
    meta = (PushNotificationMeta) smi;
    data = (PushNotificationData) sdi;

    if (super.init(smi, sdi)) {
      if (meta.getTimeToLive() != null) {
        data.timeToLive = Integer.parseInt(environmentSubstitute(meta.getTimeToLive()));
      }
      if (meta.getRetryNumber() != null) {
        data.retryNumber = Integer.parseInt(environmentSubstitute(meta.getRetryNumber()));
      }
      if (meta.getDelayBeforeLastRetry() != null) {
        data.delayBeforeLastRetry =
            Integer.parseInt(environmentSubstitute(meta.getDelayBeforeLastRetry()));
      }
      if ((meta.getCollapseKey() != null) && !"".equals(meta.getCollapseKey())) {
        data.collapseKey = environmentSubstitute(meta.getCollapseKey());
      }
      if (meta.getRestrictedPackageName() != null && !"".equals(meta.getRestrictedPackageName())) {
        data.restrictedPackageName = environmentSubstitute(meta.getRestrictedPackageName());
      }
      if ((meta.getApiKey() != null) && !"".equals(meta.getApiKey())) {
        data.apiKey = environmentSubstitute(meta.getApiKey());
      }
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
    super.dispose(smi, sdi);
  }

  /**
   * Run is were the action happens.
   */
  public void run() {
    logBasic("Starting to run...");
    try {
      while (processRow(meta, data) && !isStopped()) {
        continue;
      }
    } catch (Exception e) {
      logError("Unexpected error : " + e.toString());
      logError(Const.getStackTracker(e));
      setErrors(1);
      stopAll();
    } finally {
      dispose(meta, data);
      logBasic("Finished, processing " + getLinesRead() + " rows");
      markStop();
    }
  }
}
