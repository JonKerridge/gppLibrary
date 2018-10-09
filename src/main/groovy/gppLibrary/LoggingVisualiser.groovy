package gppLibrary

import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput

class LoggingVisualiser implements CSProcess {
    ChannelInput logInput
    boolean running = true
    List logEntry

    void run(){
       while (running) {
           logEntry = logInput.read()
           if (logEntry instanceof UniversalTerminator)
               running = false
           else
               println "${logEntry[0]}, ${logEntry[1]}, ${logEntry[2]}, ${logEntry[3]}"
       }
    }
}
