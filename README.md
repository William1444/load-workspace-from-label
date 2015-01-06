load-workspace-from-label
=========================

Rational Team Concert build engine participant.

The load workspace from label plugin does the following:
1.Takes the build definition and a build label as parameters from the user
2.Validates the inputs belonging to a successful build and creates a new workspace from the associated snapshot (referred to as a baseline set in the Rational Team Concert SDK). 
3.Updates the current build’s working workspace with the new workspace. 
4.The jazzscm plugin continues as normal with the exception that it now loads the newly created workspace that contained the same source code as the original build.

For a guide on installing and implementing Jazz plugins, please find the developer works article titled: "DevOps: Extending Rational Team Concert for Continuous Integration in Agile".

USAGE - development

full implementation details are availabe on developer works for a simplified plugin: "DevOps: Extending Rational Team Concert for Continuous Integration in Agile".

to develop with the plugin, first download all components: https://github.com/William1444/load-workspace-from-label.git.

Create two workspaces in eclipse and for each switch to the Plugin Development View. One should be named load-workspace-from-label-client and the other load-workspace-from-label-engine. 

com.ibm.rtc.extensions.load.workspace.from.label.client  com.ibm.rtc.extensions.load.workspace.from.label.engine   com.ibm.rtc.extensions.load.workspace.from.label.updatesite
com.ibm.rtc.extensions.load.workspace.from.label.common  com.ibm.rtc.extensions.load.workspace.from.label.feature

In the client workspace, import the two plugin projects: com.ibm.rtc.extensions.load.workspace.from.label.client and com.ibm.rtc.extensions.load.workspace.from.label.common. Set the target platform to the unzipped Rational Team Concert SDK you downloaded as part of Setting up the Development Environment. In the project settings for the client plugin, create a project dependency on the common plugin. 

In the engine workspace, import the engine project named bm.rtc.extensions.load.workspace.from.label.engine and import the common project named com.ibm.rtc.extensions.load.workspace.from.label.common. Set the target platform to ${JBE_HOME}/buildsystem/buildengine/eclipse/plugins. Create a project dependency on common.


USAGE - using the plugin

To install and use the plugin, Follow the article "DevOps: Extending Rational Team Concert for Continuous Integration in Agile" on developerWorks, specifically section "Build and Distribution".
