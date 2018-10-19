package gppLibrary.terminals

import gppLibrary.DataClass
import gppLibrary.DataClassInterface
import gppLibrary.DataDetails
import gppLibrary.Logger
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 *
 * The EmitWithLocal process is used to send newly instanced data objects of type emitClassName to the rest of the
 * parallel structure.  It sends output data objects to one output channel.
 * Once the all the data objects have been created the process terminates and
 * writes a UniversalTerminator object to the output channel. The process makes use of a local class
 * that provides an additional capability for the emit process<p>
 *
 * <pre>
 * <b>Methods required by class emitClassName:</b>
 *     initClass( initialData )
 *     createInstance( createData )
 *
 * <b>Methods required by the local worker class:</b>
 *
 * <b>Behaviour:</b>
 *     ec = emitClass.newInstance()
 *     ec.initClass(initialData)
 *     while  ec.createInstance([localClass, createData]) == normalContinuation
 *         output.write(ec)
 *         ec = emitClass.newInstance()
 * </pre>
 * 	<p>
 * @param output The one2one channel to which data objects are written
 * @param eDetails A {@link gppLibrary.DataDetails} object that specifies the data class to be emitted that
 * specifies a local class
 *
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 */

class EmitWithLocal extends DataClass implements CSProcess {

    ChannelOutput output
    DataDetails eDetails

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        int returnCode = -1
        boolean running = true
        Class EmitClass = Class.forName(eDetails.dName)
        def lc = null
        Class LocalClass = Class.forName(eDetails.lName)
        lc = LocalClass.newInstance()
        returnCode =callUserMethod(lc, eDetails.lInitMethod, eDetails.lInitData, 12)
        Object ecInit = EmitClass.newInstance()
        returnCode = callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 13)
        while (running){
            Object ec = EmitClass.newInstance()
            returnCode = callUserMethod(ec, eDetails.dCreateMethod,  [lc, eDetails.dCreateData] , 14)
            if ( returnCode == DataClassInterface.normalContinuation) {
                output.write(ec)
            }
            else
                running = false

        }
        output.write(new UniversalTerminator())

    }

    void run(){
        assert eDetails.lName != null: "EmitWithLocal: A local worker class MUST be specified"
        if (logPhaseName == "") {	// no logging required
            runMethod()
        }
        else { //logging required
            def timer = new CSTimer()

            Logger.startLog(logPhaseName, timer.read())

            int returnCode = -1
            boolean running = true
            Class EmitClass = Class.forName(eDetails.dName)
            def lc = null
            Class LocalClass = Class.forName(eDetails.lName)
            lc = LocalClass.newInstance()
            returnCode =callUserMethod(lc, eDetails.lInitMethod, eDetails.lInitData, 12)
            Object ecInit = EmitClass.newInstance()
            returnCode = callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 13)

            Logger.initLog(logPhaseName, timer.read())

            while (running){
                Object ec = EmitClass.newInstance()
                returnCode = callUserMethod(ec, eDetails.dCreateMethod, [lc, eDetails.dCreateData], 14)
                if ( returnCode == DataClassInterface.normalContinuation) {
                    output.write(ec)
                    Logger.outputEvent(logPhaseName, timer.read(), ec.getProperty(logPropertyName))
                }
                else
                    running = false
            }
            Logger.endEvent(logPhaseName, timer.read())
            output.write(new UniversalTerminator())
        }
    }

}
