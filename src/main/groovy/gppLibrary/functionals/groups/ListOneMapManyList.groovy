package gppLibrary.functionals.groups

import gppLibrary.mapReduce.OneMapperMany
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The ListMapList process implements a group of Map processes asthe Map part of the map-reduce architecture.  The process inputs an input class object, which
 * is transformed into one or more output class objects by means of the map fuction specified in the input class.
 *
 * @param inputList The ChannelInputList from which the input objects to be processed are read
 * @param outputList The ChannelOutputList to which the mapped output objects are written
 * @param mappers an integer specifying the number of Map processes in the group
 * @param outClassname The name of the output class
 * @param mapFunction The name of the method in the input object that is the map function definition
 * @param createClass The name of the method in the output object that is used to set the properties of an object instance
 *
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 *
 *
 */

@CompileStatic
class ListOneMapManyList implements CSProcess {
	
	ChannelInputList inputList
	ChannelOutputList outputList
	int mappers
	int outsPerMap
	String outClassName
	String mapFunction = ""
	String indexingFunction = ""		
	String createClass = ""

	String logPhaseName = ""
	String logPropertyName = ""

	void run(){
		assert (inputList.size() == mappers)
		assert (outputList.size() == (mappers * outsPerMap))
		List network = (0 ..< mappers).collect { e ->
			ChannelOutputList outList = []
			for ( i in 0..< outsPerMap) outList.append((ChannelOutput)outputList[(e*outsPerMap) + i])
			new OneMapperMany(input: (ChannelInput)inputList[e],
					   outputList: outList,
					   outClassName: outClassName,
					   mapFunction: mapFunction,
					   indexingFunction: indexingFunction,
					   createClass: createClass,
					   logPhaseName: logPhaseName == "" ?  "" : (String)"$e, "  + logPhaseName ,
					   logPropertyName: logPropertyName)
			
		}
		new PAR (network).run()
		
	}

}
