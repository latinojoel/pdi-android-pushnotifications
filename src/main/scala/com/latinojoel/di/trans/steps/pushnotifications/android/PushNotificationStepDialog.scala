package com.latinojoel.di.trans.steps.pushnotifications.android

import collection.JavaConversions._
import org.pentaho.di.ui.trans.step.BaseStepDialog
import org.eclipse.swt.widgets.Shell
import org.pentaho.di.trans.TransMeta
import org.pentaho.di.trans.step.StepDialogInterface
import org.pentaho.di.trans.step.BaseStepMeta
import org.eclipse.swt.layout.FormData
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Group
import org.eclipse.swt.widgets.MessageBox
import org.eclipse.swt.SWT
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.layout.FormLayout
import org.pentaho.di.core.Const
import org.pentaho.di.i18n.BaseMessages
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.TableItem
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.ShellAdapter
import org.eclipse.swt.events.ShellEvent
import org.eclipse.swt.widgets.Composite
import org.pentaho.di.ui.core.widget.TextVar
import org.pentaho.di.ui.core.widget.TableView
import org.pentaho.di.ui.core.widget.ColumnInfo
import java.util.ArrayList
import scala.collection.mutable.ListBuffer
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.pentaho.di.core.Props
import org.eclipse.swt.custom.CCombo
import org.pentaho.di.ui.core.widget.ComboVar
import org.eclipse.swt.events.FocusListener
import org.eclipse.swt.graphics.Cursor
import java.nio.charset.Charset
import org.pentaho.di.core.row.RowMetaInterface
import org.pentaho.di.core.exception.KettleException
import org.pentaho.di.ui.core.dialog.ErrorDialog
import scala.collection.immutable.HashMap
import org.pentaho.di.trans.step.StepMeta
import java.util.SortedMap

/**
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 */
class PushNotificationStepDialog(parent: Shell, in: BaseStepMeta, transMeta: TransMeta, sname: String) extends BaseStepDialog(parent: Shell, in: BaseStepMeta, transMeta: TransMeta, sname: String) with StepDialogInterface {
  val PKG: Class[PushNotificationStepDialog] = classOf[PushNotificationStepDialog]
  // List of ColumnInfo that should have the field names of the selected database table
  val tableFieldColumns: List[ColumnInfo] = List[ColumnInfo]()

  var input: PushNotificationStepMeta = in.asInstanceOf[PushNotificationStepMeta]
  var wValCollapseKeyField, wTimeToLiveField, wRestrictedPackageField, wAPIKeyField, wRetriesNumberField, wDelayRetryNumberField: TextVar = _
  var wResponseField: Text = _
  var wFields: TableView = _
  var wGetFields, wDelayWhileIdleField, wDryRunField, wRetryingField: Button = _
  var fdGetFields: FormData = _
  var ciFields: Array[ColumnInfo] = _
  var wTabFolder: CTabFolder = _
  var wMainOptionsTab, wPropTab: CTabItem = _
  var wRegIdField: CCombo = _
  var wEncoding: ComboVar = _
  var gotEncodings, gotPreviousFields: Boolean = false
  var inputFields: Map[String, Int] = new HashMap[String, Int]()

  def open(): String = {
    val parent: Shell = getParent()
    val display: Display = parent.getDisplay()

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
    props.setLook(shell)
    setShellImage(shell, this.input)

    var lsMod: ModifyListener = new ModifyListener() {
      override def modifyText(e: ModifyEvent) = {
        input.setChanged()
      }
    }
    backupChanged = input.hasChanged()

    val formLayout: FormLayout = new FormLayout()
    formLayout.marginWidth = Const.FORM_MARGIN
    formLayout.marginHeight = Const.FORM_MARGIN
    shell.setLayout(formLayout)
    shell.setText(BaseMessages.getString(PKG, "AndroidPushNotification.Shell.Title"))

    val middle: Int = props.getMiddlePct()
    val margin: Int = Const.MARGIN

    // Stepname line
    wlStepname = new Label(shell, SWT.RIGHT)
    wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"))
    props.setLook(wlStepname)
    fdlStepname = new FormData()
    fdlStepname.left = new FormAttachment(0, 0)
    fdlStepname.right = new FormAttachment(middle, -margin)
    fdlStepname.top = new FormAttachment(0, margin)
    wlStepname.setLayoutData(fdlStepname)

    wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    wStepname.setText(stepname)
    props.setLook(wStepname)
    wStepname.addModifyListener(lsMod)
    fdStepname = new FormData()
    fdStepname.left = new FormAttachment(middle, 0)
    fdStepname.top = new FormAttachment(0, margin)
    fdStepname.right = new FormAttachment(100, 0)
    wStepname.setLayoutData(fdStepname)

    wTabFolder = new CTabFolder(shell, SWT.BORDER);
    props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

    ///////////////////////////////////
    // START OF Main Options TAB     //
    ///////////////////////////////////
    wMainOptionsTab = new CTabItem(wTabFolder, SWT.NONE)
    wMainOptionsTab.setText(BaseMessages.getString(PKG, "AndroidPushNotification.MainOptionTab.CTabItem.Title"))

    val mainOptionsLayout: FormLayout = new FormLayout()
    mainOptionsLayout.marginWidth = 3
    mainOptionsLayout.marginHeight = 3

    val wMainOptionsComp: Composite = new Composite(wTabFolder, SWT.NONE)
    props.setLook(wMainOptionsComp)
    wMainOptionsComp.setLayout(mainOptionsLayout)

    // Registration Id field
    val wlRegIdField = new Label(wMainOptionsComp, SWT.RIGHT)
    wlRegIdField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.wlRegIdField.Label"))
    props.setLook(wlRegIdField)
    val fdlRegIdField: FormData = new FormData()
    fdlRegIdField.left = new FormAttachment(0, -margin)
    fdlRegIdField.top = new FormAttachment(0, margin)
    fdlRegIdField.right = new FormAttachment(middle, -2 * margin)
    wlRegIdField.setLayoutData(fdlRegIdField)

    wRegIdField = new CCombo(wMainOptionsComp, SWT.BORDER | SWT.READ_ONLY)
    wRegIdField.setEditable(true)
    props.setLook(wRegIdField)
    wRegIdField.addModifyListener(lsMod)
    val fdRegIdField = new FormData()
    fdRegIdField.left = new FormAttachment(middle, -margin)
    fdRegIdField.top = new FormAttachment(0, margin)
    fdRegIdField.right = new FormAttachment(100, -margin)
    wRegIdField.setLayoutData(fdRegIdField)
    wRegIdField.addFocusListener(new FocusListener() {
      def focusLost(e: org.eclipse.swt.events.FocusEvent) = {}
      def focusGained(e: org.eclipse.swt.events.FocusEvent) = getRegIdFields()
    })

    // //////////////////////////////////
    // START OF OPTIONAL FIELDS GROUP  //
    /////////////////////////////////////
    val gOptionalFields: Group = new Group(wMainOptionsComp, SWT.SHADOW_NONE);
    props.setLook(gOptionalFields);
    gOptionalFields.setText(BaseMessages.getString(PKG, "AndroidPushNotification.gOptionalFields.Label"));
    val flOptionalFieldsLayout: FormLayout = new FormLayout()
    flOptionalFieldsLayout.marginWidth = 10
    flOptionalFieldsLayout.marginHeight = 10
    gOptionalFields.setLayout(flOptionalFieldsLayout)

    // collapse key value
    val wlValCollapseKeyField = new Label(gOptionalFields, SWT.RIGHT)
    wlValCollapseKeyField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.CollapseKeyField.Label"))
    props.setLook(wlValCollapseKeyField)

    val fdlValCollapseKeyField: FormData = new FormData();
    fdlValCollapseKeyField.left = new FormAttachment(0, 0)
    fdlValCollapseKeyField.right = new FormAttachment(middle, -margin)
    fdlValCollapseKeyField.top = new FormAttachment(wRegIdField, margin)
    wlValCollapseKeyField.setLayoutData(fdlValCollapseKeyField)

    wValCollapseKeyField = new TextVar(this.transMeta, gOptionalFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    props.setLook(wValCollapseKeyField)
    wValCollapseKeyField.addModifyListener(lsMod)
    val fdValCollapseKeyFeild: FormData = new FormData()
    fdValCollapseKeyFeild.left = new FormAttachment(middle, 0)
    fdValCollapseKeyFeild.right = new FormAttachment(100, 0)
    fdValCollapseKeyFeild.top = new FormAttachment(wRegIdField, margin)
    wValCollapseKeyField.setLayoutData(fdValCollapseKeyFeild)

    //Delay while idle value
    val wlDelayWhileIdleField = new Label(gOptionalFields, SWT.RIGHT)
    wlDelayWhileIdleField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.DelayWhileIdleField.Label"))
    props.setLook(wlDelayWhileIdleField)
    val fdlDelayWhileIdleField: FormData = new FormData()
    fdlDelayWhileIdleField.left = new FormAttachment(0, -margin)
    fdlDelayWhileIdleField.top = new FormAttachment(wValCollapseKeyField, margin)
    fdlDelayWhileIdleField.right = new FormAttachment(middle, -2 * margin)
    wlDelayWhileIdleField.setLayoutData(fdlDelayWhileIdleField)

    wDelayWhileIdleField = new Button(gOptionalFields, SWT.CHECK)
    props.setLook(wDelayWhileIdleField)
    val fdDelayWhileIdleField: FormData = new FormData()
    fdDelayWhileIdleField.left = new FormAttachment(middle, 0)
    fdDelayWhileIdleField.right = new FormAttachment(100, 0)
    fdDelayWhileIdleField.top = new FormAttachment(wValCollapseKeyField, margin)
    wDelayWhileIdleField.setLayoutData(fdDelayWhileIdleField)

    //Time to live value
    val wlTimeToLiveField = new Label(gOptionalFields, SWT.RIGHT)
    wlTimeToLiveField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.TimeToLiveField.Label"))
    props.setLook(wlTimeToLiveField)
    val fdlTimeToLiveField: FormData = new FormData()
    fdlTimeToLiveField.left = new FormAttachment(0, -margin)
    fdlTimeToLiveField.top = new FormAttachment(wDelayWhileIdleField, margin)
    fdlTimeToLiveField.right = new FormAttachment(middle, -2 * margin)
    wlTimeToLiveField.setLayoutData(fdlTimeToLiveField)

    wTimeToLiveField = new TextVar(this.transMeta, gOptionalFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    props.setLook(wTimeToLiveField)
    val fdTimeToLiveField: FormData = new FormData()
    fdTimeToLiveField.left = new FormAttachment(middle, 0)
    fdTimeToLiveField.top = new FormAttachment(wDelayWhileIdleField, margin)
    fdTimeToLiveField.right = new FormAttachment(100, 0)
    wTimeToLiveField.setLayoutData(fdTimeToLiveField)

    //Restricted package name value
    val wlRestrictedPackageField = new Label(gOptionalFields, SWT.RIGHT)
    wlRestrictedPackageField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.RestrictedPackageField.Label"))
    props.setLook(wlRestrictedPackageField)
    val fdlRestrictedPackageField: FormData = new FormData()
    fdlRestrictedPackageField.left = new FormAttachment(0, -margin)
    fdlRestrictedPackageField.top = new FormAttachment(wTimeToLiveField, margin)
    fdlRestrictedPackageField.right = new FormAttachment(middle, -2 * margin)
    wlRestrictedPackageField.setLayoutData(fdlRestrictedPackageField)

    wRestrictedPackageField = new TextVar(this.transMeta, gOptionalFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    props.setLook(wRestrictedPackageField)
    wRestrictedPackageField.setToolTipText(BaseMessages.getString(PKG, "GetFileNamesDialog.FileField.Tooltip"))
    val fdRestrictedPackageField: FormData = new FormData()
    fdRestrictedPackageField.left = new FormAttachment(middle, 0)
    fdRestrictedPackageField.top = new FormAttachment(wTimeToLiveField, margin)
    fdRestrictedPackageField.right = new FormAttachment(100, 0)
    wRestrictedPackageField.setLayoutData(fdRestrictedPackageField)

    //Dry run value
    val wlDryRunField = new Label(gOptionalFields, SWT.RIGHT)
    wlDryRunField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.DryRunField.Label"))
    props.setLook(wlDryRunField)
    val fdlDryRunField: FormData = new FormData()
    fdlDryRunField.left = new FormAttachment(0, -margin)
    fdlDryRunField.top = new FormAttachment(wRestrictedPackageField, margin)
    fdlDryRunField.right = new FormAttachment(middle, -2 * margin)
    wlDryRunField.setLayoutData(fdlDryRunField)

    wDryRunField = new Button(gOptionalFields, SWT.CHECK)
    props.setLook(wDryRunField)
    val fdDryRunField: FormData = new FormData()
    fdDryRunField.left = new FormAttachment(middle, 0)
    fdDryRunField.top = new FormAttachment(wRestrictedPackageField, margin)
    fdDryRunField.right = new FormAttachment(100, 0)
    wDryRunField.setLayoutData(fdDryRunField)

    val fdOutpuFields: FormData = new FormData()
    fdOutpuFields.left = new FormAttachment(0, margin)
    fdOutpuFields.top = new FormAttachment(wRegIdField, 2 * margin)
    fdOutpuFields.right = new FormAttachment(100, -margin)
    gOptionalFields.setLayoutData(fdOutpuFields)
    // /////////////////////////////////
    // End OF OPTIONAL FIELDS GROUP   //
    // /////////////////////////////////

    // The fields table
    val wlFields = new Label(wMainOptionsComp, SWT.NONE)
    wlFields.setText(BaseMessages.getString(PKG, "AndroidPushNotification.InsertFields.Label"))
    props.setLook(wlFields)
    val fdlUpIns: FormData = new FormData()
    fdlUpIns.left = new FormAttachment(0, 0)
    fdlUpIns.top = new FormAttachment(gOptionalFields, margin)
    wlFields.setLayoutData(fdlUpIns)

    val tableCols: Int = 2
    val UpInsRows: Int = (if (input.getFieldStream() != null) input.getFieldStream().length else 1)

    ciFields = Array.ofDim[ColumnInfo](tableCols)
    ciFields(0) = new ColumnInfo(BaseMessages.getString(PKG, "AndroidPushNotification.ColumnInfo.PushField"), ColumnInfo.COLUMN_TYPE_CCOMBO, Array[String](""), false)
    ciFields(1) = new ColumnInfo(BaseMessages.getString(PKG, "AndroidPushNotification.ColumnInfo.StreamField"), ColumnInfo.COLUMN_TYPE_CCOMBO, Array[String](""), false)
    List(ciFields(0)) ::: tableFieldColumns
    wFields = new TableView(transMeta, wMainOptionsComp,
      SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
      ciFields, UpInsRows, lsMod, props)

    wGetFields = new Button(wMainOptionsComp, SWT.PUSH)
    wGetFields.setText(BaseMessages.getString(PKG, "AndroidPushNotification.GetFields.Button"))
    fdGetFields = new FormData()
    fdGetFields.top = new FormAttachment(wlFields, margin)
    fdGetFields.right = new FormAttachment(100, 0)
    wGetFields.setLayoutData(fdGetFields)

    val fdFields: FormData = new FormData()
    fdFields.left = new FormAttachment(0, 0)
    fdFields.top = new FormAttachment(wlFields, margin)
    fdFields.right = new FormAttachment(wGetFields, -margin)
    fdFields.bottom = new FormAttachment(100, -2 * margin)
    wFields.setLayoutData(fdFields)

    val fdMainOptions: FormData = new FormData();
    fdMainOptions.left = new FormAttachment(0, 0);
    fdMainOptions.top = new FormAttachment(0, 0);
    fdMainOptions.right = new FormAttachment(100, 0);
    fdMainOptions.bottom = new FormAttachment(100, 0);
    wMainOptionsComp.setLayoutData(fdMainOptions);

    wMainOptionsComp.layout();
    wMainOptionsTab.setControl(wMainOptionsComp);
    ///////////////////////////////////
    // END OF Main Options TAB       //
    ///////////////////////////////////

    val fdMainOptionsTab = new FormData();
    fdMainOptionsTab.left = new FormAttachment(0, 0);
    fdMainOptionsTab.top = new FormAttachment(wStepname, margin);
    fdMainOptionsTab.right = new FormAttachment(100, 0);
    fdMainOptionsTab.bottom = new FormAttachment(100, -50);
    wTabFolder.setLayoutData(fdMainOptionsTab);

    //
    // Properties tab...
    //
    wPropTab = new CTabItem(wTabFolder, SWT.NONE)
    wPropTab.setText(BaseMessages.getString(PKG, "AndroidPushNotification.PropTab.CTabItem.Title"))

    val wPropComp: Composite = new Composite(wTabFolder, SWT.NONE)
    props.setLook(wPropComp)

    val propsCompLayout: FormLayout = new FormLayout()
    propsCompLayout.marginWidth = Const.FORM_MARGIN
    propsCompLayout.marginHeight = Const.FORM_MARGIN
    wPropComp.setLayout(propsCompLayout)

    //API key value
    val wlAPIKeyField = new Label(wPropComp, SWT.RIGHT)
    wlAPIKeyField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.APIKeyField.Label"))
    props.setLook(wlAPIKeyField)
    val fdlAPIKeyField: FormData = new FormData()
    fdlAPIKeyField.left = new FormAttachment(0, -margin)
    fdlAPIKeyField.top = new FormAttachment(0, margin)
    fdlAPIKeyField.right = new FormAttachment(middle, -2 * margin)
    wlAPIKeyField.setLayoutData(fdlAPIKeyField)

    wAPIKeyField = new TextVar(this.transMeta, wPropComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    props.setLook(wAPIKeyField)
    val fdAPIKeyField: FormData = new FormData()
    fdAPIKeyField.left = new FormAttachment(middle, -margin)
    fdAPIKeyField.top = new FormAttachment(0, margin)
    fdAPIKeyField.right = new FormAttachment(100, -margin)
    wAPIKeyField.setLayoutData(fdAPIKeyField)

    // response value
    val wlResponseField = new Label(wPropComp, SWT.RIGHT)
    wlResponseField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.ResponseField.Label"))
    props.setLook(wlResponseField)
    val fdlResponseField: FormData = new FormData()
    fdlResponseField.left = new FormAttachment(0, -margin)
    fdlResponseField.top = new FormAttachment(wAPIKeyField, margin)
    fdlResponseField.right = new FormAttachment(middle, -2 * margin)
    wlResponseField.setLayoutData(fdlResponseField)

    wResponseField = new Text(wPropComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    props.setLook(wResponseField)
    val fdResponseField: FormData = new FormData()
    fdResponseField.left = new FormAttachment(middle, -margin)
    fdResponseField.top = new FormAttachment(wAPIKeyField, margin)
    fdResponseField.right = new FormAttachment(100, -margin)
    wResponseField.setLayoutData(fdResponseField)

    // Push encoding value
    val wlEncoding: Label = new Label(wPropComp, SWT.RIGHT);
    wlEncoding.setText(BaseMessages.getString(PKG, "AndroidPushNotification.Encoding.Label"));
    props.setLook(wlEncoding)
    val fdlEncoding: FormData = new FormData()
    fdlEncoding.left = new FormAttachment(0, -margin)
    fdlEncoding.top = new FormAttachment(wResponseField, margin)
    fdlEncoding.right = new FormAttachment(middle, -2 * margin)
    wlEncoding.setLayoutData(fdlEncoding)

    wEncoding = new ComboVar(transMeta, wPropComp, SWT.BORDER | SWT.READ_ONLY)
    wEncoding.setEditable(true)
    props.setLook(wEncoding)
    wEncoding.addModifyListener(lsMod)
    val fdEncoding: FormData = new FormData()
    fdEncoding.left = new FormAttachment(middle, -margin)
    fdEncoding.top = new FormAttachment(wResponseField, margin)
    fdEncoding.right = new FormAttachment(100, -margin)
    wEncoding.setLayoutData(fdEncoding)

    wEncoding.addFocusListener(new FocusListener() {
      def focusLost(e: org.eclipse.swt.events.FocusEvent) = {
      }

      def focusGained(e: org.eclipse.swt.events.FocusEvent) {
        val busy: Cursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT)
        shell.setCursor(busy)
        setEncodings()
        shell.setCursor(null)
        busy.dispose()
      }
    })

    //Retrying in case of unavailability
    val wlRetryingField = new Label(wPropComp, SWT.RIGHT)
    wlRetryingField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.RetryingField.Label"))
    props.setLook(wlRetryingField)
    val fdlRetryingField: FormData = new FormData()
    fdlRetryingField.left = new FormAttachment(0, -margin)
    fdlRetryingField.top = new FormAttachment(wEncoding, margin)
    fdlRetryingField.right = new FormAttachment(middle, -2 * margin)
    wlRetryingField.setLayoutData(fdlRetryingField)

    wRetryingField = new Button(wPropComp, SWT.CHECK)
    props.setLook(wRetryingField)
    val fdRetryingField: FormData = new FormData()
    fdRetryingField.left = new FormAttachment(middle, -margin)
    fdRetryingField.top = new FormAttachment(wEncoding, margin)
    fdRetryingField.right = new FormAttachment(100, -margin)
    wRetryingField.setLayoutData(fdRetryingField)
    wRetryingField.addSelectionListener(
      new SelectionAdapter() {
        override def widgetSelected(arg0: SelectionEvent) = {
          wRetriesNumberField.setEnabled(wRetryingField.getSelection())
          wDelayRetryNumberField.setEnabled(wRetryingField.getSelection())
          //setFlags();
        }
      })

    //Retries number value
    val wlRetriesNumberField = new Label(wPropComp, SWT.RIGHT)
    wlRetriesNumberField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.RetriesNumberField.Label"))
    props.setLook(wlRetriesNumberField)
    val fdlRetriesNumberField: FormData = new FormData()
    fdlRetriesNumberField.left = new FormAttachment(0, -margin)
    fdlRetriesNumberField.top = new FormAttachment(wRetryingField, margin)
    fdlRetriesNumberField.right = new FormAttachment(middle, -2 * margin)
    wlRetriesNumberField.setLayoutData(fdlRetriesNumberField)

    wRetriesNumberField = new TextVar(this.transMeta, wPropComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    props.setLook(wRetriesNumberField)
    val fdRetriesNumberField: FormData = new FormData()
    fdRetriesNumberField.left = new FormAttachment(middle, -margin)
    fdRetriesNumberField.top = new FormAttachment(wRetryingField, margin)
    fdRetriesNumberField.right = new FormAttachment(100, -margin)
    wRetriesNumberField.setLayoutData(fdRetriesNumberField)

    //Delay before last retry in seconds
    val wlDelayRetryNumberField = new Label(wPropComp, SWT.RIGHT)
    wlDelayRetryNumberField.setText(BaseMessages.getString(PKG, "AndroidPushNotification.DelayRetryNumberField.Label"))
    props.setLook(wlDelayRetryNumberField)
    val fdlDelayRetryNumberField: FormData = new FormData()
    fdlDelayRetryNumberField.left = new FormAttachment(0, -margin)
    fdlDelayRetryNumberField.top = new FormAttachment(wRetriesNumberField, margin)
    fdlDelayRetryNumberField.right = new FormAttachment(middle, -2 * margin)
    wlDelayRetryNumberField.setLayoutData(fdlDelayRetryNumberField)

    wDelayRetryNumberField = new TextVar(this.transMeta, wPropComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    props.setLook(wDelayRetryNumberField)
    val fdDelayRetryNumberField: FormData = new FormData()
    fdDelayRetryNumberField.left = new FormAttachment(middle, -margin)
    fdDelayRetryNumberField.top = new FormAttachment(wRetriesNumberField, margin)
    fdDelayRetryNumberField.right = new FormAttachment(100, -margin)
    wDelayRetryNumberField.setLayoutData(fdDelayRetryNumberField)
    //
    // End Properties tab...
    //

    val fdPropsComp: FormData = new FormData();
    fdPropsComp.left = new FormAttachment(0, 0);
    fdPropsComp.top = new FormAttachment(0, 0);
    fdPropsComp.right = new FormAttachment(100, 0);
    fdPropsComp.bottom = new FormAttachment(100, 0);
    wPropComp.setLayoutData(fdPropsComp);

    wPropComp.layout();
    wPropTab.setControl(wPropComp);

    // OK and cancel buttons
    wOK = new Button(shell, SWT.PUSH)
    wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"))
    wCancel = new Button(shell, SWT.PUSH)
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"))

    BaseStepDialog.positionBottomButtons(shell, Array[Button](wOK, wCancel), margin, null)

    val fdTabFolder: FormData = new FormData();
    fdTabFolder.left = new FormAttachment(0, 0);
    fdTabFolder.top = new FormAttachment(wStepname, margin);
    fdTabFolder.right = new FormAttachment(100, 0);
    fdTabFolder.bottom = new FormAttachment(wOK, -margin);
    wTabFolder.setLayoutData(fdTabFolder);

    wTabFolder.setSelection(0);

    // 
    // Search the fields in the background
    //
    val runnable: Runnable = new Runnable() {
      def run() = {
        val stepMeta: StepMeta = transMeta.findStep(stepname);
        if (stepMeta != null) {
          try {
            val row: RowMetaInterface = transMeta.getPrevStepFields(stepMeta)
            // Remember these fields...
            var i: Int = 0
            if (row != null) {
              for (fieldName <- row.getFieldNames()) {
                inputFields += fieldName -> i
                i += 1
              }
            }
            setComboBoxes()
          } catch {
            case e: KettleException => logError(BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
          }
        }
      }
    };
    new Thread(runnable).start();

    // Add listeners
    lsCancel = new Listener() {
      def handleEvent(e: Event) = {
        cancel()
      }
    }
    lsOK = new Listener() {
      def handleEvent(e: Event) = {
        ok()
      }
    }
    lsGet = new Listener() { def handleEvent(e: Event) = { get() } }

    wCancel.addListener(SWT.Selection, lsCancel)
    wOK.addListener(SWT.Selection, lsOK)
    wGetFields.addListener(SWT.Selection, lsGet);

    lsDef = new SelectionAdapter() {
      override def widgetDefaultSelected(e: SelectionEvent) = {
        ok()
      }
    }
    wStepname.addSelectionListener(lsDef)

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener(new ShellAdapter() {
      override def shellClosed(e: ShellEvent) = {
        cancel()
      }
    })

    // Set the shell size, based upon previous time...
    setSize()

    getData()
    input.setChanged(backupChanged)

    shell.open()
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep()
    }
    stepname
  }

  // Read data and place it in the dialog
  def getData() = {
    wRegIdField.setText(input.getRegistrationId)
    if (input.getCollapseKey ne null)
      wValCollapseKeyField.setText(input.getCollapseKey)
    wDelayWhileIdleField.setSelection(input.isDelayWhileIdle)
    if (input.getTimeToLive ne null)
      wTimeToLiveField.setText(String.valueOf(input.getTimeToLive))
    if (input.getRestrictedPackageName != null)
      wRestrictedPackageField.setText(input.getRestrictedPackageName)
    wDryRunField.setSelection(input.isDryRun)
    if (input.getApiKey ne null)
      wAPIKeyField.setText(input.getApiKey)
    if (input.getResponseField != null)
      wResponseField.setText(input.getResponseField)
    if (input.getPushEncoding ne null)
      wEncoding.setText(input.getPushEncoding)
    wRetryingField.setSelection(input.isRetrying)
    if (input.getRetryNumber ne null)
      wRetriesNumberField.setText(String.valueOf(input.getRetryNumber))
    wRetriesNumberField.setEnabled(input.isRetrying)
    if (input.getDelayBeforeLastRetry ne null)
      wDelayRetryNumberField.setText(String.valueOf(input.getDelayBeforeLastRetry))
    wDelayRetryNumberField.setEnabled(input.isRetrying)

    if (input.getFieldStream() != null && input.getDataFieldPush() != null) {
      var i: Int = 0
      while (i < input.getDataFieldPush().size) {
        val item: TableItem = wFields.table.getItem(i);
        if (input.getDataFieldPush.apply(i) != null) item.setText(1, input.getDataFieldPush.apply(i));
        if (input.getFieldStream.apply(i) != null) item.setText(2, input.getFieldStream.apply(i));
        i += 1
      }
    }

    wStepname.selectAll()
  }

  def cancel() = {
    stepname = null
    input.setChanged(backupChanged)
    dispose()
  }

  // let the plugin know about the entered data
  def ok() = {
    if (!Const.isEmpty(wStepname.getText())) {
      stepname = wStepname.getText();
      getInfo(input);
      if (input.getRegistrationId == null || input.getRegistrationId.equals("")) {
        val mb: MessageBox = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR)
        mb.setMessage(BaseMessages.getString(PKG, "AndroidPushNotification.RegistrationIdError.DialogMessage"))
        mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"))
        mb.open()
      } else if (input.getApiKey == null || input.getApiKey.equals("")) {
        val mb: MessageBox = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR)
        mb.setMessage(BaseMessages.getString(PKG, "AndroidPushNotification.APIKeyError.DialogMessage"))
        mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"))
        mb.open()
      } else if (input.getResponseField == null || input.getResponseField.equals("")) {
        val mb: MessageBox = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR)
        mb.setMessage(BaseMessages.getString(PKG, "AndroidPushNotification.ResponseFieldError.DialogMessage"))
        mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"))
        mb.open()
      } else if (input.getPushEncoding == null || input.getPushEncoding.equals("")) {
        val mb: MessageBox = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR)
        mb.setMessage(BaseMessages.getString(PKG, "AndroidPushNotification.PushEncodingError.DialogMessage"))
        mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"))
        mb.open()
      } else {
        stepname = wStepname.getText()
        dispose()
      }
    }
  }

  def getInfo(info: PushNotificationStepMeta) = {
    input.setRegistrationId(wRegIdField.getText())
    input.setCollapseKey(wValCollapseKeyField.getText())
    input.setDelayWhileIdle(wDelayWhileIdleField.getSelection())
    input.setTimeToLive(wTimeToLiveField.getText())
    input.setRestrictedPackageName(wRestrictedPackageField.getText())
    input.setDryRun(wDryRunField.getSelection())
    input.setApiKey(wAPIKeyField.getText())
    input.setResponseField(wResponseField.getText())
    input.setPushEncoding(wEncoding.getText())
    input.setRetrying(wRetryingField.getSelection())
    input.setRetryNumber(wRetriesNumberField.getText())
    input.setDelayBeforeLastRetry(wDelayRetryNumberField.getText())

    val nrRows: Int = wFields.nrNonEmpty();
    val fieldStream: ListBuffer[String] = new ListBuffer[String]
    val dataFieldPush: ListBuffer[String] = new ListBuffer[String]
    var i: Int = 0
    while (i < nrRows) {
      val item: TableItem = wFields.getNonEmpty(i)
      dataFieldPush += Const.NVL(item.getText(1), "")
      fieldStream += Const.NVL(item.getText(2), "")
      i += 1
    }
    input.setFieldStream(fieldStream)
    input.setDataFieldPush(dataFieldPush)
  }

  def getRegIdFields() = {
    if (!gotPreviousFields) {
      gotPreviousFields = true;
      try {
        val source: String = wRegIdField.getText();
        wRegIdField.removeAll();
        val r: RowMetaInterface = transMeta.getPrevStepFields(stepname);
        if (r != null) {
          wRegIdField.setItems(r.getFieldNames());
          if (source != null) wRegIdField.setText(source);
        }
      } catch {
        case ke: KettleException => new ErrorDialog(shell,
          BaseMessages.getString(PKG, "SyslogMessageDialog.FailedToGetFields/.DialogTitle"),
          BaseMessages.getString(PKG, "SyslogMessageDialog.FailedToGetFields.DialogMessage"), ke)
      }
    }
  }

  /**
   * Fill up the fields table with the incoming fields.
   */
  private def get() = {
    try {
      val r: RowMetaInterface = transMeta.getPrevStepFields(stepname);
      if (r != null && !r.isEmpty()) {
        BaseStepDialog.getFieldsFromPrevious(r, wFields, 1, Array[Int](1, 2), Array[Int](), -1, -1, null);
      }
    } catch {
      case ke: KettleException => new ErrorDialog(shell,
        BaseMessages.getString(PKG, "AndroidPushNotification.FailedToGetFields.DialogTitle"),
        BaseMessages.getString(PKG, "AndroidPushNotification.FailedToGetFields.DialogMessage"), ke)
    }

  }

  def setComboBoxes() = {
    // Something was changed in the row.
    val fields: Map[String, Int] = inputFields
    val keySet: scala.collection.Set[String] = fields.keySet
    val fieldNames: Array[String] = keySet.toArray
    Const.sortStrings(fieldNames)
    //    val columnInfo: ColumnInfo = ciFields.apply(1)
    //    columnInfo.setComboValues(fieldNames)
    ciFields.apply(1).setComboValues(fieldNames)
  }

  def setEncodings() = {
    // Encoding of the text file:
    if (!gotEncodings) {
      gotEncodings = true
      wEncoding.removeAll()
      val values: Iterable[Charset] = Charset.availableCharsets().values()
      values.foreach(v => wEncoding.add(v.displayName()))
      // Now select the default!
      val defEncoding: String = Const.getEnvironmentVariable("file.encoding", "UTF-8")
      val idx: Int = Const.indexOfString(defEncoding, wEncoding.getItems())
      if (idx >= 0)
        wEncoding.select(idx)
      else
        wEncoding.select(Const.indexOfString("UTF-8", wEncoding.getItems()));
    }
  }
}