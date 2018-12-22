package gppLibrary

import groovy.transform.CompileStatic
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput

/**
 *
 */

@CompileStatic
class LoggingVisualiser implements CSProcess {
    ChannelInput logInput
    int collectors      // the number of parallel Collector processes in the network
    String logFileName  // the full name of the file with path to which the log data will be output



    void run(){
        assert logFileName != "" : "LogFileName must be specified"
        def file = new File(logFileName + "log.csv")
        if (file.exists()) file.delete()
        def writer = file.newPrintWriter()
        boolean running
        def logEntry
        int terminated
        terminated = 0
        running = true
        while (running) {
           logEntry = logInput.read()
           if (logEntry instanceof UniversalTerminator)
               terminated += 1
           else {
               writer.println "${((List) logEntry)[0]}, ${((List) logEntry)[1]}, ${((List) logEntry)[2]}, ${((List) logEntry)[3]}"
               println "${((List) logEntry)[0]}, ${((List) logEntry)[1]}, ${((List) logEntry)[2]}, ${((List) logEntry)[3]}"
           }
           if (collectors == terminated ) running = false
       }
        writer.flush()
        writer.close()
    }
}
