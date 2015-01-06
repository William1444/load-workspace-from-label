package com.ibm.rtc.extensions.load.workspace.from.label.client;

/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2014. All Rights Reserved.
 * 
 *******************************************************************************/

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import com.ibm.rtc.extensions.load.workspace.from.label.common.ILoadWorkspaceFromLabelConfigurationElement;

/**
 * An editor for the Ant build configuration element.
 */
public class BuildIdAndLabelPreConfigurationElementEditor extends BasicConfigurationElementEditor {

	// ERROR messages
	private static final String ERROR_NO_BUILD_DEFINITION_ID_SUPPLIED = "Build definition id required";
	private static final String ERROR_NO_LABEL_SUPPLIED = "Build label required";
	
	// LABELs
	private static final String BUILD_AND_LABEL_CONFIGURATION_SECTION_LABEL = "Build Definition ID and Label";
	private static final String BUILD_DEFINITION_ID_CONFIG_LABEL = "Build definition id";
	private static final String BUILD_LABEL_SECTION_LABEL = "Build label";
	
	// DESCRIPTIONs 
	private static final String BUILD_DEFINITION_ID_AND_LABEL_SECTION_DESCRIPTION = "Configuration for the Load Workspace from Build Label Plugin";
	private static final String BUILD_DEFINITION_ID_CONFIG_DESCRIPTION = "Build definition id";
	private static final String BUILD_LABEL_SECTION_DESCRIPTION = "Build label";
	
	//Label for include toolkit button.
    public static String INCLUDE_TOOLKIT_BUTTON;
	
	//Toolkit description.
    public static String TOOLKIT_DESCRIPTION;

    //Toolkit label.
    public static String TOOLKIT_LABEL;
	
	protected Text fBuildDefinitionIdText;
    protected Text fBuildLabelText;
    protected Text fAntHomeText;
    protected Text fAntArgsText;
    protected Text fJavaHomeText;
    protected Text fJavaArgsText;
    protected Text fWorkingDirText;
    protected Text fPropertiesFileText;
    protected Button fIncludeToolkitButton;

    /**
     * Create the editor.
     * 
     * @param elementId
     *            The id of the build configuration element being edited.
     * @param title
     *            The editor title.
     */
    public BuildIdAndLabelPreConfigurationElementEditor(String elementId, String title) {
        super(elementId, title);
    }

    /*
     * (non-Javadoc) Intentionally not documented. See parent.
     */
    
    @Override
    public void createContent(Composite parent, FormToolkit toolkit) {
        parent.setLayout(new TableWrapLayout());

        // Build file section
       
        Section section = createSection(parent, BUILD_AND_LABEL_CONFIGURATION_SECTION_LABEL ,
        		BUILD_DEFINITION_ID_AND_LABEL_SECTION_DESCRIPTION, false);
        Composite composite = (Composite) section.getClient();

        createBuildDefinitionIdWidgets(composite);
		createBuildLabelWidgets(composite);
        
        // Configuration details section

//        createSpacer(parent, 15, 1);
        
    }

    private void createBuildDefinitionIdWidgets(Composite parent) {
        fBuildDefinitionIdText = createConfigPropertyTextField(parent, 
        		ILoadWorkspaceFromLabelConfigurationElement.PROPERTY_BUILD_DEFINITION_ID, 
        		BUILD_DEFINITION_ID_CONFIG_LABEL, 
        		BUILD_DEFINITION_ID_CONFIG_DESCRIPTION, false);
    }
    
    private void createBuildLabelWidgets(Composite parent) {
        createSpacer(parent, FIELD_SPACING, 2);
        
        fBuildLabelText = createConfigPropertyTextField(parent, 
        		ILoadWorkspaceFromLabelConfigurationElement.PROPERTY_BUILD_LABEL,
        		BUILD_LABEL_SECTION_LABEL, BUILD_LABEL_SECTION_DESCRIPTION, false);
    }

//    private void createIncludeToolkitWidgets(Composite parent) {
//        fIncludeToolkitButton = createConfigPropertyCheckboxField(parent,
//                IAntConfigurationElement.PROPERTY_INCLUDE_TOOLKIT, true,
//                TOOLKIT_LABEL,
//                INCLUDE_TOOLKIT_BUTTON,
//                TOOLKIT_DESCRIPTION, false);
//    }

    /*
     * (non-Javadoc) Intentionally not documented. See parent.
     */
    @Override
    public boolean validate() {
        boolean isValid = true;

        if (fBuildDefinitionIdText.getText().trim().equals("")) { 
            addErrorMessageForRequiredField(fBuildDefinitionIdText,
                    ERROR_NO_BUILD_DEFINITION_ID_SUPPLIED, fBuildDefinitionIdText);
            isValid = false;
        } else {
        	removeMessage(fBuildDefinitionIdText, fBuildDefinitionIdText);
        }
        if (fBuildLabelText.getText().trim().equals("")) {
        	addErrorMessageForRequiredField(fBuildLabelText,
                    ERROR_NO_LABEL_SUPPLIED, fBuildLabelText);
            isValid = false;
        } else {
        	removeMessage(fBuildLabelText, fBuildLabelText);
        }
        
        setPageStatusIndicator(!isValid, false);
        return isValid;
    }

    @Override
    public Control getFocusControl() {
        return fBuildDefinitionIdText;
    }

    /*
     * (non-Javadoc) Intentionally not documented. See parent.
     */
//    @Override
//    protected String getContextHelpId() {
//        return IHelpContextIds.HELP_CONTEXT_BUILD_DEFINITION_EDITOR_ANT_PAGE;
//    }

}
