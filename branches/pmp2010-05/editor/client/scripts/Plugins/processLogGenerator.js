/*global ORYX, Ext, Ajax*/ //for JSLint

if (!ORYX.Plugins) {
    ORYX.Plugins = {};
}

/**
 * A Plugin for generating Process-Logs from Petrinets. This plugin uses a
 * server-side plugin to actually generate the log. This client-side plugin
 * only shows an options-dialog, sends the log-generation-request to the server
 * and offers the generated log for download. 
 * 
 * @class
 * @author Thomas Milde
 * */
ORYX.Plugins.ProcessLogGenerator = ORYX.Plugins.AbstractPlugin.extend({
    
    /**
     * The URL, that handles Requests for creating a Process-Log
     * @constant
     * @private
     * @fieldof ORYX.Plugins.ProcessLogGenerator#
     * */
    processLogGeneratorHandleURL: ORYX.PATH + "processloggenerator",
    
    /**
     * Creates a new instance of the Process-Log-Generation plugin and registers
     * it at the supplied facade.
     * 
     * @methodof ORYX.Plugins.ProcessLogGenerator#
     * @param {Facade} facade The plugin facade (interface between
     * plugin and editor)
     * */
    construct: function (facade) {
    
        arguments.callee.$.construct.apply(this, arguments);
        // Call super class constructor
    
        this.facade.offer({
            'name'          : ORYX.I18N.ProcessLogGenerator.generate,
            'functionality' : this.perform.bind(this),
            'description'   : ORYX.I18N.ProcessLogGenerator.generateDescription,
            'icon'          : ORYX.PATH + "images/processLogGeneratorIcon.png",
            'index'         : 0,
            'minShape'      : 0,
            'maxShape'      : 0
        });    
    },
    
    /**
     * Perform the plugin's action, i.e. open a Properties-window and send a
     * request for creating a log to the server, when the form was submitted.
     * 
     * @methodof ORYX.Plugins.ProcessLogGenerator#
     * */
    perform: function () {
        var completenessSelector = this.createCompletenessSelector(),
            //the select-(drop-down-list) field for choosing, whether some kind
            //of completeness is desired.
            noiseField = this.createNoiseField(),
            //the input-field for choosing the degree of noise (percentage) in
            //the generated log
            traceCountField = this.createTraceCountField(),
            //the input-field for selecting, how many traces should be generated
            dialog = this.createDialog();
            //the dialog-window, which will present all those fields
        
        this.addFormPanelToWindow(
                completenessSelector, noiseField, traceCountField, dialog);
        //generate a FormPanel with the three fields and add it to the dialog
        
        dialog.show();
    },
    
    /**
     * Creates a ComboBox, that allows selecting, whether Trace-Completeness,
     * Ordering-Completeness or no completeness is required.
     * 
     * @methodof ORYX.Plugins.ProcessLogGenerator#
     * @return {Ext.form.ComboBox} the new ComboBox
     * */
    createCompletenessSelector: function () {
        var completenessOptions = [['None'], ['Trace'], ['Ordering']],
            dataStore = new Ext.data.SimpleStore({
                fields    : ['value'],
                data    : completenessOptions
            });
        //dataStore is necessary, because a ComboBox cannot be directly provided
        //with an array.
        
        return new Ext.form.ComboBox({
            fieldLabel: ORYX.I18N.ProcessLogGenerator.completenessSelect,
            store: dataStore,
            displayField: 'value',
            valueField: 'value',
            typeAhead: true,
            mode: 'local',
            triggerAction: 'all',
            selectOnFocus: true,
            forceSelection: true,
            emptyText: ORYX.I18N.ProcessLogGenerator.pleaseSelect
        });
    },
    
    /**
     * Creates an input-field, that allows entering a number from 0
     * to 100 (percentage), that specifies the degree of noise in the generated
     * log.
     * 
     *  @methodof ORYX.Plugins.ProcessLogGenerator#
     *  @return {Ext.form.NumberField} the new input-field
     * */
    createNoiseField: function () {
        return new Ext.form.NumberField({
            fieldLabel        : ORYX.I18N.ProcessLogGenerator.degreeOfNoise,
            allowBlank        : false,
            allowDecimals    : false,
            minValue        : 0,
            maxValue        : 100
        });
    },
    
    /**
     * Creates an input-field for a number from 1 to 10,000 specifying the
     * number of traces, that the generated log should contain.
     * 
     * @methodof ORYX.Plugins.ProcessLogGenerator#
     * @return {Ext.form.NumberField} the new input-field
     * */
    createTraceCountField: function () {
        return new Ext.form.NumberField({
            fieldLabel        : ORYX.I18N.ProcessLogGenerator.numberOfTraces,
            allowBlank        : false,
            allowDecimals    : false,
            minValue        : 1,
            maxValue        : 10000
        });
    },
    
    /**
     * Creates a new dialog-window carrying the title and having the size needed
     * for displaying the configuration-options for log-generation.
     *  
     * @methodof ORYX.Plugins.ProcessLogGenerator#
     * @return {Ext.Window} the new dialog-window.
     * */
    createDialog: function () {
        var dialog = new Ext.Window({ 
            autoCreate: true, 
            title: ORYX.I18N.ProcessLogGenerator.preferencesWindowTitle, 
            height: 240, 
            width: 400, 
            modal: true,
            collapsible: false,
            fixedcenter: true, 
            shadow: true, 
            style: 'font-size:12px;',
            proxyDrag: true,
            resizable: true,
            items: [
                new Ext.form.Label({
                    text: ORYX.I18N.ProcessLogGenerator.dialogDescription, 
                    style: 'font-size:12px;'
                })//this is the title of the dialog. the content is not added
                  //in this method.
            ]
        });
        
        dialog.on('hide', function () {
            dialog.destroy(true);
        });
        
        return dialog;
    },
    
    /**
     * Creates a new FormPanel with the first three parameters (
     * completenessSelector, noiseField and traceCountField) as its elements
     * and adds the new FormPanel to the window, which is the fourth parameter.
     * 
     * @methodof ORYX.Plugins.ProcessLogGenerator#
     * @param {Ext.form.ComboBox} completenessSelector the ComboBox, that allows
     * selecting the type of completeness and will be the first field in the 
     * Form.
     * @param {Ext.form.NumberField} noiseField the input-field, which allows
     * selecting the percentage of noise in the generated log and will be the 
     * second field of the Form.
     * @param {Ext.form.NumberField} tracecountField the input-field, which
     * allows selecting the desired number of traces and will be the last entry
     * in the Form.
     * @param {Ext.Window} window the dialog-window, which the form should be 
     * added to.
     * */
    addFormPanelToWindow: function (
            completenessSelector, noiseField, traceCountField, window) {
        var panel = new Ext.form.FormPanel({
            frame : false,
            defaultType : 'textfield',
            waitMsgTarget : true,
            labelAlign : 'left',
            buttonAlign: 'right',
            enctype : 'multipart/form-data',
            style: 'font-size:12px;',
            monitorValid: true,
            items : [
                completenessSelector,
                noiseField,
                traceCountField
            ],
            buttons: [{
                text: "Submit",
                formBind: true,//ensures, that the button can only be clicked,
                //when the form's constraints are met (e.g. all fields are 
                handler: function () {
                    this.generateLog({
                        completeness: completenessSelector.getValue(),
                        noise: noiseField.getValue(),
                        traceCount: traceCountField.getValue()
                    });//this will send a request to the server for generating
                    //a log with the desired options (->parameter)
                    window.hide();//will destroy the dialog
                }.bind(this)
            }]
        });
        window.add(panel);
    },
    
    /**
     * Sends a Request for creating a log with the desired parameters from the
     * current model to the server and opens a new window for downloading the 
     * log, when generating is completed.
     * 
     * @methodof ORYX.Plugins.ProcessLogGenerator#
     * @param {Object} options The options for generating the log. This object
     * must have the fields completeness, noise and traceCount. completeness is
     * "None", "Trace" or "Ordering"; noise is a number from 0 to 100
     * (percentage of noise in the generated log); traceCount is a number from
     * 1 to 10,000 (number of traces in the generated log).
     * */
    generateLog: function (options) {
        var erdf = this.getRDFFromDOM(),
            optionsString = JSON.stringify(options);
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE,
            text: ORYX.I18N.ProcessLogGenerator.shortWaitText
        });//will show a "please wait"-style message in the editor's topleft
        //corner (like the one for "Saving")
        new Ajax.Request(this.processLogGeneratorHandleURL, {
                method: 'POST',
                parameters: {
                    'options'    : optionsString,
                    'model'        : erdf
            },
            onSuccess: function (request) {
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });//remove the "please wait"-message
                this.openDownloadWindow("generated_log.mxml",
                        request.responseText);//request.responseText contains
                //the log, which will be offered as a file
                //named generated_log.mxml
            }.bind(this),
            
            onFailure: function () {
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });//remove the "please wait"-message
                Ext.Msg.alert(ORYX.I18N.Oryx.title,
                        ORYX.I18N.ProcessLogGenerator.failed);
                //shows an error-message
            }.bind(this)
        });
    }
    
});