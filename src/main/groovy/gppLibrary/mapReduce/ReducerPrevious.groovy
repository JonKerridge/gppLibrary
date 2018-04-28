package gppLibrary.mapReduce

import gppLibrary.Logger
import gppLibrary.UniversalTerminator
import groovyJCSP.ChannelInputList
import jcsp.lang.CSProcess
import jcsp.lang.CSTimer
import jcsp.lang.ChannelOutput

/**
 * The Reducer process implements the reduce phase of the map-reduce architecture.  The process reads input objects
 * from the channel inputList.  It is assumed that each elelemnt of the inputList will send objects in the same sorted order.
 * The Reducer process undertakes a n-way merge operation on the inputs such that it can be guaranteed that all equal inputs
 * will be available to the process at the same time.  The reduceFunction then undertakes an operation on all the equal
 * input objects in order to output a single object summarising the inputs.  The output object does not have to be of
 * the same class definition as the input object.
 * 
 * @param inputList The ChannelInputList from which input objects are read
 * @param output The channel to which the processed object is written
 * @param reduceFunction The name of the method in the input object that is the reduce function definition
 * @param outClassName The name of the class used for output objects
 * 
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.  
 * LogPropertyName must be specified if logPhaseName is specified 
 * 
 * 
 *
 */
class ReducerPrevious implements CSProcess {
	
	ChannelInputList inputList
	ChannelOutput output
	String reduceFunction
	String outClassName
	
	String logPhaseName = ""
	String logPropertyName = ""
	
	void run(){
		int sources = inputList.size()
		int terminated = 0
		def buffers = []
		for ( i in 0 ..< sources) buffers[i] = null
		Object o = null
		boolean running = true
		def ic = null
		// read in the initial values from each inputList element
		boolean icKnown = false		
		
		if (logPhaseName == "") { // not logging		
			for ( i in 0 ..< sources){
				o =  inputList[i].read()
				if ( ! (o instanceof UniversalTerminator)){
					buffers[i] = o
					if (! icKnown) {
						ic = o				// should only do this once!!
						icKnown = true
					}
				}
				else {
					terminated = terminated + 1
				}
			}
			running = terminated == sources ? false : true
			// now start the function part
			Class outClass = Class.forName(outClassName)
			def returnValues = []	//[error, [subscript of buffers used], outClass object]
			while (running){
				returnValues = ic.&"$reduceFunction"(buffers, outClass)
				if (returnValues[0] == DataClassInterface.normalContinuation){
					output.write(returnValues[2])
					returnValues[1].each{ index ->
						o =  inputList[index].read()
						if ( ! (o instanceof UniversalTerminator))
							buffers[index] = o
						else {
							buffers[index]= null
							terminated = terminated + 1
							running = terminated == sources ? false : true
						}					
					}
				}
				else 
					gpp.DataClass.unexpectedReturnCode("Reducer: error during $reduceFunction", returnValues[0])
			}
			output.write(o)  // write the last instance of a UT
		}
		else { // logging
			def timer = new CSTimer()
			List logPhase = []
			logPhase << Logger.initLog(logPhaseName, timer.read())

			for ( i in 0 ..< sources){
				o =  inputList[i].read()
				if ( ! (o instanceof UniversalTerminator)){
					logPhase << Logger.inputEvent(o.getProperty(logPropertyName), timer.read())
					buffers[i] = o
					if (! icKnown) {
						ic = o				// should only do this once!!
						icKnown = true
					}
				}
				else {
					terminated = terminated + 1
				}
			}
			running = terminated == sources ? false : true
			// now start the function part
			Class outClass = Class.forName(outClassName)
			def returnValues = []	//[error, [subscript of buffers used], outClass object]
			while (running){
				returnValues = ic.&"$reduceFunction"(buffers, outClass)
				if (returnValues[0] == DataClassInterface.normalContinuation){
					output.write(returnValues[2])
					logPhase << Logger.outputEvent(returnValues[2].getProperty(logPropertyName), timer.read())
					returnValues[1].each{ index ->
						o =  inputList[index].read()
						if ( ! (o instanceof UniversalTerminator)){
							logPhase << Logger.inputEvent(o.getProperty(logPropertyName), timer.read())
							buffers[index] = o
						}
						else {
							buffers[index]= null
							terminated = terminated + 1
							running = terminated == sources ? false : true
						}					
					}
				}
				else 
					gpp.DataClass.unexpectedReturnCode("Reducer: error during $reduceFunction", returnValues[0])
			}
			logPhase << Logger.endEvent(timer.read())
			o.log << logPhase
			output.write(o)  // write the last instance of a UT
		}
	}

}
