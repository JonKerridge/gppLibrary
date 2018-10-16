package gppLibrary.terminals

import gppLibrary.*
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * The Collect process collects results from the rest of the process network.
 * It reads an inputObject from the input channel which it then processes using
 * methods defined in the results class.<p>
 * <pre>
 * <b>Methods required by class resultClassName:</b>
 *     initClass( initialData )
 *     collector( inputObject )
 *     finalise( finaliseData )
 *
 * <b>Behaviour:</b>
 *     resultsClass.initClass(initData)
 *     o = input.read()
 *     while ( o != UniversalTerminator )
 *         resultClass.collector(o)
 *         o = input.read()
 *     resultsClass.finalise(finaliseData)
 * </pre>
 * 	<p>
 * @param input The one2one input channel used to receive results
 * @param rDetails A ResultDetails object containing data pertaining to result class used by the Collect process, it MUST be specified.
 * @param logFileName is a string value specifying the file name to which the log output should be written.
 * The filename string should contain the full path name.  The suffix log.txt will be added to the file name.
 * @param visLogChan the output end of an any2one channel to which log data will be sent to an instance of the LoggingVisualiser
 * process running in parallel with the application network.  If not specified then it is assumed that no visualiser process is running.
 *
 *
 *
*/

class Collect extends DataClass implements CSProcess {

	ChannelInput input
	ResultDetails rDetails
	int collected = 0

	String logFileName = ""
    ChannelOutput visLogChan = null
    Object inputObject = null


    @CompileStatic
    void runMethod() {
        Class resultsClass = Class.forName(rDetails.rName)
        def rc = resultsClass.newInstance()
        inputObject = input.read()
        int returnCode //= -1
        returnCode = callUserMethod(rc, rDetails.rInitMethod, rDetails.rInitData, 5 )
//      returnCode = rc.&"${rDetails.rInitMethod}"( rDetails.rInitData )
        while (!(inputObject instanceof UniversalTerminator)){
            collected += 1
            returnCode = callUserMethod(rc, rDetails.rCollectMethod, inputObject, 6)
//          returnCode = rc.&"${rDetails.rCollectMethod}"( inputObject )
//          if (returnCode == Constants.completedOK )
            inputObject = input.read()
//          else
//              gpp.DataClass.unexpectedReturnCode("Collect: error while collecting", returnCode)
        }
        returnCode = callUserMethod(rc, rDetails.rFinaliseMethod, rDetails.rFinaliseData, 7)
//      returnCode = rc.&"${rDetails.rFinaliseMethod}"( rDetails.rFinaliseData )
//      if (returnCode != Constants.completedOK)
//          gpp.DataClass.unexpectedReturnCode("Collect: error while finalising", returnCode)


	}

   	void run(){
		// now process any log that may be present
        runMethod()
		if ( inputObject.log != []) Logger.produceLog(inputObject.log, logFileName)
        // added to deal with visualiser process
        if (visLogChan != null) visLogChan.write(new UniversalTerminator())
	}

}
