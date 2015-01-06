package com.ibm.rtc.extensions.load.workspace.from.label.common;

import com.ibm.team.build.common.model.BuildPhase;

public abstract interface ILoadWorkspaceFromLabelConfigurationElement {

	  public static final BuildPhase BUILD_PHASE = BuildPhase.PRE_BUILD;
	  
	  public static final java.lang.String PROPERTY_BUILD_DEFINITION_ID = "com.ibm.rtc.extensions.load.workspace.from.label.common.buildDefinitionId";
	  
	  public static final java.lang.String PROPERTY_BUILD_LABEL = "com.ibm.rtc.extensions.load.workspace.from.label.common.buildLabel";
	  
	  public static final java.lang.String ELEMENT_ID = "com.ibm.rtc.extensions.load.workspace.from.label";
	  
	  public static final java.lang.String NAME = "Load Workspace From Build Label";
		  	
}
