package gppLibrary.functionals.transformers

import gppLibrary.DataClass
import gppLibrary.LocalDetails
import gppLibrary.Logger
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * CombineNto1 takes any number of input data objects and then combines them into a single
 * output data class.  Intermediate values are formed in a local class. Only a single
 * instance of the local class is created.
 * No modifications are performed on the input data objects, they are only read.
 *
 * Methods required by inputClass none<p>
 * Methods required by localClass:<br>
 * initClass(localInitData)<br>
 * combineMethod (inputClass): operation to transfer data from input class to local class<p>
 * Methods required by outputClass:<br>
 * initClass(outputInitData)<br>
 * finalise (localClass) : copies data from local class into output class
 *
 * <p>
 * @param input A one2one channel from which input data objects are read.
 * @param output A one2one channel to which the final single data output object is written
 * @param localDetails A {@link gppLibrary.LocalDetails} object that specifies the details of a local class
 * @param outDetails A {@link gppLibrary.LocalDetails} object that defines the single output object that results from this process.
 * @param combineMethod A String specifying the name of the operation to be undertaken that combine input data objects into the local worker class
 *
 * <p>
 *
*/

class CombineNto1 extends DataClass implements CSProcess {
	ChannelInput input
	ChannelOutput output
	LocalDetails localDetails
	LocalDetails outDetails
	String combineMethod
	List dataModifier = null		// is this required???

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod(){
        int returnCode = -1
        Class lClass = Class.forName(localDetails.lName)
        def localClass = lClass.newInstance()
        returnCode = callUserMethod(localClass, localDetails.lInitMethod, localDetails.lInitData, 16)

        Class oClass = Class.forName(outDetails.lName)
        def outputObject = oClass.newInstance()
        returnCode = callUserMethod(outputObject, outDetails.lInitMethod, outDetails.lInitData, 17)

        boolean running = true
        Object inputObject = new Object()
        while (running){
            inputObject = input.read()
            if ( inputObject instanceof UniversalTerminator){
                running = false
            }
            else {
                returnCode = callUserMethod(localClass, combineMethod, inputObject, 18)
                // does this need data modifier as well???? if so
            }
        }
        returnCode = callUserMethod(outputObject,outDetails.lFinaliseMethod, [localClass] , 19)
        output.write(outputObject)
        output.write(inputObject)   // the Universal Terminator previously read
    }

	void run(){
        assert localDetails.lName != null : "CombineNto1: A local class MUST be defined"
        assert outDetails.lName != null : "CombineNto1: An output class MUST be defined"

        if (logPhaseName == "") {
            runMethod()
        }
        else { // logging
            def timer = new CSTimer()
            List logPhase = []
            logPhase << Logger.startLog(logPhaseName, timer.read())

    		int returnCode = -1
    		Class lClass = Class.forName(localDetails.lName)
    		def localClass = lClass.newInstance()
            returnCode = callUserMethod(localClass, localDetails.lInitMethod, localDetails.lInitData, 16)

    		Class oClass = Class.forName(outDetails.lName)
    		def outputObject = oClass.newInstance()
            returnCode = callUserMethod(outputObject, outDetails.lInitMethod, outDetails.lInitData, 17)

    		boolean running = true
    		Object inputObject = new Object()
			logPhase << Logger.initLog(logPhaseName, timer.read())
			
    		while (running){
    			inputObject = input.read()
    			if ( inputObject instanceof UniversalTerminator){
    				running = false
    			}
    			else {
                    logPhase << Logger.inputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
                    returnCode = callUserMethod(localClass, combineMethod, inputObject, 18)
                    // does this need data modifier as well???? if so
    			}
    		}
            returnCode = callUserMethod(outputObject,outDetails.lFinaliseMethod, [localClass] , 19)
    		output.write(outputObject)
            logPhase << Logger.outputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))

            // now write the terminating UT that was read previously with log data appended
            logPhase << Logger.endEvent(logPhaseName, timer.read())
            inputObject.log << logPhase
            output.write(inputObject)
        }
	}

}