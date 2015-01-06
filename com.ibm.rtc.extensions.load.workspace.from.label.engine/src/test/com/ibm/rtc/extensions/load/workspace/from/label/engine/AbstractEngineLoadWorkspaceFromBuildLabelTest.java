package com.ibm.rtc.extensions.load.workspace.from.label.engine;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Ignore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.ibm.rtc.extensions.load.workspace.from.label.engine.utils.BuildParticipantLogger;
import com.ibm.team.repository.common.LogFactory;

@PrepareForTest({LogFactory.class, BuildParticipantLogger.class})
@Ignore
public class AbstractEngineLoadWorkspaceFromBuildLabelTest extends AbstractBuildParticipantTest {
    
	public void setUp() throws Exception {
		super.setUp();
		mockStatic(BuildParticipantLogger.class);
		
	}
	
}
