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
 * @param logChan the any2one logging channel output connecting processes to the LoggingVisualiser
 */
    static void initLogChannel(ChannelOutput loggingChan) {
        logChan = loggingChan
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void startLog(String logID, long time) {
        logChan.write([time, startTag, logID, " "])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void initLog(String logID, long time) {
        logChan.write([time, initTag, logID, " "])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 * @param o the property value being tracked
 */
    static void inputEvent(String logID, long time, Object o) {
        logChan.write([time, inputTag, logID, o])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 * @param o the property value being tracked
 */
    static void outputEvent(String logID, long time, Object o) {
        logChan.write([time, outputTag, logID, o])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void endEvent(String logID, long time) {
        logChan.write([time, endTag, logID, " "])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void workStartEvent(String logID, long time) {
        logChan.write([time, workStartTag, logID, " "])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void workEndEvent(String logID, long time) {
        logChan.write([time, workEndTag, logID, " "])
    }


}