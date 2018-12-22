package gppLibrary.functionals.transformers

import gppLibrary.DataClass
import gppLibrary.FeedbackDetails
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class FbProcess extends DataClass implements CSProcess {

    ChannelInput input
    ChannelOutput output
    ChannelOutput sendChan

    FeedbackDetails fDetails

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod(){
        Class FeedbackClass = Class.forName(fDetails.fName)
        int returnCode
        Object fc = FeedbackClass.newInstance()
        callUserMethod(fc, fDetails.fInitMethod, fDetails.fInitData, 22)
        Object inputObject
        boolean running
        running = true
        while (running ) {
            inputObject = input.read()
            if (inputObject instanceof UniversalTerminator) {
                sendChan.write(inputObject)
                running = false
            }
            else { // must be a data object
                // evaluate the feedback function
                returnCode = callUserFunction(fc, fDetails.fEvalMethod, [inputObject], 23)
                if (returnCode == normalContinuation) {
                    // data object does not need to do feedback
                    output.write(inputObject)
//                    println "FbProcess: written $inputObject"
                }
                else { // need to create and send a Feedback object
                    Class FeedbackObject = Class.forName(fDetails.fName)
                    Object fbObject = FeedbackObject.newInstance()
                    callUserMethod(fbObject, fDetails.fCreateMethod, [inputObject], 35)
                    sendChan.write(fbObject)
//                    println "FbProcess: sent feedback object = $fbObject, $inputObject"
                }
            }
        }
        output.write(inputObject)   // should be a UniversalTerminator
    }

    void run() {
        if (logPhaseName == "") {
            runMethod()
        }
        else {
            println "Logging not yet implemented"
        }

    }
}
