package gppLibrary.functionals.workers

import gppLibrary.DataClass
import gppLibrary.LocalDetails
import gppLibrary.Logger
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.CSProcess
import jcsp.lang.CSTimer
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * ThreePhaseWorker is a specialisation of worker processes that incorporates three distinct
 * phases into the process.<p>
 * Phase 1 reads objects from the input channel which are then stored in a local
 * worker object specified in the LocalDetails lDetails object.  The data is passed as a parameter
 * of the worker objects inFunction.  This phase only terminates when a UniversalTerminator object is read.<p>
 * Phase 2 runs the worker method, workFunction, which processes the input data now stored in the local worker object.<p>
 * Phase 3 causes the output of the processed local worker object data to the output channel using the function outFunction.
 * The outFunction returns an object which may be a different object to that which was read in.  This phase
 * terminates once all the local worker object has been output.  The process then outputs the UniversalTerminator that was
 * previously read.<p>
 *
 * Methods required by the required local worker class specified in lDetails:<br>
 * initClass(workerInitData)<p>
 *
 * inFunction([dataModifer, o]) where o is the input object read from input channel<p>
 * workFunction()<p>
 * outFunction()<p>
 *
 * @param input			The channel from which the input object to be processed is read
 * @param output		The channel to which the processed object is written
 * @param inputMethod  	The name of the method corresponding to the method in the worker object that is to be employed to do the input operation
 * @param workMethod	The name of the method corresponding to the method in the worker object that is to be employed to do the internal work operation
 * @param outFunction	The name of the function corresponding to the function method in the worker object that is to be employed to do the output operation;
 * returns an object or null
 * @param dataModifier	A list of any values to be used by the inputMethod; it is polymorphic in type
 * 						and defaults to null
 * @param lDetails A LocalDetails object containing data pertaining to any local class used by the worker, defaults to null.
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 *
 */
class ThreePhaseWorker extends DataClass implements CSProcess {

    ChannelInput input
    ChannelOutput output
    String inputMethod, workMethod, outFunction
    List dataModifier = null
    LocalDetails lDetails = null

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        int returnCode = -1
        def wc = null
        Class workerClass = Class.forName(lDetails.lName)
        wc = workerClass.newInstance()
        returnCode = callUserMethod(wc, lDetails.lInitMethod, lDetails.lInitData, 9)
        boolean running = true
        Object inputObject = new Object()
        while (running){ // first phase: inputting data into local worker
            inputObject = input.read()
            if ( inputObject instanceof UniversalTerminator)
                running = false
            else {
                returnCode = callUserMethod(wc, inputMethod, [dataModifier, inputObject], 10 )
            }
        }
        // now invoke the second phase function on the local worker
        returnCode = callUserMethod(wc, workMethod, 11)
        //now output the data from the local worker class
        running = true
        Object out = new Object()
        while (running){
            out = wc.&"$outFunction"() // the function possibly will return an object of another class
            if (out == null)
                running = false
            else {
                output.write(out)
            }
        }
        // now write the terminating UT that was read previously
        output.write(inputObject)
    }

    void run(){
        assert lDetails != null : "ThreePhaseWorker: must have a Local Worker Class"
        if (logPhaseName == "") {
            runMethod()
        }
        else { // logging
            def timer = new CSTimer()
            List logPhase = []
            logPhase << Logger.startLog(logPhaseName, timer.read())
            int returnCode = -1
            def wc = null
            Class workerClass = Class.forName(lDetails.lName)
            wc = workerClass.newInstance()
            returnCode = callUserMethod(wc, lDetails.lInitMethod, lDetails.lInitData, 9)

            boolean running = true
            Object inputObject = new Object()
			logPhase << Logger.initLog(logPhaseName, timer.read())
			
            while (running){ // first phase: inputting data into local worker
                inputObject = input.read()
                if ( inputObject instanceof UniversalTerminator)
                    running = false
                else {
                    logPhase << Logger.inputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
                    returnCode = callUserMethod(wc, inputMethod, [dataModifier, inputObject], 10 )
                }
            }
            // now invoke the second phase function on the local worker
            logPhase << Logger.workStartEvent( timer.read())
            returnCode = callUserMethod(wc, workMethod, 11)
            //now output the data from the local worker class
            logPhase << Logger.workEndEvent( timer.read())
            running = true
            Object out = new Object()
            while (running){
                out = wc.&"$outFunction"() // the function possibly will return an object of another class
                if (out == null)
                    running = false
                else {
                    output.write(out)
                    logPhase << Logger.outputEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
                }
            }
            // now write the terminating UT that was read previously with log data appended
            logPhase << Logger.endEvent(logPhaseName, timer.read())
            inputObject.log << logPhase
            output.write(inputObject)
        } //end else
    }
}
