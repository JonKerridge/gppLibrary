package gppLibrary.functionals.transformers

import gppLibrary.DataClass
import gppLibrary.UniversalSignal
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ALT
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

@CompileStatic
class FbManager extends DataClass implements CSProcess {

    ChannelInput request
    ChannelOutput feedback
    ChannelInput sendChan

    void run() {
        def alt = new ALT([sendChan, request])
        boolean bufferFull, running
        bufferFull = false
        running = true
        Object buffer    // could be data or UT
        while (running) {
            switch (alt.priSelect()){
                case 0:  //sendChan
                    buffer = sendChan.read()
                    bufferFull = true
//                    println "FbManager: get feedback data $buffer, $bufferFull"
                    break
                case 1:
                    request.read()  // just read the signal
                    if (bufferFull) {
                        feedback.write(buffer)
//                        println "FbManager: buffer full $buffer"
                        bufferFull = false
                        if (buffer instanceof UniversalTerminator)
                                running = false
                    }
                    else {
                        feedback.write(new UniversalSignal())
                    }
                    break
            }
        }
    }
}
