package com.ibm.rtc.extensions.load.workspace.from.label.engine;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.PrintWriter;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.ibm.rtc.extensions.load.workspace.from.label.common.ILoadWorkspaceFromLabelConfigurationElement;
import com.ibm.rtc.extensions.load.workspace.from.label.engine.utils.BuildParticipantLogger;
import com.ibm.rtc.extensions.load.workspace.from.label.engine.utils.Properties;
import com.ibm.team.build.client.ITeamBuildClient;
import com.ibm.team.build.common.ScmConstants;
import com.ibm.team.build.common.TeamBuildException;
import com.ibm.team.build.common.model.BuildStatus;
import com.ibm.team.build.common.model.IBuildConfigurationElement;
import com.ibm.team.build.common.model.IBuildProperty;
import com.ibm.team.build.common.model.IBuildResult;
import com.ibm.team.build.common.model.IBuildResultContribution;
import com.ibm.team.build.common.model.IConfigurationProperty;
import com.ibm.team.build.internal.common.model.BuildFactory;
import com.ibm.team.build.internal.scm.RepositoryManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.LogFactory;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.internal.BaselineSet;

@SuppressWarnings("restriction")
@PrepareForTest({TeamPlatform.class, SCMPlatform.class, LogFactory.class, PreBuildParticipantLoadWorkspaceFromBuildLabel.class, BuildParticipantLogger.class, Properties.class})
public class PreBuildParticipantLoadWorkspaceFromBuildLabelTest extends AbstractEngineLoadWorkspaceFromBuildLabelTest {
    
	@Mock 
	IBuildConfigurationElement elementMock;
	IConfigurationProperty configurationPropertyBuildDefinitionMock;
	IConfigurationProperty configurationPropertyBuildLabelMock;
	
	
    //private methods to be mocked
	String getTeamRepositoryMethodName = "getTeamRepository";
	String getBuildHistoryMethodName = "getBuildHistory";
	String getSnapshotMethodName = "getSnapshot";
	String getBuildRequestMethodName = "getBuildRequest";	
	
	private PreBuildParticipantLoadWorkspaceFromBuildLabel testObj;
	
	private static final String validBuildLabel = "validLabel";
	private static final String validBuildDefinitionId = "validId";
	
	
    @Before
    public void setUp() throws Exception {
    	super.setUp();
		mockStatic(BuildParticipantLogger.class);
		mockStatic(Properties.class);
		
    	super.setUp();

		elementMock = Mockito.mock(IBuildConfigurationElement.class);
		configurationPropertyBuildDefinitionMock = Mockito.mock(IConfigurationProperty.class);
		configurationPropertyBuildLabelMock = Mockito.mock(IConfigurationProperty.class);
    	
		when(elementMock.getConfigurationProperty(ILoadWorkspaceFromLabelConfigurationElement.PROPERTY_BUILD_DEFINITION_ID)).thenReturn(configurationPropertyBuildDefinitionMock);
		when(elementMock.getConfigurationProperty(ILoadWorkspaceFromLabelConfigurationElement.PROPERTY_BUILD_LABEL)).thenReturn(configurationPropertyBuildLabelMock);
		when(configurationPropertyBuildDefinitionMock.getValue()).thenReturn("buildDefinitionId");
		when(configurationPropertyBuildLabelMock.getValue()).thenReturn("label");
		
		when(buildDefinitionInstanceMock.getConfigurationElement(ILoadWorkspaceFromLabelConfigurationElement.ELEMENT_ID)).thenReturn(elementMock);
    	
		when(Properties.buildPropertyVariableReplacement(buildRequestMock, "buildDefinitionId")).thenReturn(validBuildDefinitionId);
		when(Properties.buildPropertyVariableReplacement(buildRequestMock, "label")).thenReturn(validBuildLabel);
		
    	// private method mocks (to instantiate itemManagerMock and to mock private methods from extending a preBuildParticipant)
    	testObj = mockPreBuildPartipant(new PreBuildParticipantLoadWorkspaceFromBuildLabel());
    	
    	// method specific to PreBuildParticipantLoadWorkspaceFromBuildLabel
    	PowerMockito.doReturn(queryPageMock).when(testObj, getBuildHistoryMethodName, Mockito.any(ITeamBuildClient.class),
    			Mockito.any(String.class), Mockito.any(String.class), Mockito.any(IProgressMonitor.class));
    	
    	testObj.setBuildDefinitionIdProperty(validBuildDefinitionId);
    	testObj.setBuildLabelProperty(validBuildLabel);
    	
    }
   	
   	@Test
    public void shouldReturnBuildStatusOKWhereOneBuildIsReturnedWithASnapshot() throws Exception {
   	// setup - 
   		// mock returned snapshot
   		PowerMockito.doReturn(snapshotMock).when(testObj, getSnapshotMethodName, Mockito.any(ITeamBuildClient.class),
    			Mockito.any(IBuildResult.class), Mockito.any(IProgressMonitor.class), Mockito.any(ITeamRepository.class));
   		// inject response of returned build results
   		when(queryPageMock.getSize()).thenReturn(1);
   		// inject build lookup response
   		buildResultListMock = Arrays.asList(successfulBuild);
   		when(itemManagerMock.fetchCompleteItems(Mockito.anyList(), Mockito.anyInt(), Mockito.any(IProgressMonitor.class)))
			.thenReturn(buildResultListMock);
   		PowerMockito.doNothing().when(testObj).transferWorkspaceOwnerAndVisibility(Mockito.anyString(), Mockito.anyString(), Mockito.any(ITeamRepository.class), 
   				Mockito.any(RepositoryManager.class), Mockito.any(IProgressMonitor.class));

	    // expect
   		BuildStatus expectedBuildStatus = BuildStatus.OK;
   		// act
   		BuildStatus actualBuildStatus = testObj.preBuild(monitorMock);
   		// assert
   		
	    assertEquals(expectedBuildStatus, actualBuildStatus);

	    Mockito.verify(buildResultWorkingCopyMock).setTags(snapshotNameMock + "_workspace");
	    PowerMockito.verifyStatic();
    }
   	
	@Test
    public void shouldTagBuildCorrectlyWithBuildEnvAsSuffixWhereBuildStatusIsOK() throws Exception {
   	// setup - 
   		// mock returned snapshot
   		PowerMockito.doReturn(snapshotMock).when(testObj, getSnapshotMethodName, Mockito.any(ITeamBuildClient.class),
    			Mockito.any(IBuildResult.class), Mockito.any(IProgressMonitor.class), Mockito.any(ITeamRepository.class));
   		// inject response of returned build results
   		when(queryPageMock.getSize()).thenReturn(1);
   		// inject build lookup response
   		buildResultListMock = Arrays.asList(successfulBuild);
   		when(itemManagerMock.fetchCompleteItems(Mockito.anyList(), Mockito.anyInt(), Mockito.any(IProgressMonitor.class)))
			.thenReturn(buildResultListMock);
   		PowerMockito.doNothing().when(testObj).transferWorkspaceOwnerAndVisibility(Mockito.anyString(), Mockito.anyString(), Mockito.any(ITeamRepository.class), 
   				Mockito.any(RepositoryManager.class), Mockito.any(IProgressMonitor.class));
   		String buildEnv = "environment";
   		IBuildProperty propertyBuildEnv = BuildFactory.eINSTANCE.createBuildProperty();
    	propertyBuildEnv.setName(Properties.BUILD_ENV_PROPERTY);
    	propertyBuildEnv.setValue("environment");
    	when(buildDefinitionInstanceMock.getProperty(Properties.BUILD_ENV_PROPERTY))
			.thenReturn(propertyBuildEnv);
	    // expect
   		BuildStatus expectedBuildStatus = BuildStatus.OK;
   		// act
   		BuildStatus actualBuildStatus = testObj.preBuild(monitorMock);
   		// assert
   		
	    assertEquals(expectedBuildStatus, actualBuildStatus);

	    Mockito.verify(buildResultWorkingCopyMock).setTags(snapshotNameMock + "_workspace-" + buildEnv);
	    PowerMockito.verifyStatic();
    }
   	
   	@Test
    public void shouldReturnBuildStatusERRORWhereOneBuildIsReturnedWithAssociatedBuildStatusOfFailure() throws Exception {
	// setup - 
   		// mock returned snapshot
   		PowerMockito.doReturn(snapshotMock).when(testObj, getSnapshotMethodName, Mockito.any(ITeamBuildClient.class),
    			Mockito.any(IBuildResult.class), Mockito.any(IProgressMonitor.class), Mockito.any(ITeamRepository.class));
   		// inject response of returned build results
   		when(queryPageMock.getSize()).thenReturn(1);
   		// inject build lookup response
   		buildResultListMock = Arrays.asList(unsuccessfulBuild);
   		when(itemManagerMock.fetchCompleteItems(Mockito.anyList(), Mockito.anyInt(), Mockito.any(IProgressMonitor.class)))
			.thenReturn(buildResultListMock);

	    // expect
   		BuildStatus expectedBuildStatus = BuildStatus.ERROR;
   		String buildStatusERROR = "Build with id: " + validBuildDefinitionId + " and label: " + validBuildLabel + 
   				", is not a successful build and should not be promoted!";
   		// act
   		BuildStatus actualBuildStatus = testObj.preBuild(monitorMock);
   		// assert
   		
	    assertEquals(expectedBuildStatus, actualBuildStatus);
	    PowerMockito.verifyStatic();
	    BuildParticipantLogger.error(Mockito.any(PrintWriter.class), Mockito.eq(buildStatusERROR));
    }
   	
   	@Test
    public void shouldReturnBuildStatusERRORWhereMoreThanOneBuildIsReturned() throws Exception {
    	// setup - 
   		// mock returned snapshot
   		PowerMockito.doReturn(snapshotMock).when(testObj, getSnapshotMethodName, Mockito.any(ITeamBuildClient.class),
    			Mockito.any(IBuildResult.class), Mockito.any(IProgressMonitor.class), Mockito.any(ITeamRepository.class));
   		// inject response of returned build results
   		when(queryPageMock.getSize()).thenReturn(2);
   		// inject build lookup response
   		buildResultListMock = Arrays.asList(successfulBuild,successfulBuild);
   		when(itemManagerMock.fetchCompleteItems(Mockito.anyList(), Mockito.anyInt(), Mockito.any(IProgressMonitor.class)))
   			.thenReturn(buildResultListMock);
	    // expect
   		BuildStatus expectedBuildStatus = BuildStatus.ERROR;
   		String moreThanOneBuildReturnedERROR = "Too many matching builds with id: " + validBuildDefinitionId + " and label: " 
   		+ validBuildLabel;
   		// act
   		BuildStatus actualBuildStatus = testObj.preBuild(monitorMock);
   		// assert
   		
	    assertEquals(expectedBuildStatus, actualBuildStatus);
	    PowerMockito.verifyStatic();
	    BuildParticipantLogger.error(Mockito.any(PrintWriter.class), Mockito.eq(moreThanOneBuildReturnedERROR));
    }
   	
   	@Test
    public void shouldReturnBuildStatusERRORWhereNoBuildIsReturned() throws Exception {
    	// setup - 
   		// mock returned snapshot
   		PowerMockito.doReturn(snapshotMock).when(testObj, getSnapshotMethodName, Mockito.any(ITeamBuildClient.class),
    			Mockito.any(IBuildResult.class), Mockito.any(IProgressMonitor.class), Mockito.any(ITeamRepository.class));
   		// inject response of returned build results
   		when(queryPageMock.getSize()).thenReturn(0);
   		// inject build lookup response
   		buildResultListMock = Arrays.asList(successfulBuild);
   		when(itemManagerMock.fetchCompleteItems(Mockito.anyList(), Mockito.anyInt(), Mockito.any(IProgressMonitor.class)))
			.thenReturn(buildResultListMock);
	    // expect
   		BuildStatus expectedBuildStatus = BuildStatus.ERROR;
   		String noMatchingBuildERROR = "No matching builds with id: " + validBuildDefinitionId + " and label: " + validBuildLabel;
   		// act
   		BuildStatus actualBuildStatus = testObj.preBuild(monitorMock);
   		// assert
   		
	    assertEquals(expectedBuildStatus, actualBuildStatus);
	    PowerMockito.verifyStatic();
	    BuildParticipantLogger.error(Mockito.any(PrintWriter.class), Mockito.eq(noMatchingBuildERROR));
    }
   	
   	@Test
    public void shouldLogSuccessfullyFoundSnapshotWhereASnapshotIsAssociatedToTheBuildResult() throws Exception {
   		// setup -
   		// inject snapshot lookup for a build response
   		buildResultContributionsArrayMock = new IBuildResultContribution[]{buildResultContributionMock};
   		when(buildClientMock.getBuildResultContributions(Mockito.any(IBuildResult.class), Mockito.eq(ScmConstants.EXTENDED_DATA_TYPE_ID_BUILD_SNAPSHOT), Mockito.any(IProgressMonitor.class)))
   			.thenReturn(buildResultContributionsArrayMock);
   		// expect
   		String getSnapshotSuccessLogMessage = "valid snapshot: " + snapshotNameMock;
   		// act
   		BaselineSet snapshot = testObj.getSnapshot(buildClientMock, successfulBuild, monitorMock, repoMock);
   		// assert
	    PowerMockito.verifyStatic();
	    BuildParticipantLogger.info(Mockito.any(PrintWriter.class), Mockito.eq(getSnapshotSuccessLogMessage));
	    assertEquals(snapshot,snapshotMock);
    }
   	
   	public void shouldLogSnapshotNotFoundERRORWhereNoSnapshotIsAssociatedToTheBuildResult() throws Exception {
   		// setup -
   		// inject snapshot lookup for a build response
   		buildResultContributionsArrayMock = new IBuildResultContribution[]{};
   		when(buildClientMock.getBuildResultContributions(Mockito.any(IBuildResult.class), Mockito.eq(ScmConstants.EXTENDED_DATA_TYPE_ID_BUILD_SNAPSHOT), Mockito.any(IProgressMonitor.class)))
   			.thenReturn(buildResultContributionsArrayMock);
   		// expect
   		String snapshotNotFoundERROR = "no snapshots found! Expected 1.";
   		// act
   		try {
   			testObj.getSnapshot(buildClientMock, successfulBuild, monitorMock, repoMock);
   		} catch (Exception e) {
   			assertThat(e, is(TeamBuildException.class));
   		}
   		// assert
	    PowerMockito.verifyStatic();
	    BuildParticipantLogger.error(Mockito.any(PrintWriter.class), Mockito.eq(snapshotNotFoundERROR));
    }
   	@Test
   	public void shouldLogTooManySnapshotsERRORWhereTooManySnapshotAreAssociatedToTheBuildResult() throws Exception {
   		// setup -
   		// inject snapshot lookup for a build response
   		buildResultContributionsArrayMock = new IBuildResultContribution[]{buildResultContributionMock,buildResultContributionMock};
   		when(buildClientMock.getBuildResultContributions(Mockito.any(IBuildResult.class), Mockito.eq(ScmConstants.EXTENDED_DATA_TYPE_ID_BUILD_SNAPSHOT), Mockito.any(IProgressMonitor.class)))
   			.thenReturn(buildResultContributionsArrayMock);
   		// expect
   		String tooManySnapshotsFoundError = "too many snapshots found! Expected 1.";
   		// act
   		try {
   			testObj.getSnapshot(buildClientMock, successfulBuild, monitorMock, repoMock);
   		} catch (Exception e) {
   			assertThat(e, is(TeamBuildException.class));
   		}
   		// assert
	    PowerMockito.verifyStatic();
	    BuildParticipantLogger.error(Mockito.any(PrintWriter.class), Mockito.eq(tooManySnapshotsFoundError));
    }
}