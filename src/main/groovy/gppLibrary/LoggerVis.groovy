package gppLibrary

import groovy.transform.CompileStatic
import jcsp.lang.ChannelOutput

/**
 * The Logger class provides a number of static methods that are used internally,
 * within other processes, that cause the recording of timing
 * data pertaining to the input and output communications associated with that process.
 *
 * Processes can be optionally logged simply by specifying a string property logPhaseName and the
 * associated name of a property within the object being processed that uniquely identifies the object instance.
 *
 * The log data is output as part of a Collect process.  It can either be printed direct to the console,
 * showing elapsed timings.
 * If a  logFileName property is specified then the log data will be written to a file as text values,
 * simply as the content of each tagged log message, comma separated, each on a separate line.
 * Time values are output as the long representation of system millisecond time
 */
@CompileStatic
class LoggerVis implements Cloneable, Serializable {
	static int startTag = 0
	static int initTag = 1
	static int inputTag = 2
	static int outputTag = 3
	static int endTag = 4
	static int workStartTag = 5
	static int workEndTag = 6
    static ChannelOutput logChan = null

    static void initLogChannel (ChannelOutput loggingChan){
        logChan = loggingChan
    }

	static List startLog(String logID, long time) {
        if (logChan != null) logChan.write([time, startTag, logID, " "])
		return [time, startTag, logID, " "]
	}

	static List initLog(String logID, long time) {
        if (logChan != null) logChan.write([time, startTag, logID, " "])
        return [time, initTag, logID, " "]
	}

	static List inputEvent(String logID, long time, Object o) {
        if (logChan != null) logChan.write([time, startTag, logID, o])
		return [time, inputTag, logID, o]
	}

	static List outputEvent(String logID, long time, Object o) {
        if (logChan != null) logChan.write([time, startTag, logID, o])
        return [time, outputTag, logID, o]
	}

	static List endEvent(String logID, long time) {
        if (logChan != null) logChan.write([time, startTag, logID, " "])
        return [time, endTag, logID, " "]
	}

	static List workStartEvent(String logID, long time) {
        if (logChan != null) logChan.write([time, startTag, logID, " "])
        return [time, workStartTag, logID, " "]
	}

	static List workEndEvent(String logID, long time) {
        if (logChan != null) logChan.write([time, startTag, logID, " "])
        return [time, workEndTag, logID, " "]
	}

	static void produceLog ( List log, String logFileName ){
		if (logFileName == ""){
			log.each { phase ->
				println " "
				phase.each{ List entry ->
					switch (entry[0]){
							case 0: // start
								print "START "
								break
							case 1: // init
								print "\tINIT "
								break
							case 2:  // input
								print "\tIN "
								break
							case 3: // output
								print "\tOUT "
								break
							case 4: //end
								print "END "
								break
							case 5 : //workStart
								print "\t\t\tWORK START "
								break
							case 6 : //workEnd
								print "\t\t\tWORK END "
								break
					}
					println "${entry[1]}, ${entry[2]}, ${entry[3]}"
				}
			}
		}
		else {
			//TODO convert to new format
			
			// the log should be written to a file
			def file = new File(logFileName + "log.txt")
			if (file.exists()) file.delete()
			file.withPrintWriter { writer ->
				log.each { phase ->
					phase.each{ List entry ->
						writer.println "${entry[0]}, ${entry[1]}, ${entry[2]}, ${entry[3]}"
					}//phase
				} //log
			} // with printWriter
		} // end if
	} // produceLog

}
