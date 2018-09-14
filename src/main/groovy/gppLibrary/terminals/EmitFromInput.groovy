package gppLibrary.terminals

import gppLibrary.DataClass
import gppLibrary.DataClassInterface
import gppLibrary.LocalDetails
import gppLibrary.Logger
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * EmitFromInput reads ONE object of type dataClassName from its input channel; it then uses the
 * initClass method to set any static variables of the class that are as yet not initialised and then
 * the createInstance method is then called repeatedly to create new instances of the class.
 * Each object is written to the output channel. Once all the required object instances
 * have been created the process writes a {@link gppLibrary.UniversalTerminator} to the output channel.
 * <p>
 * Methods required by class:
 * initClass(initialData)
 * createInstance(createData)
 *
 * <p>
 * @param input The one2one channel from which the base class is read.
 * @param output The one2one channel to which new object instances are written
 * @param eDetails A {@link gppLibrary.LocalDetails} object that specifies the data class to be emitted
 * <p>
 *
*/


class EmitFromInput extends DataClass implements CSProcess {
	ChannelInput input
	ChannelOutput output
	LocalDetails eDetails

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod(){
        int returnCode = -1
        Class LocalClass = Class.forName(eDetails.lName)
        Object lcInit = LocalClass.newInstance()
        def lcBase = input.read()
        assert (lcBase.getClass().isInstance(lcInit)) : "EmitFromInput: input Class not ${eDetails.lName}"
        returnCode = callUserMethod(lcInit, eDetails.lInitMethod, eDetails.lInitData, 21)
//        lcInit.&"${eDetails.lInitMethod}"(eDetails.lInitData)
        boolean running = true
        while (running){
            Object lc = LocalClass.newInstance()
            returnCode = callUserMethod(lc, eDetails.lCreateMethod, [lcBase, eDetails.lCreateData], 15)
            if ( returnCode == DataClassInterface.normalContinuation){
                output.write(lc)
            }
            else
                running = false
        }
        UniversalTerminator ut = (UniversalTerminator) input.read()   // terminator from previous process
        output.write(ut)

    }

	void run() {
        if (logPhaseName == "") {
            runMethod()
        }
        else {
            def timer = new CSTimer()
            List logPhase = []
            logPhase << Logger.startLog(logPhaseName, timer.read())

            int returnCode = -1
    		Class LocalClass = Class.forName(eDetails.lName)
    		Object lcInit = LocalClass.newInstance()
    		def lcBase = input.read()
            assert (lcBase.getClass().isInstance(lcInit)) : "EmitFromInput: input Class not ${eDetails.lName}"

            returnCode = callUserMethod(lcInit, eDetails.lInitMethod, eDetails.lInitData, 21)
//            lcInit.&"${eDetails.lInitMethod}"(eDetails.lInitData)
    		boolean running = true

            logPhase << Logger.initLog(logPhaseName, timer.read())

    		while (running){
    			Object lc = LocalClass.newInstance()
                returnCode = callUserMethod(lc, eDetails.lCreateMethod, [lcBase, eDetails.lCreateData], 15)
                if ( returnCode == DataClassInterface.normalContinuation){
                    output.write(lc)
                    logPhase << Logger.outputEvent(logPhaseName, timer.read(), lc.getProperty(logPropertyName))
                }
                else
                    running = false
    		}
            logPhase << Logger.endEvent(logPhaseName, timer.read())
    		UniversalTerminator ut = input.read()	// terminator from previous process
            ut.log << logPhase
    		output.write(ut)
        }
	}

}