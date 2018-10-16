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
 * The log data is output as part of a Collect process.
 * The logFileName property must be specified and the log data will be written to a file as text values,
 * simply as the content of each tagged log message, comma separated, each on a separate line.
 * Time values are output as the long representation of system millisecond time
 * The property logChan holds a channel that can be used to output log data directly to a
 * LoggingVisualiser process.
 */
@CompileStatic
class Logger implements Cloneable, Serializable {
	static int startTag = 0
	static int initTag = 1
	static int inputTag = 2
	static int outputTag = 3
	static int endTag = 4
	static int workStartTag = 5
	static int workEndTag = 6
    static ChannelOutput logChan = null

/**
 * initLogChannel is used to initialise the logging channel, if used
 * @param loggingChan the any2one logging channel output connecting processes to the LoggingVisualiser
 */
    static void initLogChannel (ChannelOutput loggingChan){
        logChan = loggingChan
    }

	static List startLog(String logID, long time) {
        List logData = [time, startTag, logID, " "]
        if (logChan != null) logChan.write(logData)
		return logData
	}

	static List initLog(String logID, long time) {
        List logData = [time, initTag, logID, " "]
        if (logChan != null) logChan.write(logData)
        return logData
	}

	static List inputEvent(String logID, long time, Object o) {
        List logData = [time, inputTag, logID, o]
        if (logChan != null) logChan.write(logData)
        return logData
	}

	static List outputEvent(String logID, long time, Object o) {
        List logData = [time, outputTag, logID, o]
        if (logChan != null) logChan.write(logData)
        return logData
	}

	static List endEvent(String logID, long time) {
        List logData = [time, endTag, logID, " "]
        if (logChan != null) logChan.write(logData)
        return logData
	}

	static List workStartEvent(String logID, long time) {
        List logData = [time, workStartTag, logID, " "]
        if (logChan != null) logChan.write(logData)
        return logData
	}

	static List workEndEvent(String logID, long time) {
        List logData = [time, workEndTag, logID, " "]
        if (logChan != null) logChan.write(logData)
        return logData
	}

	static void produceLog ( List log, String logFileName ){
		assert logFileName != "" : "LogFileName must be specified"
        def file = new File(logFileName + "log.csv")
        if (file.exists()) file.delete()
        file.withPrintWriter { writer ->
            log.each { phase ->
                phase.each{ List entry ->
                    writer.println "${entry[0]}, ${entry[1]}, ${entry[2]}, ${entry[3]}"
                }//phase
            } //log
		} // end file
	} // produceLog

}
