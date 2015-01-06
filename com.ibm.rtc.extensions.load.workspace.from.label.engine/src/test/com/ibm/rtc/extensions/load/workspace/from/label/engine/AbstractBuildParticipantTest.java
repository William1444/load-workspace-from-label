package com.ibm.rtc.extensions.load.workspace.from.label.engine;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ibm.team.build.client.ITeamBuildClient;
import com.ibm.team.build.common.model.BuildStatus;
import com.ibm.team.build.common.model.IBuildDefinitionInstance;
import com.ibm.team.build.common.model.IBuildProperty;
import com.ibm.team.build.common.model.IBuildRequest;
import com.ibm.team.build.common.model.IBuildResult;
import com.ibm.team.build.common.model.IBuildResultContribution;
import com.ibm.team.build.common.model.IBuildResultHandle;
import com.ibm.team.build.engine.AbstractPreBuildParticipant;
import com.ibm.team.build.internal.common.builddefinition.IJazzScmConfigurationElement;
import com.ibm.team.build.internal.common.model.BuildFactory;
import com.ibm.team.build.internal.common.model.BuildResult;
import com.ibm.team.build.internal.scm.BuildWorkspaceDescriptor;
import com.ibm.team.build.internal.scm.RepositoryManager;
import com.ibm.team.repository.client.IContributorManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.internal.ContributorManager;
import com.ibm.team.repository.client.internal.ItemManager;
import com.ibm.team.repository.common.IContributorHandle;
import com.ibm.team.repository.common.IItemHandle;
import com.ibm.team.repository.common.LogFactory;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.repository.common.query.IItemQueryPage;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.client.internal.WorkspaceManager;
import com.ibm.team.scm.common.IBaselineSetHandle;
import com.ibm.team.scm.common.IRepositoryProgressMonitor;
import com.ibm.team.scm.common.IScmService;
import com.ibm.team.scm.common.IWorkspace;
import com.ibm.team.scm.common.internal.BaselineSet;
import com.ibm.team.scm.common.internal.dto.WorkspaceRefreshResult;

@SuppressWarnings("restriction")
@RunWith(PowerMockRunner.class)
@PrepareForTest({TeamPlatform.class, LogFactory.class, SCMPlatform.class, BuildWorkspaceDescriptor.class})
@Ignore
public abstract class AbstractBuildParticipantTest {
    
	@Mock WorkspaceManager workspaceManagerMock;
    @Mock IScmService serviceMock;
    @Mock ItemManager itemManagerMock;
    @Mock IProgressMonitor monitorMock;
    @Mock IItemQueryPage queryPageMock;
    @Mock IBuildResult buildResultMock;
    @Mock Log logMock;
    @Mock ITeamBuildClient buildClientMock;
	@Mock ITeamRepository repoMock;
	@Mock RepositoryManager repoManagerMock;
	@Mock IContributorManager contributorManagerMock;	
	@Mock List<BuildResult> buildResultListMock;
	@Mock BuildResult successfulBuild;
	@Mock BuildResult unsuccessfulBuild;
	@Mock BaselineSet snapshotMock;
	@Mock IBuildResultContribution validSnapshot;
	@Mock IScmService workspaceManagerServiceMock;
	@Mock IBuildDefinitionInstance buildDefinitionInstanceMock;
	@Mock IBuildProperty buildPropertyMock;
	@Mock IBuildRequest buildRequestMock;
	@Mock WorkspaceRefreshResult workspaceRefreshResultMock;
    @Mock IWorkspace workspaceMock;
    @Mock IBuildResultContribution[] buildResultContributionsArrayMock;
    @Mock IBuildResultContribution buildResultContributionMock;
    @Mock IItemHandle snapshotItemHandleMock;
    @Mock BuildWorkspaceDescriptor buildWorkspaceDescriptorMock;
    @Mock IWorkspace refreshResultWorkspaceMock; 
    @Mock IWorkspaceConnection newWorkspaceConnectionMock;
    @Mock IBuildResultHandle buildResultHandleMock;
    @Mock IBuildResult buildResultWorkingCopyMock;
    
    static final String snapshotNameMock = "snapshot";
    static final String buildRequesterUserId = "builduser";
    static final String buildWorkspaceUUIDString = UUID.generate().getUuidValue();
    static final String refreshWorkspaceUUIDString = UUID.generate().getUuidValue();
    @Ignore
    public void setUp() throws Exception {
    	
    	//static mocks
    	mockStatic(TeamPlatform.class);
    	when(TeamPlatform.isStarted()).thenReturn(true);
    	mockStatic(LogFactory.class);
    	
    	when(LogFactory.getLog(Mockito.anyString())).thenReturn(logMock);
    	//mock test object 

    	//static mocks
    	workspaceManagerMock = Mockito.mock(WorkspaceManager.class);
    	repoMock = Mockito.mock(ITeamRepository.class);
    	repoManagerMock = Mockito.mock(RepositoryManager.class); 
    	mockStatic(SCMPlatform.class);
    	when(SCMPlatform.getWorkspaceManager(repoMock)).thenReturn(workspaceManagerMock);
    	
    	//mocks
    	buildClientMock = Mockito.mock(ITeamBuildClient.class);
    	queryPageMock = Mockito.mock(IItemQueryPage.class);
    	monitorMock = Mockito.mock(IProgressMonitor.class);
    	successfulBuild = Mockito.mock(BuildResult.class);
    	unsuccessfulBuild = Mockito.mock(BuildResult.class);
    	snapshotMock = Mockito.mock(BaselineSet.class);
    	workspaceManagerServiceMock = Mockito.mock(IScmService.class);
    	buildDefinitionInstanceMock = Mockito.mock(IBuildDefinitionInstance.class);
    	List<IBuildProperty> buildProperties = new ArrayList<IBuildProperty>();
    	IBuildProperty propertyWorkspaceUUID = BuildFactory.eINSTANCE.createBuildProperty();
    	propertyWorkspaceUUID.setName(IJazzScmConfigurationElement.PROPERTY_WORKSPACE_UUID);
    	propertyWorkspaceUUID.setValue(buildWorkspaceUUIDString);
    	buildProperties.add(propertyWorkspaceUUID);
    	
    	buildPropertyMock = Mockito.mock(IBuildProperty.class);
    	buildRequestMock = Mockito.mock(IBuildRequest.class);
    	buildResultMock = Mockito.mock(IBuildResult.class);
    	buildResultHandleMock = Mockito.mock(IBuildResultHandle.class);
    	buildResultWorkingCopyMock = Mockito.mock(IBuildResult.class);
    	itemManagerMock = Mockito.mock(ItemManager.class);
    	workspaceRefreshResultMock = Mockito.mock(WorkspaceRefreshResult.class);
    	workspaceRefreshResultMock.setWorkspace(refreshResultWorkspaceMock);
    	
    	workspaceMock = Mockito.mock(IWorkspace.class);
    	buildResultContributionMock = Mockito.mock(IBuildResultContribution.class);
    	snapshotItemHandleMock = Mockito.mock(IItemHandle.class);
    	buildWorkspaceDescriptorMock = Mockito.mock(BuildWorkspaceDescriptor.class);
    	contributorManagerMock = Mockito.mock(ContributorManager.class);
    	refreshResultWorkspaceMock = Mockito.mock(IWorkspace.class);
    	newWorkspaceConnectionMock = Mockito.mock(IWorkspaceConnection.class);
    	//inject mocks
    	when(repoMock.getClientLibrary(Mockito.any(Class.class))).thenReturn(buildClientMock);
    	when(repoMock.getUserId()).thenReturn("buildRequesterUserId");
    	when(repoMock.contributorManager()).thenReturn(contributorManagerMock);
    	when(successfulBuild.getStatus()).thenReturn(BuildStatus.OK);
    	when(unsuccessfulBuild.getStatus()).thenReturn(BuildStatus.ERROR);
    	when(repoMock.itemManager()).thenReturn(itemManagerMock);
    	when(snapshotMock.getName()).thenReturn(snapshotNameMock);
    	when(snapshotMock.getItemId()).thenReturn(UUID.generate());
    	when(workspaceManagerMock.getServerConfigurationService()).thenReturn(workspaceManagerServiceMock);
    	when(buildRequestMock.getBuildDefinitionInstance()).thenReturn(buildDefinitionInstanceMock);
    	when(buildRequestMock.getBuildResult()).thenReturn(buildResultHandleMock);
    	when(buildDefinitionInstanceMock.getProperty(IJazzScmConfigurationElement.PROPERTY_WORKSPACE_UUID))
    		.thenReturn(propertyWorkspaceUUID);
    	
    	when(workspaceMock.getItemId()).thenReturn(UUID.valueOf(buildWorkspaceUUIDString));
   		//mock getBuildResult
    	when(buildResultContributionMock.getExtendedContribution()).thenReturn(snapshotItemHandleMock);
   		Mockito.when(itemManagerMock.fetchCompleteItem(Mockito.any(IBaselineSetHandle.class), Mockito.anyInt(), 
   				Mockito.any(IProgressMonitor.class))).thenReturn(snapshotMock);
    	
   		Mockito.when(itemManagerMock.fetchCompleteItem(Mockito.same(buildResultHandleMock), Mockito.anyInt(), 
   				Mockito.any(IProgressMonitor.class))).thenReturn(buildResultMock);
   		when(buildResultMock.getWorkingCopy()).thenReturn(buildResultWorkingCopyMock);
    	//for getSnapshotMethod


   		when(workspaceManagerServiceMock.createWorkspaceFromBaselineSet(Mockito.any(IContributorHandle.class), Mockito.anyString(), 
   				Mockito.anyString(), Mockito.any(IBaselineSetHandle.class), Mockito.any(IRepositoryProgressMonitor.class)))
   					.thenReturn(workspaceRefreshResultMock);
   		when(workspaceRefreshResultMock.getWorkspace()).thenReturn(workspaceMock);
    	when(refreshResultWorkspaceMock.getItemId()).thenReturn(UUID.valueOf(refreshWorkspaceUUIDString));
    	
    }
    
    String getTeamRepositoryMethodName = "getTeamRepository";
    String getBuildRequestMethodName = "getBuildRequest";
    String getRepositoryManagerMethodName = "getRepositoryManager";
    @Ignore
    public <T extends AbstractPreBuildParticipant> T mockPreBuildPartipant(T instance) throws Exception {
		
		T preBuildParticipantMock = PowerMockito.spy(instance);
		
		PowerMockito.doReturn(repoMock).when(preBuildParticipantMock, getTeamRepositoryMethodName);
    	
    	PowerMockito.doReturn(buildRequestMock).when(preBuildParticipantMock, getBuildRequestMethodName);
    	PowerMockito.doReturn(repoManagerMock).when(preBuildParticipantMock, getRepositoryManagerMethodName);
		return preBuildParticipantMock;
    	
    }
	
}
