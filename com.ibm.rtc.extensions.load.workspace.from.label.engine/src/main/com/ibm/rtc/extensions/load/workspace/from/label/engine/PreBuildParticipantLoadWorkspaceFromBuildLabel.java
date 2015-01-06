package com.ibm.rtc.extensions.load.workspace.from.label.engine;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.rtc.extensions.load.workspace.from.label.common.ILoadWorkspaceFromLabelConfigurationElement;
import com.ibm.rtc.extensions.load.workspace.from.label.engine.utils.BuildParticipantLogger;
import com.ibm.rtc.extensions.load.workspace.from.label.engine.utils.Properties;
import com.ibm.team.build.client.ITeamBuildClient;
import com.ibm.team.build.common.ScmConstants;
import com.ibm.team.build.common.TeamBuildException;
import com.ibm.team.build.common.model.BuildStatus;
import com.ibm.team.build.common.model.IBuildConfigurationElement;
import com.ibm.team.build.common.model.IBuildDefinition;
import com.ibm.team.build.common.model.IBuildDefinitionInstance;
import com.ibm.team.build.common.model.IBuildProperty;
import com.ibm.team.build.common.model.IBuildResult;
import com.ibm.team.build.common.model.IBuildResultContribution;
import com.ibm.team.build.common.model.IBuildResultHandle;
import com.ibm.team.build.common.model.query.IBaseBuildResultQueryModel.IBuildResultQueryModel;
import com.ibm.team.build.engine.AbstractPreBuildParticipant;
import com.ibm.team.build.internal.common.builddefinition.IJazzScmConfigurationElement;
import com.ibm.team.build.internal.engine.MissingPropertyException;
import com.ibm.team.build.internal.scm.BuildWorkspaceDescriptor;
import com.ibm.team.build.internal.scm.RepositoryManager;
import com.ibm.team.repository.client.IContributorManager;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IContributorHandle;
import com.ibm.team.repository.common.IItemHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.query.IItemQuery;
import com.ibm.team.repository.common.query.IItemQueryPage;
import com.ibm.team.repository.common.service.IQueryService;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.client.internal.WorkspaceManager;
import com.ibm.team.scm.common.IBaselineSet;
import com.ibm.team.scm.common.IBaselineSetHandle;
import com.ibm.team.scm.common.IRepositoryProgressMonitor;
import com.ibm.team.scm.common.IScmService;
import com.ibm.team.scm.common.internal.BaselineSet;
import com.ibm.team.scm.common.internal.dto.WorkspaceRefreshResult;

@SuppressWarnings("restriction")
public class PreBuildParticipantLoadWorkspaceFromBuildLabel extends
		AbstractPreBuildParticipant {

	private String buildDefinitionIdProperty;
	
	private String buildLabelProperty;

	public String getBuildDefinitionIdProperty() {
		return buildDefinitionIdProperty;
	}

	public void setBuildDefinitionIdProperty(String buildDefinitionIdProperty) {
		this.buildDefinitionIdProperty = buildDefinitionIdProperty;
	}

	public String getBuildLabelProperty() {
		return buildLabelProperty;
	}

	public void setBuildLabelProperty(String buildLabelProperty) {
		this.buildLabelProperty = buildLabelProperty;
	}

	public boolean shouldBuild(IProgressMonitor monitor) throws Exception {

        if (buildDefinitionIdProperty == null || buildDefinitionIdProperty.length() < 1) {
            throw new MissingPropertyException("buildDefinitionId is not valid!!");
        }
        if (buildLabelProperty == null || buildLabelProperty.length() < 1) {
        	throw new MissingPropertyException("buildLabel is not valid!!");		
		}
        
		BuildParticipantLogger.info(getBuildLog(), 
				"buildDefinitionId has been set to "
						+ buildDefinitionIdProperty);
		BuildParticipantLogger.info(getBuildLog(), 
				"buildLabel has been set to " + buildLabelProperty);
		
		return true;
	}

	public BaselineSet getSnapshot(ITeamBuildClient buildClient,
			IBuildResult buildResult, IProgressMonitor monitor,
			ITeamRepository repo) throws Exception {
		// == Mutate snapshot to match new buildLabel
		shouldBuild(monitor);
		IBuildResultContribution[] contributionsSnapShot = buildClient
				.getBuildResultContributions(buildResult,
						ScmConstants.EXTENDED_DATA_TYPE_ID_BUILD_SNAPSHOT,
						monitor);
		
		BaselineSet snapshot = null;
		if (contributionsSnapShot.length == 1) {
			IBuildResultContribution snapShotContribution = contributionsSnapShot[0];
			IItemHandle snapShotItemHandler = snapShotContribution
					.getExtendedContribution();

			snapshot = (BaselineSet) repo.itemManager().fetchCompleteItem(
					snapShotItemHandler, 0, monitor);
			BuildParticipantLogger.info(getBuildLog(), "valid snapshot: " + snapshot.getName());
			

		} else if (contributionsSnapShot.length > 1) {
			BuildParticipantLogger.error(getBuildLog(), "too many snapshots found! Expected 1.");
			throw new TeamBuildException(
					"Too many snapshots found! Expected 1.");
		} else {
			BuildParticipantLogger.error(getBuildLog(), "no snapshots found! Expected 1.");
			throw new TeamBuildException(
					"No snapshots found! Expected 1.");
		}

		return snapshot;
	}
	
	@Override
	public BuildStatus preBuild(IProgressMonitor monitor) throws Exception {
		
		IBuildConfigurationElement element = getBuildRequest().getBuildDefinitionInstance().getConfigurationElement(ILoadWorkspaceFromLabelConfigurationElement.ELEMENT_ID);
	
		this.buildDefinitionIdProperty = Properties.buildPropertyVariableReplacement(getBuildRequest(), element.getConfigurationProperty(
				ILoadWorkspaceFromLabelConfigurationElement.PROPERTY_BUILD_DEFINITION_ID).getValue());

		this.buildLabelProperty = Properties.buildPropertyVariableReplacement(getBuildRequest(), element.getConfigurationProperty(
				ILoadWorkspaceFromLabelConfigurationElement.PROPERTY_BUILD_LABEL).getValue());
		
		final String buildDefinitionId = buildDefinitionIdProperty;
		final String buildLabel = buildLabelProperty;
		
		final ITeamRepository repo = getTeamRepository();

		final ITeamBuildClient buildClient = (ITeamBuildClient) repo
				.getClientLibrary(ITeamBuildClient.class);
		
		final String buildRequesterUserId = repo.getUserId();
		
		final IBuildDefinitionInstance buildDefinitionInstance = getBuildRequest().getBuildDefinitionInstance();
		
		final String currentWorkspaceUUID = buildDefinitionInstance.getProperty(IJazzScmConfigurationElement.PROPERTY_WORKSPACE_UUID).getValue();
		
		if (buildRequesterUserId == null || buildRequesterUserId.length() < 1) {
        	throw new MissingPropertyException("buildRequesterUserId could not be determined!!");		
		}

        BuildParticipantLogger.info(getBuildLog(), "buildRequesterUserId has been detected as " + buildRequesterUserId);
		
		IItemQueryPage queryPage = getBuildHistory(buildClient, buildDefinitionId, buildLabel, monitor);

		final int matchedBuildLabelsSize = queryPage.getSize();
		if (matchedBuildLabelsSize == 1) {
			IBuildResult buildResult = (IBuildResult) repo.itemManager().fetchCompleteItems(queryPage.getItemHandles(),
					IItemManager.DEFAULT, monitor).get(0);
			if (buildResult.getStatus().equals(BuildStatus.OK)) {
				BuildParticipantLogger.info(getBuildLog(), 
						"Found a matching build with id: " + buildDefinitionId + " and label: " + buildLabel + " that completed successfully");
				
				BuildParticipantLogger.info(getBuildLog(), "Determining snapshot...");
				
				final IBaselineSet snapshot = getSnapshot(buildClient, buildResult,
						monitor, repo);
				
				BuildParticipantLogger.info(getBuildLog(), "Snapshot determined as " + snapshot.getName());
				
				final WorkspaceManager workspaceManager = (WorkspaceManager) SCMPlatform.getWorkspaceManager(repo);
				
				final IScmService service = workspaceManager.getServerConfigurationService();
				
				final IBaselineSetHandle baselineSetHandle = (IBaselineSetHandle) IBaselineSet.ITEM_TYPE.createItemHandle(snapshot.getItemId(),null);
				
				final String workspaceName = snapshot.getName() + " workspace";
				
				BuildParticipantLogger.info(getBuildLog(), "Switching to new workspace with snapshot id: " + snapshot.getName());
				
				final String workspaceDescription = "workspace created from build definition: " + 
						buildDefinitionId + " at build label: " + buildLabel + ". \n The purpose of creating " +
						"this workspace is to recraete this build for a new environment.";
				
				IContributorManager contributorManager = repo.contributorManager();
				
				IContributorHandle contributorHandle = contributorManager.fetchContributorByUserId(buildRequesterUserId, monitor);
				
				final WorkspaceRefreshResult result = service.createWorkspaceFromBaselineSet(contributorHandle, workspaceName,
	                    workspaceDescription, baselineSetHandle,IRepositoryProgressMonitor.ITEM_FACTORY.createItem(monitor));
				
				final String createdWorkspaceUUID = result.getWorkspace().getItemId().getUuidValue();
				
				BuildParticipantLogger.info(getBuildLog(), "Transferring ownership and visibility from current workspace in build conf: " 
				+ currentWorkspaceUUID.toString() + ", to created workspace: " + createdWorkspaceUUID);
				
				transferWorkspaceOwnerAndVisibility(currentWorkspaceUUID, createdWorkspaceUUID, repo, getRepositoryManager(), monitor);
			
				BuildParticipantLogger.info(getBuildLog(), "Switching to new workspace...");
				
				buildDefinitionInstance.getProperty(IJazzScmConfigurationElement.PROPERTY_WORKSPACE_UUID).setValue(createdWorkspaceUUID);
				
				BuildParticipantLogger.info(getBuildLog(), "Successfully switched the current build's workspace to: " + result.getWorkspace().getName() + 
						". The JazzSCM plugin should now use this workspace for loading the source...");

				IBuildProperty buildProperty = buildDefinitionInstance.getProperty(Properties.BUILD_ENV_PROPERTY);
				
				String buildEnv = null;
				if (buildProperty != null) {
					buildEnv = buildProperty.getValue();
				}
				
				String buildEnvTagSuffix = buildEnv != null && !buildEnv.isEmpty() ? "-" + buildEnv : "";
				
				tagBuild(getBuildRequest().getBuildResult(), repo, buildClient, workspaceName + buildEnvTagSuffix, monitor);
				
				return BuildStatus.OK;
			}
			BuildParticipantLogger.error(getBuildLog(), "Build with id: " + buildDefinitionId + " and label: " + buildLabel
									+ ", is not a successful build and should not be promoted!");
			return BuildStatus.ERROR;
		} else if (matchedBuildLabelsSize > 1) {
			BuildParticipantLogger.error(getBuildLog(), "Too many matching builds with id: " + buildDefinitionId + 
					" and label: " + buildLabel);
			return BuildStatus.ERROR;
		} else {
			BuildParticipantLogger.error(getBuildLog(), "No matching builds with id: " + buildDefinitionId + 
					" and label: " + buildLabel);
			return BuildStatus.ERROR;
		}
	}
	
	public void transferWorkspaceOwnerAndVisibility(
			String transferFromWorkspaceUUID, String transferToWorkspaceUUID, ITeamRepository repo, RepositoryManager repositoryManager, IProgressMonitor monitor) throws TeamRepositoryException {
		
		BuildWorkspaceDescriptor transferFromWorkspaceDesc = new BuildWorkspaceDescriptor(repo, transferFromWorkspaceUUID, null)	;
		
		BuildWorkspaceDescriptor transferToWorkspaceDesc = new BuildWorkspaceDescriptor(repo, transferToWorkspaceUUID, null);
		
		IWorkspaceConnection currentWorkspaceConnection = transferFromWorkspaceDesc.getConnection(repositoryManager, true, monitor);
		
		IWorkspaceConnection newWorkspaceConnection = transferToWorkspaceDesc.getConnection(repositoryManager, true, monitor);
		
		newWorkspaceConnection.setOwnerAndVisibility(currentWorkspaceConnection.getOwner(), currentWorkspaceConnection.getReadScope(), monitor);
		// TODO Auto-generated method stub
		
	}

	public void tagBuild(IBuildResultHandle buildResultHandle, ITeamRepository repo, ITeamBuildClient buildClient,
			String tag, IProgressMonitor monitor) throws TeamRepositoryException {
		if (tag!= null && !tag.isEmpty()) {
			String tagStrippedInvalidChars = tag.trim().replace(' ', '_');
			BuildParticipantLogger.info(getBuildLog(), "Tagging current build with tag: " + tagStrippedInvalidChars);
			IBuildResult buildResult = (IBuildResult) repo.itemManager().fetchCompleteItem(buildResultHandle, IItemManager.REFRESH, monitor);
			IBuildResult buildResultWorkingCopy = (IBuildResult) buildResult.getWorkingCopy();
			String existing_tags = buildResultWorkingCopy.getTags();
			String existing_tags_prefix = existing_tags != null && existing_tags.isEmpty() ? existing_tags + "," : "";
			buildResultWorkingCopy.setTags(existing_tags_prefix + tagStrippedInvalidChars);
			buildClient.save(buildResultWorkingCopy, monitor); 
		}
	}

	private IItemQueryPage getBuildHistory(ITeamBuildClient buildClient,
			String buildDefinitionId, String buildLabel,
			IProgressMonitor monitor) throws IllegalArgumentException,
			TeamRepositoryException {
		final IBuildResultQueryModel buildResultQueryModel = IBuildResultQueryModel.ROOT;
		final IItemQuery query = IItemQuery.FACTORY
				.newInstance(buildResultQueryModel);

		query.filter(query.and(
				buildResultQueryModel.label()._like(query.newStringArg()),
				buildResultQueryModel.buildDefinition()._eq(
						query.newItemHandleArg())));

		final IBuildDefinition buildDefinition = buildClient
				.getBuildDefinition(buildDefinitionId, monitor);

		final Object[] parameters = new Object[] { buildLabel, buildDefinition };

		return buildClient.queryItems(query, parameters,
				IQueryService.ITEM_QUERY_MAX_PAGE_SIZE, monitor);

	}
}
