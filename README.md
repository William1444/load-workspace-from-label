load-workspace-from-label
=========================

Rational Team Concert build engine participant.

The load workspace from label plugin does the following:
1.Takes the build definition and a build label as parameters from the user
2.Validates the inputs belonging to a successful build and creates a new workspace from the associated snapshot (referred to as a baseline set in the Rational Team Concert SDK). 
3.Updates the current build’s working workspace with the new workspace. 
4.The jazzscm plugin continues as normal with the exception that it now loads the newly created workspace that contained the same source code as the original build.

For a guide on installing and implementing Jazz plugins, please find the developer works article titled: "DevOps: Extending Rational Team Concert for Continuous Integration in Agile".
