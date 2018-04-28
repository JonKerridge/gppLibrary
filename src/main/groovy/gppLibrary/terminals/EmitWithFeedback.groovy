package gppLibrary.terminals

import gppLibrary.DataClass
import gppLibrary.DataClassInterface
import gppLibrary.DataDetails
import gppLibrary.Logger
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * The EmitWithFeedback process is used to send data objects of type dataClassName to the rest of the
 * parallel structure; it writes output data objects to one output channel until
 * it receives a false boolean value on its feedback channel.  It then terminates and
 * writes a UniversalTerminator object to the output channel. <p>
 *
 * @param output The one2one channel to which data objects are written
 * @param feedback The channel used to read a boolean input; which when false causes
 * 					the process to terminate.  True values are ignored.
 * @param eDetails A {@link gppLibrary.DataDetails} object that specifies the data class to be emitted
 *
*/

class EmitWithFeedback extends DataClass implements CSProcess {

	ChannelOutput output
	ChannelInput feedback
	DataDetails eDetails

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        Class EmitClass = Class.forName(eDetails.dName)
        boolean running = true
        int returnCode = -1
        Object ecInit = EmitClass.newInstance()
        returnCode = callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 20)

        def guards = [feedback, new Skip()]
        def alt = new ALT(guards)
        while (running){
            switch (alt.priSelect()){
                case 0: // feedback input
                    running = feedback.read()
                    break
                case 1: // skip guard
                    Object ec = EmitClass.newInstance()
                    returnCode = callUserMethod(ec, eDetails.dCreateMethod, eDetails.dCreateData, 24)
//                    returnCode = ec.&"${eDetails.dCreateMethod}"( eDetails.dCreateData )
                    if ( returnCode == DataClassInterface.normalContinuation) {
                        output.write(ec)
                    }
                    else
                        running = false
                    break
            }
        }
        output.write(new UniversalTerminator())
    }

	void run(){
        if (logPhaseName == "") {
            runMethod()
        }
        else {
            //logging required
            def timer = new CSTimer()
            List logPhase = []
            logPhase << Logger.startLog(logPhaseName, timer.read())

    		Class EmitClass = Class.forName(eDetails.dName)
    		boolean running = true
    		int returnCode = -1
    		Object ecInit = EmitClass.newInstance()
            returnCode = callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 20)

    		def guards = [feedback, new Skip()]
    		def alt = new ALT(guards)

           logPhase << Logger.initLog(logPhaseName, timer.read())

    		while (running){
    			switch (alt.priSelect()){
    				case 0:	// feedback input
    					running = feedback.read()
    					break
    				case 1: // skip guard
    					Object ec = EmitClass.newInstance()
              returnCode = callUserMethod(ec, eDetails.dCreateMethod, eDetails.dCreateData, 24)
//    					returnCode = ec.&"${eDetails.dCreateMethod}"( eDetails.dCreateData )
    					if ( returnCode == DataClassInterface.normalContinuation) {
    						output.write(ec)
                            logPhase << Logger.outputEvent(logPhaseName, timer.read(), ec.getProperty(logPropertyName))
    					}
    					else
                            running = false
    					break
    			}
    		}
            logPhase << Logger.endEvent(logPhaseName, timer.read())
            def ut = new UniversalTerminator()
            ut.log << logPhase
            output.write(ut)
        }
	}

}
