/*
 * Pentaho Data Integration Android Push Notifications https://github.com/latinojoel/pdi-apple-pushnotifications
 * 
 * Copyright (c) 2009 about.me/latinojoel
 * 
 * Licensed under the GNU General Public License, Version 3.0; you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * The program is provided "as is" without any warranty express or implied, including the warranty of non-infringement
 * and the implied warranties of merchantibility and fitness for a particular purpose. The Copyright owner will not be
 * liable for any damages suffered by you as a result of using the Program. In no event will the Copyright owner be
 * liable for any special, indirect or consequential damages or lost profits even if the Copyright owner has been
 * advised of the possibility of their occurrence.
 */
package com.latinojoel.di.trans.steps.pushnotifications.android;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * This class is responsible for Push notification UI on Spoon.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @version $Revision: 666 $
 * 
 */
public class PushNotificationStepDialog extends BaseStepDialog implements StepDialogInterface {

    /** for i18n purposes. **/
    private static final Class<?> PKG = PushNotificationStepDialog.class;

    /** List of ColumnInfo that should have the field names of the selected database table. */
    final List<ColumnInfo> tableFieldColumns = new ArrayList<ColumnInfo>();

    private PushNotificationStepMeta input;
    private TextVar wValCollapseKeyField, wTimeToLiveField, wRestrictedPackageField, wAPIKeyField, wRetriesNumberField,
    wDelayRetryNumberField = null;
    private Text wResponseField;
    private TableView wFields;
    private Button wGetFields, wDelayWhileIdleField, wDryRunField, wRetryingField;
    private FormData fdGetFields;
    private ColumnInfo[] ciFields;
    private CTabFolder wTabFolder;
    private CTabItem wMainOptionsTab, wPropTab;
    private CCombo wRegIdField;
    private ComboVar wEncoding;
    private boolean gotEncodings, gotPreviousFields = false;
    private Map<String, Integer> inputFields = new HashMap<String, Integer>();

    public PushNotificationStepDialog(Shell parent, BaseStepMeta in, TransMeta transMeta, String sname) {
        super(parent, in, transMeta, sname);
        this.input = (PushNotificationStepMeta) in;
    }

    /**
     * Opens a step dialog window.
     * 
     * @return the (potentially new) name of the step
     */
    public String open() {
        final Shell parent = getParent();
        final Display display = parent.getDisplay();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
        props.setLook(shell);
        setShellImage(shell, this.input);

        final ModifyListener lsMod = new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                input.setChanged();
            }
        };
        backupChanged = input.hasChanged();

        final FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;
        shell.setLayout(formLayout);
        shell.setText(BaseMessages.getString(PKG, "AndroidPushNotification.Shell.Title"));

        final int middle = props.getMiddlePct();
        final int margin = Const.MARGIN;

        // Stepname line
        wlStepname = new Label(shell, SWT.RIGHT);
        wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
        props.setLook(wlStepname);
        fdlStepname = new FormData();
        fdlStepname.left = new FormAttachment(0, 0);
        fdlStepname.right = new FormAttachment(middle, -margin);
        fdlStepname.top = new FormAttachment(0, margin);
        wlStepname.setLayoutData(fdlStepname);

        wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStepname.setText(stepname);
        props.setLook(wStepname);
        wStepname.addModifyListener(lsMod);
        fdStepname = new FormData();
        fdStepname.left = new FormAttachment(middle, 0);
        fdStepname.top = new FormAttachment(0, margin);
        fdStepname.right = new FormAttachment(100, 0);
        wStepname.setLayoutData(fdStepname);

        wTabFolder = new CTabFolder(shell, SWT.BORDER);
        props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

        // /////////////////////////////////
        // START OF Main Options TAB //
        // /////////////////////////////////
        wMainOptionsTab = new CTabItem(wTabFolder, SWT.NONE);
        wMainOptionsTab.setText(BaseMessages.getString(PKG, "AndroidPushNotification.MainOptionTab.CTabItem.Title"));

        final FormLayout mainOptionsLayout = new FormLayout();
        mainOptionsLayout.marginWidth = 3;
        mainOptionsLayout.marginHeight = 3;

        final Composite wMainOptionsComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wMainOptionsComp);
        wMainOptionsComp.setLayout(mainOptionsLayout);

        // Registration Id field
        final Label wlRegIdField = new Label(wMainOptionsComp, SWT.RIGHT);
        wlRegIdField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.wlRegIdField.Label"));
        props.setLook(wlRegIdField);
        final FormData fdlRegIdField = new FormData();
        fdlRegIdField.left = new FormAttachment(0, -margin);
        fdlRegIdField.top = new FormAttachment(0, margin);
        fdlRegIdField.right = new FormAttachment(middle, -2 * margin);
        wlRegIdField.setLayoutData(fdlRegIdField);

        wRegIdField = new CCombo(wMainOptionsComp, SWT.BORDER | SWT.READ_ONLY);
        wRegIdField.setEditable(true);
        props.setLook(wRegIdField);
        wRegIdField.addModifyListener(lsMod);
        final FormData fdRegIdField = new FormData();
        fdRegIdField.left = new FormAttachment(middle, -margin);
        fdRegIdField.top = new FormAttachment(0, margin);
        fdRegIdField.right = new FormAttachment(100, -margin);
        wRegIdField.setLayoutData(fdRegIdField);
        wRegIdField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent arg0) {
                getRegIdFields();

            }

            public void focusLost(FocusEvent arg0) {
                // TODO Auto-generated method stub
            }
        });

        // //////////////////////////////////
        // START OF OPTIONAL FIELDS GROUP //
        // ///////////////////////////////////
        final Group gOptionalFields = new Group(wMainOptionsComp, SWT.SHADOW_NONE);
        props.setLook(gOptionalFields);
        gOptionalFields.setText(BaseMessages.getString(PKG, "AndroidPushNotification.gOptionalFields.Label"));
        final FormLayout flOptionalFieldsLayout = new FormLayout();
        flOptionalFieldsLayout.marginWidth = 10;
        flOptionalFieldsLayout.marginHeight = 10;
        gOptionalFields.setLayout(flOptionalFieldsLayout);

        // collapse key value
        final Label wlValCollapseKeyField = new Label(gOptionalFields, SWT.RIGHT);
        wlValCollapseKeyField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.CollapseKeyField.Label"));
        props.setLook(wlValCollapseKeyField);

        final FormData fdlValCollapseKeyField = new FormData();
        fdlValCollapseKeyField.left = new FormAttachment(0, 0);
        fdlValCollapseKeyField.right = new FormAttachment(middle, -margin);
        fdlValCollapseKeyField.top = new FormAttachment(wRegIdField, margin);
        wlValCollapseKeyField.setLayoutData(fdlValCollapseKeyField);

        wValCollapseKeyField = new TextVar(this.transMeta, gOptionalFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wValCollapseKeyField);
        wValCollapseKeyField.addModifyListener(lsMod);
        final FormData fdValCollapseKeyFeild = new FormData();
        fdValCollapseKeyFeild.left = new FormAttachment(middle, 0);
        fdValCollapseKeyFeild.right = new FormAttachment(100, 0);
        fdValCollapseKeyFeild.top = new FormAttachment(wRegIdField, margin);
        wValCollapseKeyField.setLayoutData(fdValCollapseKeyFeild);

        // Delay while idle value
        final Label wlDelayWhileIdleField = new Label(gOptionalFields, SWT.RIGHT);
        wlDelayWhileIdleField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.DelayWhileIdleField.Label"));
        props.setLook(wlDelayWhileIdleField);
        final FormData fdlDelayWhileIdleField = new FormData();
        fdlDelayWhileIdleField.left = new FormAttachment(0, -margin);
        fdlDelayWhileIdleField.top = new FormAttachment(wValCollapseKeyField, margin);
        fdlDelayWhileIdleField.right = new FormAttachment(middle, -2 * margin);
        wlDelayWhileIdleField.setLayoutData(fdlDelayWhileIdleField);

        wDelayWhileIdleField = new Button(gOptionalFields, SWT.CHECK);
        props.setLook(wDelayWhileIdleField);
        final FormData fdDelayWhileIdleField = new FormData();
        fdDelayWhileIdleField.left = new FormAttachment(middle, 0);
        fdDelayWhileIdleField.right = new FormAttachment(100, 0);
        fdDelayWhileIdleField.top = new FormAttachment(wValCollapseKeyField, margin);
        wDelayWhileIdleField.setLayoutData(fdDelayWhileIdleField);

        // Time to live value
        final Label wlTimeToLiveField = new Label(gOptionalFields, SWT.RIGHT);
        wlTimeToLiveField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.TimeToLiveField.Label"));
        props.setLook(wlTimeToLiveField);
        final FormData fdlTimeToLiveField = new FormData();
        fdlTimeToLiveField.left = new FormAttachment(0, -margin);
        fdlTimeToLiveField.top = new FormAttachment(wDelayWhileIdleField, margin);
        fdlTimeToLiveField.right = new FormAttachment(middle, -2 * margin);
        wlTimeToLiveField.setLayoutData(fdlTimeToLiveField);

        wTimeToLiveField = new TextVar(this.transMeta, gOptionalFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wTimeToLiveField);
        final FormData fdTimeToLiveField = new FormData();
        fdTimeToLiveField.left = new FormAttachment(middle, 0);
        fdTimeToLiveField.top = new FormAttachment(wDelayWhileIdleField, margin);
        fdTimeToLiveField.right = new FormAttachment(100, 0);
        wTimeToLiveField.setLayoutData(fdTimeToLiveField);

        // Restricted package name value
        final Label wlRestrictedPackageField = new Label(gOptionalFields, SWT.RIGHT);
        wlRestrictedPackageField.setText(BaseMessages.getString(PKG,
                "AndroidPushNotification.RestrictedPackageField.Label"));
        props.setLook(wlRestrictedPackageField);
        final FormData fdlRestrictedPackageField = new FormData();
        fdlRestrictedPackageField.left = new FormAttachment(0, -margin);
        fdlRestrictedPackageField.top = new FormAttachment(wTimeToLiveField, margin);
        fdlRestrictedPackageField.right = new FormAttachment(middle, -2 * margin);
        wlRestrictedPackageField.setLayoutData(fdlRestrictedPackageField);

        wRestrictedPackageField = new TextVar(this.transMeta, gOptionalFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wRestrictedPackageField);
        wRestrictedPackageField.setToolTipText(BaseMessages.getString(PKG, "GetFileNamesDialog.FileField.Tooltip"));
        final FormData fdRestrictedPackageField = new FormData();
        fdRestrictedPackageField.left = new FormAttachment(middle, 0);
        fdRestrictedPackageField.top = new FormAttachment(wTimeToLiveField, margin);
        fdRestrictedPackageField.right = new FormAttachment(100, 0);
        wRestrictedPackageField.setLayoutData(fdRestrictedPackageField);

        // Dry run value
        final Label wlDryRunField = new Label(gOptionalFields, SWT.RIGHT);
        wlDryRunField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.DryRunField.Label"));
        props.setLook(wlDryRunField);
        final FormData fdlDryRunField = new FormData();
        fdlDryRunField.left = new FormAttachment(0, -margin);
        fdlDryRunField.top = new FormAttachment(wRestrictedPackageField, margin);
        fdlDryRunField.right = new FormAttachment(middle, -2 * margin);
        wlDryRunField.setLayoutData(fdlDryRunField);

        wDryRunField = new Button(gOptionalFields, SWT.CHECK);
        props.setLook(wDryRunField);
        final FormData fdDryRunField = new FormData();
        fdDryRunField.left = new FormAttachment(middle, 0);
        fdDryRunField.top = new FormAttachment(wRestrictedPackageField, margin);
        fdDryRunField.right = new FormAttachment(100, 0);
        wDryRunField.setLayoutData(fdDryRunField);

        final FormData fdOutpuFields = new FormData();
        fdOutpuFields.left = new FormAttachment(0, margin);
        fdOutpuFields.top = new FormAttachment(wRegIdField, 2 * margin);
        fdOutpuFields.right = new FormAttachment(100, -margin);
        gOptionalFields.setLayoutData(fdOutpuFields);
        // /////////////////////////////////
        // End OF OPTIONAL FIELDS GROUP //
        // /////////////////////////////////

        // The fields table
        final Label wlFields = new Label(wMainOptionsComp, SWT.NONE);
        wlFields.setText(BaseMessages.getString(PKG, "AndroidPushNotification.InsertFields.Label"));
        props.setLook(wlFields);
        final FormData fdlUpIns = new FormData();
        fdlUpIns.left = new FormAttachment(0, 0);
        fdlUpIns.top = new FormAttachment(gOptionalFields, margin);
        wlFields.setLayoutData(fdlUpIns);

        final int tableCols = 2;
        final int upInsRows = input.getFieldStream() != null ? input.getFieldStream().size() : 1;

        ciFields = new ColumnInfo[tableCols];
        ciFields[0] = new ColumnInfo(BaseMessages.getString(PKG, "AndroidPushNotification.ColumnInfo.PushField"),
                ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false);
        ciFields[1] = new ColumnInfo(BaseMessages.getString(PKG, "AndroidPushNotification.ColumnInfo.StreamField"),
                ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false);
        wFields = new TableView(transMeta, wMainOptionsComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL
                | SWT.H_SCROLL, ciFields, upInsRows, lsMod, props);

        wGetFields = new Button(wMainOptionsComp, SWT.PUSH);
        wGetFields.setText(BaseMessages.getString(PKG, "AndroidPushNotification.GetFields.Button"));
        fdGetFields = new FormData();
        fdGetFields.top = new FormAttachment(wlFields, margin);
        fdGetFields.right = new FormAttachment(100, 0);
        wGetFields.setLayoutData(fdGetFields);

        final FormData fdFields = new FormData();
        fdFields.left = new FormAttachment(0, 0);
        fdFields.top = new FormAttachment(wlFields, margin);
        fdFields.right = new FormAttachment(wGetFields, -margin);
        fdFields.bottom = new FormAttachment(100, -2 * margin);
        wFields.setLayoutData(fdFields);

        final FormData fdMainOptions = new FormData();
        fdMainOptions.left = new FormAttachment(0, 0);
        fdMainOptions.top = new FormAttachment(0, 0);
        fdMainOptions.right = new FormAttachment(100, 0);
        fdMainOptions.bottom = new FormAttachment(100, 0);
        wMainOptionsComp.setLayoutData(fdMainOptions);

        wMainOptionsComp.layout();
        wMainOptionsTab.setControl(wMainOptionsComp);
        // /////////////////////////////////
        // END OF Main Options TAB //
        // /////////////////////////////////

        final FormData fdMainOptionsTab = new FormData();
        fdMainOptionsTab.left = new FormAttachment(0, 0);
        fdMainOptionsTab.top = new FormAttachment(wStepname, margin);
        fdMainOptionsTab.right = new FormAttachment(100, 0);
        fdMainOptionsTab.bottom = new FormAttachment(100, -50);
        wTabFolder.setLayoutData(fdMainOptionsTab);

        //
        // Properties tab...
        //
        wPropTab = new CTabItem(wTabFolder, SWT.NONE);
        wPropTab.setText(BaseMessages.getString(PKG, "AndroidPushNotification.PropTab.CTabItem.Title"));

        final Composite wPropComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wPropComp);

        final FormLayout propsCompLayout = new FormLayout();
        propsCompLayout.marginWidth = Const.FORM_MARGIN;
        propsCompLayout.marginHeight = Const.FORM_MARGIN;
        wPropComp.setLayout(propsCompLayout);

        // API key value
        final Label wlAPIKeyField = new Label(wPropComp, SWT.RIGHT);
        wlAPIKeyField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.APIKeyField.Label"));
        props.setLook(wlAPIKeyField);
        final FormData fdlAPIKeyField = new FormData();
        fdlAPIKeyField.left = new FormAttachment(0, -margin);
        fdlAPIKeyField.top = new FormAttachment(0, margin);
        fdlAPIKeyField.right = new FormAttachment(middle, -2 * margin);
        wlAPIKeyField.setLayoutData(fdlAPIKeyField);

        wAPIKeyField = new TextVar(this.transMeta, wPropComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wAPIKeyField);
        final FormData fdAPIKeyField = new FormData();
        fdAPIKeyField.left = new FormAttachment(middle, -margin);
        fdAPIKeyField.top = new FormAttachment(0, margin);
        fdAPIKeyField.right = new FormAttachment(100, -margin);
        wAPIKeyField.setLayoutData(fdAPIKeyField);

        // response value
        final Label wlResponseField = new Label(wPropComp, SWT.RIGHT);
        wlResponseField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.ResponseField.Label"));
        props.setLook(wlResponseField);
        final FormData fdlResponseField = new FormData();
        fdlResponseField.left = new FormAttachment(0, -margin);
        fdlResponseField.top = new FormAttachment(wAPIKeyField, margin);
        fdlResponseField.right = new FormAttachment(middle, -2 * margin);
        wlResponseField.setLayoutData(fdlResponseField);

        wResponseField = new Text(wPropComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wResponseField);
        final FormData fdResponseField = new FormData();
        fdResponseField.left = new FormAttachment(middle, -margin);
        fdResponseField.top = new FormAttachment(wAPIKeyField, margin);
        fdResponseField.right = new FormAttachment(100, -margin);
        wResponseField.setLayoutData(fdResponseField);

        // Push encoding value
        final Label wlEncoding = new Label(wPropComp, SWT.RIGHT);
        wlEncoding.setText(BaseMessages.getString(PKG, "AndroidPushNotification.Encoding.Label"));
        props.setLook(wlEncoding);
        final FormData fdlEncoding = new FormData();
        fdlEncoding.left = new FormAttachment(0, -margin);
        fdlEncoding.top = new FormAttachment(wResponseField, margin);
        fdlEncoding.right = new FormAttachment(middle, -2 * margin);
        wlEncoding.setLayoutData(fdlEncoding);

        wEncoding = new ComboVar(transMeta, wPropComp, SWT.BORDER | SWT.READ_ONLY);
        wEncoding.setEditable(true);
        props.setLook(wEncoding);
        wEncoding.addModifyListener(lsMod);
        final FormData fdEncoding = new FormData();
        fdEncoding.left = new FormAttachment(middle, -margin);
        fdEncoding.top = new FormAttachment(wResponseField, margin);
        fdEncoding.right = new FormAttachment(100, -margin);
        wEncoding.setLayoutData(fdEncoding);

        wEncoding.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
            }

            public void focusGained(FocusEvent e) {
                final Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                setEncodings();
                shell.setCursor(null);
                busy.dispose();
            }
        });

        // Retrying in case of unavailability
        final Label wlRetryingField = new Label(wPropComp, SWT.RIGHT);
        wlRetryingField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.RetryingField.Label"));
        props.setLook(wlRetryingField);
        final FormData fdlRetryingField = new FormData();
        fdlRetryingField.left = new FormAttachment(0, -margin);
        fdlRetryingField.top = new FormAttachment(wEncoding, margin);
        fdlRetryingField.right = new FormAttachment(middle, -2 * margin);
        wlRetryingField.setLayoutData(fdlRetryingField);

        wRetryingField = new Button(wPropComp, SWT.CHECK);
        props.setLook(wRetryingField);
        final FormData fdRetryingField = new FormData();
        fdRetryingField.left = new FormAttachment(middle, -margin);
        fdRetryingField.top = new FormAttachment(wEncoding, margin);
        fdRetryingField.right = new FormAttachment(100, -margin);
        wRetryingField.setLayoutData(fdRetryingField);
        wRetryingField.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent se) {
                wRetriesNumberField.setEnabled(wRetryingField.getSelection());
                wDelayRetryNumberField.setEnabled(wRetryingField.getSelection());
                // setFlags();
            }
        });

        // Retries number value
        final Label wlRetriesNumberField = new Label(wPropComp, SWT.RIGHT);
        wlRetriesNumberField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.RetriesNumberField.Label"));
        props.setLook(wlRetriesNumberField);
        final FormData fdlRetriesNumberField = new FormData();
        fdlRetriesNumberField.left = new FormAttachment(0, -margin);
        fdlRetriesNumberField.top = new FormAttachment(wRetryingField, margin);
        fdlRetriesNumberField.right = new FormAttachment(middle, -2 * margin);
        wlRetriesNumberField.setLayoutData(fdlRetriesNumberField);

        wRetriesNumberField = new TextVar(this.transMeta, wPropComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wRetriesNumberField);
        final FormData fdRetriesNumberField = new FormData();
        fdRetriesNumberField.left = new FormAttachment(middle, -margin);
        fdRetriesNumberField.top = new FormAttachment(wRetryingField, margin);
        fdRetriesNumberField.right = new FormAttachment(100, -margin);
        wRetriesNumberField.setLayoutData(fdRetriesNumberField);

        // Delay before last retry in seconds
        final Label wlDelayRetryNumberField = new Label(wPropComp, SWT.RIGHT);
        wlDelayRetryNumberField.setText(BaseMessages.getString(PKG,
                "AndroidPushNotification.DelayRetryNumberField.Label"));
        props.setLook(wlDelayRetryNumberField);
        final FormData fdlDelayRetryNumberField = new FormData();
        fdlDelayRetryNumberField.left = new FormAttachment(0, -margin);
        fdlDelayRetryNumberField.top = new FormAttachment(wRetriesNumberField, margin);
        fdlDelayRetryNumberField.right = new FormAttachment(middle, -2 * margin);
        wlDelayRetryNumberField.setLayoutData(fdlDelayRetryNumberField);

        wDelayRetryNumberField = new TextVar(this.transMeta, wPropComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wDelayRetryNumberField);
        final FormData fdDelayRetryNumberField = new FormData();
        fdDelayRetryNumberField.left = new FormAttachment(middle, -margin);
        fdDelayRetryNumberField.top = new FormAttachment(wRetriesNumberField, margin);
        fdDelayRetryNumberField.right = new FormAttachment(100, -margin);
        wDelayRetryNumberField.setLayoutData(fdDelayRetryNumberField);
        //
        // End Properties tab...
        //

        final FormData fdPropsComp = new FormData();
        fdPropsComp.left = new FormAttachment(0, 0);
        fdPropsComp.top = new FormAttachment(0, 0);
        fdPropsComp.right = new FormAttachment(100, 0);
        fdPropsComp.bottom = new FormAttachment(100, 0);
        wPropComp.setLayoutData(fdPropsComp);

        wPropComp.layout();
        wPropTab.setControl(wPropComp);

        // OK and cancel buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

        final FormData fdTabFolder = new FormData();
        fdTabFolder.left = new FormAttachment(0, 0);
        fdTabFolder.top = new FormAttachment(wStepname, margin);
        fdTabFolder.right = new FormAttachment(100, 0);
        fdTabFolder.bottom = new FormAttachment(wOK, -margin);
        wTabFolder.setLayoutData(fdTabFolder);

        wTabFolder.setSelection(0);

        //
        // Search the fields in the background
        //
        final Runnable runnable = new Runnable() {
            public void run() {
                final StepMeta stepMeta = transMeta.findStep(stepname);
                if (stepMeta != null) {
                    try {
                        final RowMetaInterface row = transMeta.getPrevStepFields(stepMeta);

                        // Remember these fields...
                        for (int i = 0; i < row.size(); i++) {
                            inputFields.put(row.getValueMeta(i).getName(), Integer.valueOf(i));
                        }
                        setComboBoxes();
                    } catch (KettleException e) {
                        logError(BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
                    }
                }
            }
        };
        new Thread(runnable).start();

        // Add listeners
        lsCancel = new Listener() {
            public void handleEvent(Event e) {
                cancel();
            }
        };
        lsOK = new Listener() {
            public void handleEvent(Event e) {
                ok();
            }
        };
        lsGet = new Listener() {
            public void handleEvent(Event e) {
                get();
            }
        };

        wCancel.addListener(SWT.Selection, lsCancel);
        wOK.addListener(SWT.Selection, lsOK);
        wGetFields.addListener(SWT.Selection, lsGet);

        lsDef = new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                ok();
            }
        };
        wStepname.addSelectionListener(lsDef);

        // Detect X or ALT-F4 or something that kills this window...
        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                cancel();
            }
        });

        // Set the shell size, based upon previous time...
        setSize();

        getData();
        input.setChanged(backupChanged);

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return stepname;
    }

    /**
     * Read data and place it in the dialog.
     */
    public void getData() {
        wRegIdField.setText(input.getRegistrationId());
        if (input.getCollapseKey() != null) {
            wValCollapseKeyField.setText(input.getCollapseKey());
        }
        wDelayWhileIdleField.setSelection(input.isDelayWhileIdle());
        if (input.getTimeToLive() != null) {
            wTimeToLiveField.setText(String.valueOf(input.getTimeToLive()));
        }
        if (input.getRestrictedPackageName() != null) {
            wRestrictedPackageField.setText(input.getRestrictedPackageName());
        }
        wDryRunField.setSelection(input.isDryRun());
        if (input.getApiKey() != null) {
            wAPIKeyField.setText(input.getApiKey());
        }
        if (input.getResponseField() != null) {
            wResponseField.setText(input.getResponseField());
        }
        if (input.getPushEncoding() != null) {
            wEncoding.setText(input.getPushEncoding());
        }
        wRetryingField.setSelection(input.isRetrying());
        if (input.getRetryNumber() != null) {
            wRetriesNumberField.setText(String.valueOf(input.getRetryNumber()));
        }
        wRetriesNumberField.setEnabled(input.isRetrying());
        if (input.getDelayBeforeLastRetry() != null) {
            wDelayRetryNumberField.setText(String.valueOf(input.getDelayBeforeLastRetry()));
        }
        wDelayRetryNumberField.setEnabled(input.isRetrying());

        if (input.getFieldStream() != null && input.getDataFieldPush() != null) {
            for (int i = 0; i < input.getDataFieldPush().size(); i++) {
                final TableItem item = wFields.table.getItem(i);
                if (input.getDataFieldPush().get(i) != null) {
                    item.setText(1, input.getDataFieldPush().get(i));
                }
                if (input.getFieldStream().get(i) != null) {
                    item.setText(2, input.getFieldStream().get(i));
                }
            }
        }

        wStepname.selectAll();
    }

    /**
     * Cancel.
     */
    private void cancel() {
        stepname = null;
        input.setChanged(backupChanged);
        dispose();
    }

    /**
     * Let the plugin know about the entered data.
     */
    private void ok() {
        if (!Const.isEmpty(wStepname.getText())) {
            stepname = wStepname.getText();
            getInfo(input);
            if (input.getRegistrationId() == null || "".equals(input.getRegistrationId())) {
                final MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                mb.setMessage(BaseMessages.getString(PKG, "AndroidPushNotification.RegistrationIdError.DialogMessage"));
                mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
                mb.open();
            } else if (input.getApiKey() == null || "".equals(input.getApiKey())) {
                final MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                mb.setMessage(BaseMessages.getString(PKG, "AndroidPushNotification.APIKeyError.DialogMessage"));
                mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
                mb.open();
            } else if (input.getResponseField() == null || "".equals(input.getResponseField())) {
                final MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                mb.setMessage(BaseMessages.getString(PKG, "AndroidPushNotification.ResponseFieldError.DialogMessage"));
                mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
                mb.open();
            } else if (input.getPushEncoding() == null || "".equals(input.getPushEncoding())) {
                final MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                mb.setMessage(BaseMessages.getString(PKG, "AndroidPushNotification.PushEncodingError.DialogMessage"));
                mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
                mb.open();
            } else {
                stepname = wStepname.getText();
                dispose();
            }
        }
    }

    /**
     * Get the information.
     * 
     * @param info the push notification step meta data.
     */
    public void getInfo(PushNotificationStepMeta info) {
        input.setRegistrationId(wRegIdField.getText());
        input.setCollapseKey(wValCollapseKeyField.getText());
        input.setDelayWhileIdle(wDelayWhileIdleField.getSelection());
        input.setTimeToLive(wTimeToLiveField.getText());
        input.setRestrictedPackageName(wRestrictedPackageField.getText());
        input.setDryRun(wDryRunField.getSelection());
        input.setApiKey(wAPIKeyField.getText());
        input.setResponseField(wResponseField.getText());
        input.setPushEncoding(wEncoding.getText());
        input.setRetrying(wRetryingField.getSelection());
        input.setRetryNumber(wRetriesNumberField.getText());
        input.setDelayBeforeLastRetry(wDelayRetryNumberField.getText());

        final int nrRows = wFields.nrNonEmpty();
        final List<String> fieldStream = new ArrayList<String>();
        final List<String> dataFieldPush = new ArrayList<String>();
        for (int i = 0; i < nrRows; i++) {
            final TableItem item = wFields.getNonEmpty(i);
            dataFieldPush.add(Const.NVL(item.getText(1), ""));
            fieldStream.add(Const.NVL(item.getText(2), ""));
        }
        input.setFieldStream(fieldStream);
        input.setDataFieldPush(dataFieldPush);
    }

    /**
     * Get Registration id and sets on select box.
     */
    private void getRegIdFields() {
        if (!gotPreviousFields) {
            gotPreviousFields = true;
            try {
                final String source = wRegIdField.getText();
                wRegIdField.removeAll();
                final RowMetaInterface r = transMeta.getPrevStepFields(stepname);
                if (r != null) {
                    wRegIdField.setItems(r.getFieldNames());
                    if (source != null) {
                        wRegIdField.setText(source);
                    }
                }
            } catch (KettleException ke) {
                new ErrorDialog(shell,
                        BaseMessages.getString(PKG, "SyslogMessageDialog.FailedToGetFields/.DialogTitle"),
                        BaseMessages.getString(PKG, "SyslogMessageDialog.FailedToGetFields.DialogMessage"), ke);
            }
        }
    }

    /**
     * Fill up the fields table with the incoming fields.
     */
    private void get() {
        try {
            final RowMetaInterface r = transMeta.getPrevStepFields(stepname);
            if (r != null && !r.isEmpty()) {
                BaseStepDialog.getFieldsFromPrevious(r, wFields, 1, new int[] { 1, 2 }, new int[] {}, -1, -1, null);
            }
        } catch (KettleException ke) {
            new ErrorDialog(shell,
                    BaseMessages.getString(PKG, "AndroidPushNotification.FailedToGetFields.DialogTitle"),
                    BaseMessages.getString(PKG, "AndroidPushNotification.FailedToGetFields.DialogMessage"), ke);
        }

    }

    /**
     * Sets combo boxes values.
     */
    private void setComboBoxes() {
        // Something was changed in the row.
        //
        final Map<String, Integer> fields = new HashMap<String, Integer>();

        // Add the currentMeta fields...
        fields.putAll(inputFields);

        final Set<String> keySet = fields.keySet();
        final List<String> entries = new ArrayList<String>(keySet);

        final String[] fieldNames = (String[]) entries.toArray(new String[entries.size()]);

        Const.sortStrings(fieldNames);
        ciFields[1].setComboValues(fieldNames);
    }

    /**
     * Sets encodings on select box.
     */
    private void setEncodings() {
        // Encoding of the text file:
        if (!gotEncodings) {
            gotEncodings = true;

            wEncoding.removeAll();
            final List<Charset> values = new ArrayList<Charset>(Charset.availableCharsets().values());
            for (int i = 0; i < values.size(); i++) {
                final Charset charSet = (Charset) values.get(i);
                wEncoding.add(charSet.displayName());
            }

            // Now select the default!
            final String defEncoding = Const.getEnvironmentVariable("file.encoding", "UTF-8");
            final int idx = Const.indexOfString(defEncoding, wEncoding.getItems());
            if (idx >= 0) {
                wEncoding.select(idx);
            }
        }
    }
}
