package com.ibm.rtc.extensions.load.workspace.from.label.engine.utils

import spock.lang.Specification
import java.util.Map

import com.ibm.rtc.extensions.load.workspace.from.label.engine.utils.Properties;
import com.ibm.team.build.common.model.IBuildRequest;
import com.ibm.team.build.common.model.IConfigurationProperty;

/**
 * Test class for DeltaDateRetriever
 * 
 * @author Avijit Gupta
 *
 */
class PropertiesSpecification extends Specification {

	IBuildRequest buildRequestMock = Mock()
	IConfigurationProperty configurationPropertyMock = Mock()
    
	
    def setup() {
		
    }
    
    def "return a build property value given a valid key"() {
    	given:
		buildRequestMock.getBuildDefinitionProperties() >> ["propKey":"propVal", "propKey2":"propVal2"]
		
    	
		when:
		def result = Properties.buildPropertyValue(buildRequestMock,"propKey")
		
		then:
		result == "propVal"
    }
    
    def "return a build property value given a valid key aswell"() {
    	given:
		buildRequestMock.getBuildDefinitionProperties() >> ["propKey":"propVal", "propKey2":"propVal2"]
		
    	when:
		def result = Properties.buildPropertyValue(buildRequestMock,"propKey2")
		
		then:
		result == "propVal2"
    }
	
    def "return null value given an invalid key"() {
    	given:
		buildRequestMock.getBuildDefinitionProperties() >> ["propKey":"propVal", "propKey2":"propVal2"]
    	
		when:
		def result = Properties.buildPropertyValue(buildRequestMock,"nonExistent")
		
		then:
		result == null
    }
    
	def "return an unreplaced string where there are no variable matches"() {
        given:
    	buildRequestMock.getBuildDefinitionProperties() >> ["propKey":"propVal", "propKey2":"propVal2"]
		
        when:
        def result = Properties.buildPropertyVariableReplacement(buildRequestMock, '${not.a.valid.build.prop}')
        
        then:
        result == '${not.a.valid.build.prop}'
	}
	
	def "return replaced string where there is a single variable matches"() {
        given:
    	buildRequestMock.getBuildDefinitionProperties() >> ["build.prop":"value", "another.build_prop":"another value"]
		
        when:
        def result = Properties.buildPropertyVariableReplacement(buildRequestMock, '${build.prop}')
        
        then:
        result == 'value'
	}
	
	def "return replaced string where there is a multiple variable matches"() {
		given:
		buildRequestMock.getBuildDefinitionProperties() >> ["build.prop":"value", "another.build_prop":"another value"]
		
		when:
		def result = Properties.buildPropertyVariableReplacement(buildRequestMock, '${build.prop}some stuff ${build.prop}')
		
		then:
		result == 'valuesome stuff value'
	}
	
	def "return replaced string with unmatched strings left unreplaced"() {
		given:
		buildRequestMock.getBuildDefinitionProperties() >> ["build.prop":"value", "another.build_prop":"another value"]
		
		when:
		def result = Properties.buildPropertyVariableReplacement(buildRequestMock, '${build.prop} the following ${variable.does.not.exit} so that is good')
		
		then:
		result == 'value the following ${variable.does.not.exit} so that is good'
	}
	
	def "return replaced string with more than 1 matched variable"() {
		given:
		buildRequestMock.getBuildDefinitionProperties() >> ["build.prop":"value", "another.build_prop":"another value"]
		
		when:
		def result = Properties.buildPropertyVariableReplacement(buildRequestMock, 'this value ${build.prop} and this ${another.build_prop} replaced. but not me another.build_prop')
		
		then:
		result == 'this value value and this another value replaced. but not me another.build_prop'
	}
		
}
