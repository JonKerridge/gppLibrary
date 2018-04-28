package gppLibrary.functionals.groups

import gppLibrary.mapReduce.ReducerPrevious
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The ListReduceList process implements the reduce phase of the map-reduce architecture.  By means of a 
 * group of {@link gppLibrary.mapReduce.Reduce} processes.  Initially the process splits the inputlist
 * into a number of sublists, one per Reducer.  Each Reducer writes to one element of the outputList.<p>
 * 
 * In a map-reduce architecture the number of reducers must be a factor of the number of mappers
 *  
 * @param inputList The ChannelInputList from which input objects are read
 * @param outputList The ChannelOutputList to which each Reducer writes to one element
 * @param reducers An int specifying the number of Reducer processes
 * @param reduceFunction The name of the method in the input object that is the reduce function definition
 * @param outClassName The name of the class used for output objects
 * 
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.  
 * LogPropertyName must be specified if logPhaseName is specified 
 * 
 */
@CompileStatic
class ListReduceList implements CSProcess {
	
	ChannelInputList inputList
	ChannelOutputList outputList
	int reducers
	String reduceFunction
	String outClassName

	String logPhaseName = ""
	String logPropertyName = ""

	void run(){
		int inSize = inputList.size()
		assert ((inSize % reducers)  == 0): "ListReduceList: reducers ($reducers) must divide inputList size ($inSize) exactly"
		int inputsPerReducer = (int)(inSize / reducers)
		List inLists = []
		for ( r in 0 ..< reducers){
			def inList = new ChannelInputList()
			for ( ir in 0 ..< inputsPerReducer){
				inList.append((ChannelInput)inputList[ir + (r * inputsPerReducer)])
			}
			inLists << inList
		}
		List network = (0 ..< reducers).collect { e ->
			new ReducerPrevious(inputList: (ChannelInputList) inLists[e],
						output : (ChannelOutput)outputList[e],
						reduceFunction: reduceFunction,
						outClassName: outClassName,
						logPhaseName: logPhaseName == "" ?  "" : (String)"$e, "  + logPhaseName ,
						logPropertyName: logPropertyName)
		}
		new PAR (network).run()
		
	}

}
