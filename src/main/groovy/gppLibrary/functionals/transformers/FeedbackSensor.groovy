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
class FeedbackSensor extends DataClass implements CSProcess {

    ChannelInput input
    ChannelOutput output
    ChannelInput request
    ChannelOutput feedback
    FeedbackDetails fDetails

    String logPhaseName = ""
    String logPropertyName = ""


    void run(){
        One2OneChannel sendChan = Channel.one2one()
        def fbManager = new FbManager(
                request: request,
                feedback: feedback,
                sendChan: sendChan.in()
        )
        def fbProcess = new FbProcess(
                input: input,
                output: output,
                sendChan: sendChan.out(),
                logPhaseName: logPhaseName,
                logPropertyName: logPropertyName,
                fDetails: fDetails
        )
        new PAR([fbManager, fbProcess]).run()
    }

}
