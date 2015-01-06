package com.ibm.rtc.extensions.load.workspace.from.label.client;
import com.ibm.team.build.ui.editors.builddefinition.AbstractConfigurationElementEditor;
import com.ibm.team.build.ui.editors.builddefinition.IConfigurationElementEditorFactory;

public class ConfigurationElementEditorFactory implements IConfigurationElementEditorFactory {

	public ConfigurationElementEditorFactory() {
		
	}

	@Override
	public AbstractConfigurationElementEditor createElementEditor(String elementId, String editorTitle) {
		return new BuildIdAndLabelPreConfigurationElementEditor(elementId, editorTitle);
	}
	
	

}
