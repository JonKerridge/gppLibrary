package gppLibrary.functionals.groups

import gppLibrary.ResultDetails
import gppLibrary.terminals.Collect
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput

/**
 *
 * A AnyGroupCollect is a Group with an internal Collect process rather than a Worker.
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
 * @param inputAny the any end of a one2any channel from which objects are read,
 * 					each Collect process reads from the input channel.
 * @param rDetails A {@link gppLibrary.ResultDetails} object defining the same result class used by each Collect process in the group
 * @param collectors The number of Collect processes that will be created when the Group is run
 *
 * @param logFileName is a string value specifying that the log output should be written to a file rather than the console.
 * The filename string should contain the full pathe name.  The suffix.log will be added to the file name.  Each log file in
 * the group will be identified by its index.
 *
 *
 *
 * @see gpp.terminals.Collect
 */

@CompileStatic
class AnyGroupCollect implements CSProcess{

	ChannelInput inputAny
	ResultDetails rDetails
	int collectors

	String logFileName = ""

	void run() {
		List network = (0 ..< collectors).collect { e ->
			new Collect ( input: inputAny,
						  rDetails: rDetails,
						  logFileName: logFileName == "" ? "" : logFileName + "$e")
		}
		new PAR (network).run()

	}

}