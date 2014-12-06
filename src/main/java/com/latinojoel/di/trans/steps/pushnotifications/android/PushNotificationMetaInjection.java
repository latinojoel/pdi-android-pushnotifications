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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.StepInjectionMetaEntry;
import org.pentaho.di.trans.step.StepMetaInjectionInterface;
import org.pentaho.di.trans.steps.denormaliser.DenormaliserTargetField;

/**
 * This class is responsible for the implementation of metadata injection.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @since 1.0.1
 */
public class PushNotificationMetaInjection implements StepMetaInjectionInterface {

  /** for i18n purposes. **/
  private static final Class<?> PKG = PushNotificationMetaInjection.class;

  private PushNotificationMeta meta;

  public PushNotificationMetaInjection(PushNotificationMeta pushNotificationMeta) {
    this.meta = pushNotificationMeta;
  }

  /**
   * Metadata injection entry fields.
   * 
   * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
   * @since 1.0.1
   */
  public enum EntryFields {

    REGISTRATION_ID(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.REGISTRATION_ID.Label")),
    COLLAPSE_KEY(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.COLLAPSE_KEY.Label")),
    DELAY_WHILE_IDLE(ValueMetaInterface.TYPE_BOOLEAN, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.DELAY_WHILE_IDLE.Label")),
    TIME_TO_LIVE(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.TIME_TO_LIVE.Label")),
    RESTRICTED_PACKAGE_NAME(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.RESTRICTED_PACKAGE_NAME.Label")),
    DRY_RUN(ValueMetaInterface.TYPE_BOOLEAN, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.DRY_RUN.Label")),
    // FIELDS_STREAM(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
    // "AndroidPushNotificationMetaInj.FIELDS_STREAM.Label")),
    // DATA_FIELDS_PUSH(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
    // "AndroidPushNotificationMetaInj.DATA_FIELDS_PUSH.Label")),
    API_KEY(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.API_KEY.Label")),
    RESPONSE_FIELD(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.RESPONSE_FIELD.Label")),
    PUSH_ENCODING(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.PUSH_ENCODING.Label")),
    RETRYING(ValueMetaInterface.TYPE_BOOLEAN, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.RETRYING.Label")),
    RETRY_NUMBER(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.RETRY_NUMBER.Label")),
    DELAY_BEFORE_LAST_RETRY(ValueMetaInterface.TYPE_STRING, BaseMessages.getString(PKG,
        "AndroidPushNotificationMetaInj.DELAY_BEFORE_LAST_RETRY.Label"));

    private int valueType;
    private String description;

    private EntryFields(int valueType, String description) {
      this.valueType = valueType;
      this.description = description;
    }

    public int getValueType() {
      return valueType;
    }

    public String getDescription() {
      return description;
    }

    public static EntryFields findEntry(String key) {
      return EntryFields.valueOf(key);
    }
  };

  /**
   * {@inheritDoc}
   */
  public List<StepInjectionMetaEntry> extractStepMetadataEntries() throws KettleException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public List<StepInjectionMetaEntry> getStepInjectionMetadataEntries() throws KettleException {
    final List<StepInjectionMetaEntry> all = new ArrayList<StepInjectionMetaEntry>();

    final EntryFields[] topEntries = EntryFields.values();
    for (EntryFields topEntry : topEntries) {
      all.add(new StepInjectionMetaEntry(topEntry.name(), topEntry.getValueType(), topEntry
          .getDescription()));
    }

    return all;
  }

  /**
   * {@inheritDoc}
   */
  public void injectStepMetadataEntries(List<StepInjectionMetaEntry> all) throws KettleException {

    final List<DenormaliserTargetField> pushTargetFields =
        new ArrayList<DenormaliserTargetField>();

    for (StepInjectionMetaEntry lookFields : all) {
      final EntryFields fieldsEntry = EntryFields.findEntry(lookFields.getKey());
      if (fieldsEntry == null) {
        continue;
      }

      final String lookValue = (String) lookFields.getValue();
      switch (fieldsEntry) {
        case API_KEY:
          meta.setApiKey(lookValue);
          break;
        case COLLAPSE_KEY:
          meta.setCollapseKey(lookValue);
          break;
        case DELAY_BEFORE_LAST_RETRY:
          meta.setDelayBeforeLastRetry(lookValue);
          break;
        case DELAY_WHILE_IDLE:
          meta.setDelayWhileIdle("Y".equalsIgnoreCase(lookValue));
          break;
        case DRY_RUN:
          meta.setDryRun("Y".equalsIgnoreCase(lookValue));
          break;
        case PUSH_ENCODING:
          meta.setPushEncoding(lookValue);
          break;
        case REGISTRATION_ID:
          meta.setRegistrationId(lookValue);
          break;
        case RESPONSE_FIELD:
          meta.setResponseField(lookValue);
          break;
        case RESTRICTED_PACKAGE_NAME:
          meta.setRestrictedPackageName(lookValue);
          break;
        case RETRY_NUMBER:
          meta.setRetryNumber(lookValue);
          break;
        case RETRYING:
          meta.setRetrying("Y".equalsIgnoreCase(lookValue));
          break;
        case TIME_TO_LIVE:
          meta.setTimeToLive(lookValue);
          break;

      }
    }

  }

  public PushNotificationMeta getMeta() {
    return meta;
  }
}
