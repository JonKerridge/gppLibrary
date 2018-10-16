package gppLibrary

import groovy.transform.CompileStatic
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput

/**
 *
 */

class LoggingVisualiser implements CSProcess {
    ChannelInput logInput
    int collectors      // the number of parallel Collector processes in the network



    @CompileStatic
    void run(){
        boolean running
        def logEntry
        int terminated
        terminated = 0
        running = true
        while (running) {
           logEntry = logInput.read()
           if (logEntry instanceof UniversalTerminator)
               terminated += 1
           else
               println "${((List)logEntry)[0]}, ${((List)logEntry)[1]}, ${((List)logEntry)[2]}, ${((List)logEntry)[3]}"
           if (collectors == terminated ) running = false
       }
    }
}
