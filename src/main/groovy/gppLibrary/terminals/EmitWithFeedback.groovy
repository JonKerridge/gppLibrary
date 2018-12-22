package gppLibrary.terminals

import gppLibrary.DataClass
import gppLibrary.DataDetails
import gppLibrary.Logger
import gppLibrary.UniversalSignal
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * The EmitWithFeedback process is used to send data objects of type dataClassName to the rest of the
 * parallel structure; it writes output data objects to one output channel.
 * The process outputs a signal to a {@link gppLibrary.functionals.transformers.FeedbackProcess} to determine whether
 * a feedback data object is available.  If not, the process emits another data object.
 * If a feedback object is available it is processed by the emitFeedbackMethod such that if the method returns zero
 * the EmitWithFeedback process terminates in such a manner that the complete process terminates correctly.
 * The process works whether or not a feedback object is received.
 *  <p>
 *
 * @param output The one2one channel to which data objects are written
 * @param request a one2one channel upon which signals to request feedback are sent, every cycle includes
 * a write to request, a read from the feedback channel and the creation of an emitted object as modified by the
 * feedback object
 * @param feedback The channel used to read a feedback object.
 * @param eDetails A {@link gppLibrary.DataDetails} object that specifies the data class to be emitted
 * @param emitFeedbackMethod the name of a method in the eDetails.dName class that interprets the feedbackObject.
 * It has the signature emitFeedbackMethod(feedbackObject) and can modify static variables of the emit class.
 * If the method returns zero it will cause the EmitWithFeedback process to terminate, otherwise it will continue
 * until such time as all the required emit objects have been output.
 *
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 *
 */

class EmitWithFeedback extends DataClass implements CSProcess {

    ChannelOutput output
    ChannelOutput request
    ChannelInput feedback
    DataDetails eDetails
    String emitFeedbackMethod   // must be a method defined in the eDetails.dName object

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        Class EmitClass = Class.forName(eDetails.dName)
        Object ecInit = EmitClass.newInstance()
        callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 20)

        boolean running, generating, terminating
        running = true
        generating = true
        terminating = false
        int returnCode
        while (running && generating) {
            request.write(new UniversalSignal())
            def fbObject = feedback.read()
//            println "EWF: fbObject is $fbObject"
            switch (fbObject) {
                case UniversalTerminator:   // unlikely this case will ever happen
                    running = false
                    break
                case UniversalSignal:
                    // no feedback so create another object
                    Object ec = EmitClass.newInstance()
                    returnCode = callUserFunction(ec, eDetails.dCreateMethod, eDetails.dCreateData, 24)
                    if (returnCode == normalContinuation) {
                        output.write(ec)
                    } else {
                        output.write(new UniversalTerminator())
                        running = false
                        terminating = true
                    }
                    break
                default:
                    returnCode = callUserFunction(ecInit, emitFeedbackMethod, [fbObject], 34)
//                    println "\tEWF: received feedback - $fbObject, $returnCode"
                    // if returnCode = 0 stop generating
                    if (returnCode == 0) {
                        generating = false
                        output.write(new UniversalTerminator())
                        terminating = true
                    }
                    // needs to be thought about more!!! some feedback methods may return non-zero values
                    break
            }
        }
        while (terminating) {    // waiting for UT on feedback channel
            request.write(new UniversalSignal())
            def fbObject = feedback.read()
            switch (fbObject) {
                case UniversalTerminator:
                    terminating = false
                    break
                default:
                    break
            }
        }
    } // run

    void run() {
        if (logPhaseName == "") {
            runMethod()
        } else {
            //logging required
            def timer = new CSTimer()
            Logger.startLog(logPhaseName, timer.read())
            Class EmitClass = Class.forName(eDetails.dName)
            boolean running = true
            int returnCode
            Object ecInit = EmitClass.newInstance()
            callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 20)
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
                        returnCode = callUserFunction(ec, eDetails.dCreateMethod, eDetails.dCreateData, 24)
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
            if (!altRead) {
                feedback.read()
//            println "\t\tEWF: read the feedback channel after UT sent"
            }

        }
    }

}
