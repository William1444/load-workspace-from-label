package com.ibm.rtc.extensions.load.workspace.from.label.engine.utils;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ibm.rtc.extensions.load.workspace.from.label.common.ILoadWorkspaceFromLabelConfigurationElement;

public class BuildParticipantLogger {
	public static void info(final PrintWriter logger, final String log) {
		logger.println(logEntryTimestamp() + " [" 
				+ ILoadWorkspaceFromLabelConfigurationElement.NAME + "]" + " " + log);
	}
	
	public static void error(final PrintWriter logger, final String log) {
		logger.println(logEntryTimestamp() + " [" 
				+ ILoadWorkspaceFromLabelConfigurationElement.NAME + "]" + " [ERROR] " + log);
	}
	
	private static String logEntryTimestamp() {
		SimpleDateFormat sdf1 = new SimpleDateFormat();
        sdf1.applyPattern("yyy-MM-dd HH:mm:ss");
        return sdf1.format((Calendar.getInstance().getTime()));
	}
	
}
