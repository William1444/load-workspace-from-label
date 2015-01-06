package com.ibm.rtc.extensions.load.workspace.from.label.engine.utils;

import java.util.Map;
import java.util.regex.Pattern;

import com.ibm.team.build.common.model.IBuildRequest;

public class Properties {
	
	public static final String BUILD_ENV_PROPERTY= "build.env";
	
	public static String buildPropertyVariableReplacement(final IBuildRequest request,
			String stringWithBuildVariable) {
		
		final String openVariableString = "${";
		final String closeVariableString = "}";
		final String openVariableRegex = "\\$\\{";
		final String closeVariableRegex = "\\}";
		
		final String validVarRefRegex = "[a-zA-Z]+[a-zA-Z0-9_\\.]*";
		
		if (stringWithBuildVariable != null) {
				
			int indexOfLastMatch = -1;
		
			while (true) {
				int indexOfOpeningMatch = stringWithBuildVariable.indexOf(openVariableString, indexOfLastMatch); 
				int indexOfClosingMatch = -1;
				if (indexOfOpeningMatch > -1) {
					indexOfClosingMatch = stringWithBuildVariable.indexOf(closeVariableString, indexOfOpeningMatch);
					if (indexOfClosingMatch > -1) {
						//do the replacement
						String variableToReplace = stringWithBuildVariable.substring(indexOfOpeningMatch + openVariableString.length(), indexOfClosingMatch);
						// validate the match is of correct form
						if (Pattern.matches(validVarRefRegex, variableToReplace)) {
							String variableValue = buildPropertyValue(request, variableToReplace);
							if (variableValue != null) {
								stringWithBuildVariable = stringWithBuildVariable.replaceAll(openVariableRegex + variableToReplace + closeVariableRegex, 
										variableValue);
							}
						}
						indexOfLastMatch = indexOfOpeningMatch + 1;
					} else {
						break;
					}
				} else {
					break;	
				}
				
			}
			
		}
		
		return stringWithBuildVariable;
	}
	
	/**
	 * Returns build property value if exists, else returns null
	 * @param request
	 * @param name
	 * @return
	 */
	public static String buildPropertyValue(IBuildRequest request, String name) {
	    @SuppressWarnings("rawtypes")
		Map properties = request.getBuildDefinitionProperties();
	    if (properties != null) {
	        String propertyValue = (String) properties.get(name);
	        if (propertyValue != null) {
	            return propertyValue;
	        }
	    }
	    return null;
	}
	
}
