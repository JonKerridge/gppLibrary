package gppLibrary.functionals.transformers

import gppLibrary.DataClass
import gppLibrary.FeedbackDetails
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput
import jcsp.lang.One2OneChannel

@CompileStatic
class FeedbackBool extends DataClass implements CSProcess {

    ChannelInput input
    ChannelOutput output
    ChannelOutput feedback
    FeedbackDetails fDetails

    String logPhaseName = ""
    String logPropertyName = ""

    One2OneChannel requestChan = Channel.one2one()
    One2OneChannel responseChan = Channel.one2one()

    void run() {
        def check = new FeedbackChecker(
                feedback: feedback,
                response: responseChan.in(),
                request: requestChan.out())
        def process = new FeedbackBoolProcess(
                input: input,
                output: output,
                request: requestChan.in(),
                response: responseChan.out(),
                fDetails: fDetails,
                logPhaseName: logPhaseName,
                logPropertyName: logPropertyName)

        new PAR([check, process]).run()
    }


}
