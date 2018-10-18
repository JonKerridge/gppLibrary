package gppLibrary.functionals.transformers

import gppLibrary.DataClass
import gppLibrary.FeedbackDetails
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * FeedbackBool reads data objects from input, which it processes and writes to output.
 * It also undertakes an evaluation of a function, specified in fDetails, which if true results
 * in the writing of a feedback boolean to an {@link gppLibrary.terminals.EmitWithFeedback} process
 *
 * <p>
 * @param input A ChannelInput used to read input objects
 * @param output A ChannelOutput used to output processed data objects
 * @param feedback A ChannelOutput used to return booleans from this process to a previous {@link gppLibrary.terminals.EmitWithhFeedback) process
 * @param fDetails A {@link gppLibrary.FeedbackDetails} object that specifies the feedback boolean  and the evaluation function
 * <p>
 *
*/


class FeedbackBool extends DataClass implements CSProcess {

	ChannelInput input
	ChannelOutput output
	ChannelOutput feedback
	FeedbackDetails fDetails

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        Class FeedbackClass = Class.forName(fDetails.fName)
        int returnCode = -1
        Object fc = FeedbackClass.newInstance()
        returnCode = callUserMethod(fc, fDetails.fInitMethod, fDetails.fInitData, 22)
        boolean running = true
        Object inputObject = new Object()
        while (running){
            inputObject = input.read()
            if ( inputObject instanceof UniversalTerminator){
                running = false
            }
            else {
                returnCode = callUserMethod(fc, fDetails.fMethod, [inputObject, feedback], 23)
                output.write(inputObject)
            }
        }
        output.write(inputObject)
    }

	void run(){
        if (logPhaseName == "") {
            runMethod()
        }
        else { // logging
    		Class FeedbackClass = Class.forName(fDetails.fName)
    		int returnCode = -1
    		Object fc = FeedbackClass.newInstance()
            returnCode = callUserMethod(fc, fDetails.fInitMethod, fDetails.fInitData, 22)
    		boolean running = true
    		Object inputObject = new Object()
            def timer = new CSTimer()

            Logger.initLog(logPhaseName, timer.read())
    		while (running){
    			inputObject = input.read()
    			if ( o instanceof UniversalTerminator){
    				running = false
    			}
    			else {
                    Logger.inputEvent(inputObject.getProperty(logPropertyName), timer.read())
                    returnCode = callUserMethod(fc, fDetails.fMethod, [inputObject, feedback], 23)
    				output.write(inputObject)
                    Logger.outputEvent(outputObject.getProperty(logPropertyName), timer.read())
    				
    			}
    		}
            Logger.endEvent(timer.read())

    		output.write(inputObject) // Universal terminator
        }
	}

}
