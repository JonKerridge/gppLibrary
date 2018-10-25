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
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
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
        boolean altRead = false
        while (running) {
            switch (alt.priSelect()) {
                case 0: // feedback input
                    running = feedback.read()
                    altRead = true
//                    println "\t\tEWF: stopping in feedback part"
                    break
                case 1: // skip guard
                    Object ec = EmitClass.newInstance()
                    returnCode = callUserMethod(ec, eDetails.dCreateMethod, eDetails.dCreateData, 24)
//                    returnCode = ec.&"${eDetails.dCreateMethod}"( eDetails.dCreateData )
                    if (returnCode == normalContinuation) {
//                        println "\t\tEWF: continuing $ec"
                        output.write(ec)
                    } else {
                        running = false
//                        println "\t\tEWF: stopping in data gen part"
                    }
                    break
            }
        }
//        println "\t\tEWF: sending UT $altRead"
        output.write(new UniversalTerminator())
//        println "\t\tEWF: sent UT"
        if (!altRead){
            feedback.read()
//            println "\t\tEWF: read the feedback channel after UT sent"
        }
    }

	void run(){
        if (logPhaseName == "") {
            runMethod()
        }
        else {
            //logging required
            def timer = new CSTimer()
            Logger.startLog(logPhaseName, timer.read())
            Class EmitClass = Class.forName(eDetails.dName)
            boolean running = true
            int returnCode = -1
            Object ecInit = EmitClass.newInstance()
            returnCode = callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 20)
            Logger.initLog(logPhaseName, timer.read())
            def guards = [feedback, new Skip()]
            def alt = new ALT(guards)
            boolean altRead = false
            while (running) {
                switch (alt.priSelect()) {
                    case 0: // feedback input
                        running = feedback.read()
                        altRead = true
//                    println "\t\tEWF: stopping in feedback part"
                        break
                    case 1: // skip guard
                        Object ec = EmitClass.newInstance()
                        returnCode = callUserMethod(ec, eDetails.dCreateMethod, eDetails.dCreateData, 24)
//                    returnCode = ec.&"${eDetails.dCreateMethod}"( eDetails.dCreateData )
                        if (returnCode == normalContinuation) {
//                        println "\t\tEWF: continuing $ec"
                            output.write(ec)
                            Logger.outputEvent(logPhaseName, timer.read(), ec.getProperty(logPropertyName))
                        } else {
                            running = false
//                        println "\t\tEWF: stopping in data gen part"
                        }
                        break
                }
            }
//        println "\t\tEWF: sending UT $altRead"
            output.write(new UniversalTerminator())
            Logger.endEvent(logPhaseName, timer.read())
//        println "\t\tEWF: sent UT"
            if (!altRead){
                feedback.read()
//            println "\t\tEWF: read the feedback channel after UT sent"
            }

        }
	}

}
