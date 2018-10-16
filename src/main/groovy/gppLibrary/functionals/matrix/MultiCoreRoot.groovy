package gppLibrary.functionals.matrix


import gppLibrary.Logger
import gppLibrary.UniversalSeparator
import gppLibrary.UniversalSignal
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

class MultiCoreRoot implements CSProcess {

    ChannelInput input
    ChannelOutput output
    ChannelOutputList toNodes
    ChannelInput fromNodes
    int nodes = 0
    int iterations = 0
    double errorMargin = 0.0
    boolean finalOut = true
    String partitionMethod = ""
    String errorMethod = ""
    String updateMethod = ""

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        boolean running = true
        Object data = new Object()

            while (running) {
                data = input.read()
                if ( data instanceof UniversalTerminator)
                    running = false
                else {  // process a new data set
                    data.&"$partitionMethod"(nodes)
                    for ( i in 0 ..< nodes) ((ChannelOutput)toNodes[i]).write(data)
//                    println "sent data to nodes"
                    for ( i in 0 ..< nodes) fromNodes.read()
//                    println "received data acknowledgements"
                    if (iterations != 0){
                       for ( j in 0..< iterations) {
                           for ( k in 0..< nodes) ((ChannelOutput)toNodes[k]).write(new UniversalSignal())
                           for ( k in 0..< nodes) fromNodes.read()
                           data.&"$updateMethod"()
                           if ( !finalOut) output.write(data)
                       }
                    } // end iterations loop
                    else { // looping until errorMargin is satisfed
//                        println "error looping"
                        iterations = 0
                        boolean iterating = true
                        while (iterating) {
                            iterations += 1
                            for ( i in 0 ..< nodes) ((ChannelOutput)toNodes[i]).write(new UniversalSignal())
//                            println "sent do calculation to nodes"
                            for ( i in 0 ..< nodes) fromNodes.read()
//                            println "received calculation acknowledgements"
                            iterating = data.&"$errorMethod"(errorMargin)
                            data.&"$updateMethod"()
//                            println " done the update and iterating = $iterating after $iterations iterations"
                            if ( !finalOut) output.write(data)
                        }
                    } // iterate or loop until differences less than errorMargin
                    // send final result
//                    println "result ${data.M.getByColumn(data.n + 1)}"
                    output.write(data)
                    // now send separator to Nodes
                    for ( i in 0 ..< nodes) ((ChannelOutput)toNodes[i]).write(new UniversalSeparator())
//                    println "sent USep to nodes"
                    for ( i in 0 ..< nodes) fromNodes.read()
//                    println "received USep acknowledgements"
                } // processed data set
            } // running
            // deal with termination; first the nodes
            for ( i in 0 ..< nodes) ((ChannelOutput)toNodes[i]).write(new UniversalTerminator())
//            println "sent UT to nodes"
            for ( i in 0 ..< nodes) fromNodes.read() // get signals to indicate Node termination
//            println "received UT acknowledgements"
            output.write(data)  // data contains a UniversalTerminator    
            }
    void run(){
		assert partitionMethod != "" : "MultiCoreRoot: partitionMethod must be specified"
		assert updateMethod != "" : "MultiCoreRoot: updateMethod must be specified"

        boolean running = true
        Object data = new Object()

        if (logPhaseName == "") { //not logging
            runMethod()
        } // not logging   
        
        else { // logging
            def timer = new CSTimer()
            List logPhase = []
            logPhase << Logger.initLog(logPhaseName, timer.read())

            while (running) {
                data = input.read()
                if ( data instanceof UniversalTerminator)
                    running = false
                else {  // process a new data set
                    logPhase << Logger.inputEvent(data.getProperty(logPropertyName), timer.read())
                    data.&"$partitionMethod"(nodes)
                    logPhase << Logger.workStartEvent(timer.read())
                    for ( i in 0 ..< nodes) toNodes[i].write(data)
//                    println "sent data to nodes"
                    for ( i in 0 ..< nodes) fromNodes.read()
//                    println "received data acknowledgements"
                    if (iterations != 0){
                       for ( j in 0..< iterations) {
                           for ( k in 0..< nodes) toNodes[k].write(new UniversalSignal())
                           for ( k in 0..< nodes) fromNodes.read()
                           data.&"$updateMethod"()
                           if ( !finalOut) output.write(data)
                       }
                   } // end iterations loop
                    else { // looping until errorMargin is satisfed
//                        println "error looping"
                        iterations = 0
                        boolean iterating = true
                        while (iterating) {
                            iterations += 1
                            for ( i in 0 ..< nodes) toNodes[i].write(new UniversalSignal())
//                            println "sent do calculation to nodes"
                            for ( i in 0 ..< nodes) fromNodes.read()
//                            println "received calculation acknowledgements"
                            iterating = data.&"$errorMethod"(errorMargin)
                            data.&"$updateMethod"()
//                            println " done the update and iterating = $iterating after $iterations iterations"
                            if ( !finalOut) output.write(data)
                        }
                    } // iterate or loop until differences less than errorMargin
                    logPhase << Logger.workEndEvent(timer.read())
                    // send final result
//                    println "result ${data.M.getByColumn(data.n + 1)}"
                    output.write(data)
                    // now send separator to Nodes
                    for ( i in 0 ..< nodes) toNodes[i].write(new UniversalSeparator())
//                    println "sent USep to nodes"
                    for ( i in 0 ..< nodes) fromNodes.read()
                    logPhase << Logger.outputEvent(data.getProperty(logPropertyName), timer.read())
//                    println "received USep acknowledgements"
                } // processed data set
            } // running
            // deal with termination; first the nodes
            for ( i in 0 ..< nodes) toNodes[i].write(new UniversalTerminator())
            for ( i in 0 ..< nodes) {
                def nodeLog = fromNodes.read()
                data.log <<  nodeLog // get logPhase from each node
            }
            data.log << logPhase
            output.write(data)  // data contains a UniversalTerminator
        } // logging
	}// run

}
