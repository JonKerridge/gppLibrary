package gppLibrary.functionals.transformers

import gppLibrary.DataClass
import gppLibrary.UniversalSignal
import groovy.transform.CompileStatic
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput


@CompileStatic
class FeedbackChecker extends DataClass implements CSProcess {

    ChannelOutput feedback
    ChannelInput response
    ChannelOutput request
    void run(){
        // only does this once!!
//        println "\tFbC: sending signal"
        request.write(new UniversalSignal())
//        println "\tFbC: written signal"
        if (response.read() instanceof UniversalSignal) {
//            println "\tFbC: response read sending false"
            feedback.write(false)
//            println "\tFbC: written false to feedback channel"
        }
        else {
//            println "\tFbC: response read UT"
            feedback.write(true)
//            println "\tFbC: written true to feedback channel"
        }
//        println "\tFbC: terminating"
    }
}
