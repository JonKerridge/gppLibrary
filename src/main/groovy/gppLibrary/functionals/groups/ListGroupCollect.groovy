package gppLibrary.functionals.groups

import gppLibrary.ResultDetails
import gppLibrary.terminals.Collect
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 *
 * A ListGroupCollect is a Group with an internal Collect process rather than a Worker.
 * This has the effect of running all the Collect processes in parallel.  It is assumed
 * that any results are fully processed by the GroupCollect as there are no output channels
 * connected to the processes.
 * <p>
 * It is assumed that it is not possible to write to the same (or part of a) data object
 * in more than ONE of the Collectors.  In other words parallel access to a data object
 * is not permitted for write operations where the operation does not have exclusive
 * access to the data object.  This is a requirement but is not checked by the system.
 * There is no synchronisation between the Collectors in the group.
 * <p>
 * @param inputList A ChannelInputList with as many channels as the value of workers.
 * 					Each Collect process reads from just one element of the input.
 * @param rDetails A list of {@link gppLibrary.ResultDetails} object defining the result class used by each Collect process in the group
 * @param workers The number of Collect processes that will be created when the Group is run
 *
 * @param logFileName is a string value specifying that the log output should be written to a file rather than the console.
 * The filename string should contain the full pathe name.  The suffix.log will be added to the file name.  Each log file in
 * the group will be identified by its index.
 *
 * @param visLogChan the output end of an any2one channel to which log data will be sent to an instance of the LoggingVisualiser
 * process running in parallel with the application network.  If not specified then it is assumed that no visualiser process is running.
 *
 *
 *
 * @see gppLibrary.terminals.Collect
 */

@CompileStatic
class ListGroupCollect implements CSProcess{

	ChannelInputList inputList
	List <ResultDetails> rDetails
	int workers

	String logFileName = ""
	ChannelOutput visLogChan = null

	void run() {
        int inSize = inputList.size()
        int rSize = rDetails.size()
        assert inSize == workers : "ListGroupCollect: inputList size, $inSize, does not equal the number of workers $workers"
        assert rSize == workers : "ListGroupCollect: rDetails size, $rSize, does not equal the number of workers $workers"
		List network = (0 ..< workers).collect { e ->
			new Collect ( input: (ChannelInput)inputList[e],
						  rDetails: rDetails[e],
						  logFileName: logFileName == "" ? "" : logFileName + "$e",
			 			  visLogChan: visLogChan)
		}
		new PAR (network).run()

	}

}
