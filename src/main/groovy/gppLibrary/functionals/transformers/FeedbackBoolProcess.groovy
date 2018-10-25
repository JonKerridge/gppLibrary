package gppLibrary.functionals.transformers

import gppLibrary.DataClass
import gppLibrary.FeedbackDetails
import gppLibrary.Logger
import gppLibrary.UniversalSignal
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ALT
import jcsp.lang.CSProcess
import jcsp.lang.CSTimer
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput
import jcsp.lang.Skip

class FeedbackBoolProcess extends DataClass implements CSProcess {

    ChannelInput input
    ChannelOutput output
    ChannelInput request
    ChannelOutput response

    FeedbackDetails fDetails

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        Class FeedbackClass = Class.forName(fDetails.fName)
        int returnCode = -1
        Object fc = FeedbackClass.newInstance()
        returnCode = callUserMethod(fc, fDetails.fInitMethod, fDetails.fInitData, 22)
        Object inputObject = new Object()
        boolean running = true
        boolean limitReached = false
        boolean dataEnded = false
        while (running ) {
            inputObject = input.read()
            if (inputObject instanceof UniversalTerminator) {
//                println "FbBool terminating in main loop"
                request.read()
                response.write(new UniversalTerminator())
//                println "FbBool sent end of data terminator"
                running = false
                dataEnded = true
            } else {
                returnCode = callUserMethod(fc, fDetails.fMethod, [inputObject], 23)
                if (returnCode == normalContinuation) {
//                    println "FbBool continuing writing $inputObject"
                    output.write(inputObject)
//                    println "FbBool written $inputObject"
                } else {// normal termination; limit reached; need to feedback a false
                    // and check for any remaining incoming objects
//                    println "FbBool: limit reached $inputObject"
                    request.read()
                    response.write(new UniversalSignal())
//                    println "FbBool: limit signal sent"
                    limitReached = true
                    running = false
                }
            }
        }
        // now have to deal with any remaining data after limit reached
        while (limitReached && !dataEnded) {
//            println "Entering first clearing loop $limitReached, $dataEnded"
            // reading more  input objects that may satisfy the limit
            // ignoring others until UniversalTerminator has been read
            // should also deal with case where the limit was not reached
            inputObject = input.read()
            if (inputObject instanceof UniversalTerminator) {
//                println "FbBool terminating in clearing loop"
                dataEnded = true
            } else {
                // have to check if any object has a value less than limit
                returnCode = callUserMethod(fc, fDetails.fMethod, [inputObject], 23)
                if (returnCode == normalContinuation) {
//                    println "FbBool clearing writing within limit $inputObject"
                    output.write(inputObject)
                }
            }
        }
        // deal with remaining data when limit not reached and data not ended
        while (!limitReached && !dataEnded) {
//            println "Entering second clearing loop $limitReached, $dataEnded"
            inputObject = input.read()
            if (inputObject instanceof UniversalTerminator) {
//                println "FbBool terminating in second clearing loop"
                request.read()
                response.write(new UniversalTerminator())
//                println "FbBool sent end of data terminator in second clearing loop"
                dataEnded = true
            }
            // just keep on reading data
        }
        output.write(inputObject) // should be UniversalTerminator
    }


    void run() {
        if (logPhaseName == "") {
            runMethod()
        } else { // logging
            def timer = new CSTimer()
            Logger.startLog(logPhaseName, timer.read())
            Class FeedbackClass = Class.forName(fDetails.fName)
            int returnCode = -1
            Object fc = FeedbackClass.newInstance()
            returnCode = callUserMethod(fc, fDetails.fInitMethod, fDetails.fInitData, 22)
            Object inputObject = new Object()
            boolean running = true
            boolean limitReached = false
            boolean dataEnded = false
            Logger.initLog(logPhaseName, timer.read())
            while (running) {
                inputObject = input.read()
                if (inputObject instanceof UniversalTerminator) {
//                println "FbBool terminating in main loop"
                    request.read()
                    response.write(new UniversalTerminator())
//                println "FbBool sent end of data terminator"
                    running = false
                    dataEnded = true
                } else {
                    Logger.inputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
                    returnCode = callUserMethod(fc, fDetails.fMethod, [inputObject], 23)
                    if (returnCode == normalContinuation) {
//                    println "FbBool continuing writing $inputObject"
                        output.write(inputObject)
                        Logger.outputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
//                    println "FbBool written $inputObject"
                    } else {// normal termination; limit reached; need to feedback a false
                        // and check for any remaining incoming objects
//                    println "FbBool: limit reached $inputObject"
                        request.read()
                        response.write(new UniversalSignal())
//                    println "FbBool: limit signal sent"
                        limitReached = true
                        running = false
                    }
                }
            }
            // now have to deal with any remaining data after limit reached
            while (limitReached && !dataEnded) {
//            println "Entering first clearing loop $limitReached, $dataEnded"
                // reading more  input objects that may satisfy the limit
                // ignoring others until UniversalTerminator has been read
                // should also deal with case where the limit was not reached
                inputObject = input.read()
                if (inputObject instanceof UniversalTerminator) {
//                println "FbBool terminating in clearing loop"
                    dataEnded = true
                } else {
                    Logger.inputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
                    // have to check if any object has a value less than limit
                    returnCode = callUserMethod(fc, fDetails.fMethod, [inputObject], 23)
                    if (returnCode == normalContinuation) {
//                    println "FbBool clearing writing within limit $inputObject"
                        output.write(inputObject)
                        Logger.outputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
                    }
                }
            }
            // deal with remaining data when limit not reached and data not ended
            while (!limitReached && !dataEnded) {
//            println "Entering second clearing loop $limitReached, $dataEnded"
                inputObject = input.read()
                if (inputObject instanceof UniversalTerminator) {
//                println "FbBool terminating in second clearing loop"
                    request.read()
                    response.write(new UniversalTerminator())
//                println "FbBool sent end of data terminator in second clearing loop"
                    dataEnded = true
                }
                else {
                    // just keep on reading data and log it
                    Logger.inputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
                }
             }
            output.write(inputObject) // should be UniversalTerminator
            Logger.endEvent(logPhaseName, timer.read())
        }
    }


}
