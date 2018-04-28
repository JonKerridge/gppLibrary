package gppLibrary.mapReduce

import gppLibrary.Logger
import gppLibrary.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.CSTimer
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The Mapper process implements the Map part of the map-reduce architecture.  The process inputs an input class object, which
 * is transformed into one or more output class objects by means of the map fuction specified in the input class.
 * 
 * @param input The channel from which the input object to be processed is read
 * @param output The channel to which the mapped output object is written
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
class OneMapperOne implements CSProcess {
	
	ChannelInput input
	ChannelOutput output
	String outClassName
	String mapFunction = ""
	String createClass = ""
	
	String logPhaseName = ""
	String logPropertyName = ""
	
	void run(){
		if (logPhaseName == "") {
			Map localMap = [:]
			def o = input.read()
			Class outClass = Class.forName(outClassName)
			while ( ! (o instanceof UniversalTerminator)){
				localMap.putAll(o.&"$mapFunction"())
				localMap.each{ k, v ->
					def oc = outClass.newInstance()
					oc.&"$createClass"(k,v)
					output.write(oc)
				}
				localMap = [:]
				o = input.read()
			}
			output.write(o)
		}
		else { //logging
			def timer = new CSTimer()
			List logPhase = []
			logPhase << Logger.initLog(logPhaseName, timer.read())

			Map localMap = [:]
			def o = input.read()
			Class outClass = Class.forName(outClassName)
			while ( ! (o instanceof UniversalTerminator)){
				logPhase << Logger.inputEvent(o.getProperty(logPropertyName), timer.read())
				localMap.putAll(o.&"$mapFunction"())
				localMap.each{ k, v ->
					def oc = outClass.newInstance()
					oc.&"$createClass"(k,v)
					output.write(oc)
					logPhase << Logger.outputEvent(oc.getProperty(logPropertyName), timer.read())
				}
				localMap = [:]
				o = input.read()
			}
			logPhase << Logger.endEvent(timer.read())
			o.log << logPhase
			output.write(o)
		}
	}

}
