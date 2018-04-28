package gppLibrary.functionals.matrix

import gppLibrary.Logger
import gppLibrary.UniversalSeparator
import gppLibrary.UniversalSignal
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

class MultiCoreNode implements CSProcess {

    ChannelInput input
    ChannelOutput output
    String calculationMethod = ""
    int nodeId = -1

    String logPhaseName = ""
    String logPropertyName = ""
    
    @CompileStatic
    void runMethod() {
        boolean running = true
        Object data = new Object()

        while (running){
            data = input.read()
            if ( data instanceof UniversalTerminator){
                running = false
                output.write(new UniversalSignal()) // signal root UT has been read and return UT
            }
            else {
                boolean looping = true
                def signal
                output.write(new UniversalSignal())  // signal we have read new data
                while (looping){
                    signal = input.read() // read signal to do the calculation or end
                    if ( signal instanceof UniversalSeparator){
                        looping = false
                        output.write(new UniversalSignal())
                    }
                    else {
                        data.&"$calculationMethod"(nodeId)
                        output.write(new UniversalSignal()) // signal root calculation has been done
                    }
                }
            }
        } // running
    }

    void run(){
		assert calculationMethod != "" :
            "MultiCoreNode: calculationMethod has not been specified"
        assert nodeId > -1: "MultiCoreNode: NodeId has not been set"

        boolean running = true
        Object data = new Object()

        if (logPhaseName == "") {
            runMethod()
        } //not logging
        else { // logging
            def timer = new CSTimer()
            List logPhase = []
            logPhase << Logger.startLog(logPhaseName, timer.read())
			logPhase << Logger.initLog(logPhaseName, timer.read())
			
            while (running){
                data = input.read()
                if ( data instanceof UniversalTerminator){
                    running = false
                    logPhase << Logger.endEvent( logPhaseName, timer.read())
                    output.write(logPhase) // signal root UT has been read and return the node's logPhase
                }
                else {
                    boolean looping = true
                    def signal
                    logPhase << Logger.inputEvent(logPhaseName, timer.read(), data.getProperty(logPropertyName))
                    output.write(new UniversalSignal())  // signal we have read new data
                    while (looping){
                        signal = input.read() // read signal to do the calculation or end
                        if ( signal instanceof UniversalSeparator){
                            looping = false
                            logPhase << Logger.outputEvent(logPhaseName, timer.read(), data.getProperty(logPropertyName))
                            output.write(new UniversalSignal())
                        }
                        else {
                            logPhase << Logger.workStartEvent( logPhaseName, timer.read())
                            data.&"$calculationMethod"(nodeId)
                            output.write(new UniversalSignal()) // signal root calculation has been done
                            logPhase << Logger.workEndEvent( logPhaseName, timer.read())
                        }

                    }
                }
            } // running
        }// logging

	}

}
