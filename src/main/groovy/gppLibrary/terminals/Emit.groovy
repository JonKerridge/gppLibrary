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
 * The Emit process is used to send newly instanced data objects of type emitClassName to the rest of the
 * parallel structure.  It sends output data objects to one output channel.
 * Once the all the data objects have been created the process terminates and
 * writes a UniversalTerminator object to the output channel. <p>
 *
 * <pre>
 * <b>Methods required by class emitClassName:</b>
 *     initClass( initialData )
 *     createInstance( createData )
 *
 * <b>Behaviour:</b>
 *     ec = emitClass.newInstance()
 *     ec.initClass(initialData)
 *     while  ec.createInstance(createData) == normalContinuation
 *         output.write(ec)
 *         ec = emitClass.newInstance()
 * </pre>
 * 	<p>
 * @param output The one2one channel to which data objects are written
 * @param eDetails A {@link gppLibrary.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the eDetails object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 */

class Emit extends DataClass implements CSProcess {

    ChannelOutput output
    DataDetails eDetails

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        Class EmitClass = Class.forName(eDetails.dName)
        boolean running = true
        int returnCode = -1
        Object ecInit = EmitClass.newInstance()
        returnCode = callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 3)

        while (running){
            Object ec = EmitClass.newInstance()
            returnCode = callUserMethod(ec, eDetails.dCreateMethod, eDetails.dCreateData, 4)
            if ( returnCode == normalContinuation)
                output.write(ec)
            else
                running = false
        }
        output.write(new UniversalTerminator())
    }

    void run(){
        if (logPhaseName == "") {
            runMethod()
        }
        else {
            //logging required
			logPhaseName = "" + logPhaseName
            def timer = new CSTimer()

            Logger.startLog(logPhaseName, timer.read())
            Class EmitClass = Class.forName(eDetails.dName)
            boolean running = true
            int returnCode = -1
            Object ecInit = EmitClass.newInstance()
            returnCode = callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 3)

            Logger.initLog(logPhaseName, timer.read())

            while (running){
                Object ec = EmitClass.newInstance()
                returnCode = callUserMethod(ec, eDetails.dCreateMethod, eDetails.dCreateData, 4)
                if ( returnCode == DataClassInterface.normalContinuation)  {
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
