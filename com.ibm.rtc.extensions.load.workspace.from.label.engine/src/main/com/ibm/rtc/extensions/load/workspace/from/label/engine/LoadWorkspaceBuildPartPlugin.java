package com.ibm.rtc.extensions.load.workspace.from.label.engine;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LoadWorkspaceBuildPartPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.rtc.extensions.load.workspace.from.label.engine"; //$NON-NLS-1$

	// The shared instance
	private static LoadWorkspaceBuildPartPlugin plugin;
	
	/**
	 * The constructor
	 */
	public LoadWorkspaceBuildPartPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		@SuppressWarnings("unused")
		Plugin plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LoadWorkspaceBuildPartPlugin getDefault() {
		return plugin;
	}
}
