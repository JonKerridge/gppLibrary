package gppLibrary.mapReduce

import gppLibrary.Logger
import gppLibrary.UniversalTerminator
import groovyJCSP.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.CSTimer
import jcsp.lang.ChannelInput

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
 *
 */
class OneMapperMany implements CSProcess {

	ChannelInput input
	ChannelOutputList outputList
	String outClassName
	String mapFunction = ""		// a method in the input class
	String indexingFunction = ""		// a method of the output class
	String createClass = ""		// a method of the output class

	String logPhaseName = ""
	String logPropertyName = ""

	void run(){
		if (logPhaseName == "") {
			Map localMap = [:]
			def o = input.read()
			Class outClass = Class.forName(outClassName)
			int index = -1
			while ( ! (o instanceof UniversalTerminator)){
				localMap.putAll(o.&"$mapFunction"())
				localMap.each{ k, v ->
					def oc = outClass.newInstance()
					index = oc.&"$indexingFunction"(k)
					oc.&"$createClass"(k,v)
					outputList[index].write(oc)
				}
				localMap = [:]
				o = input.read()
			}
			for ( i in 0..< outputList.size() ) outputList[i].write(o)
		}
		else { //logging
			def timer = new CSTimer()
			List logPhase = []
			logPhase << Logger.initLog(logPhaseName, timer.read())

			Map localMap = [:]
			def o = input.read()
			Class outClass = Class.forName(outClassName)
			int index = -1
			while ( ! (o instanceof UniversalTerminator)){
				logPhase << Logger.inputEvent(o.getProperty(logPropertyName), timer.read())
				localMap.putAll(o.&"$mapFunction"())
				localMap.each{ k, v ->
					def oc = outClass.newInstance()
					index = oc.&"$indexingFunction"(k)
					oc.&"$createClass"(k,v)
					outputList[index].write(oc)
					logPhase << Logger.outputEvent(oc.getProperty(logPropertyName), timer.read())
				}
				localMap = [:]
				o = input.read()
			}
			logPhase << Logger.endEvent(timer.read())
			o.log << logPhase
			for ( i in 0..< outputList.size() ) outputList[i].write(o)
		}
	}

}
