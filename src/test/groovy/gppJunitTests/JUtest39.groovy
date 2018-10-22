package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.FeedbackDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.transformers.FeedbackBool
import gppLibrary.functionals.workers.Worker
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithFeedback
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class JUtest39 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def chan3 = Channel.one2one()
        def chan4 = Channel.one2one()

        def limit = 15

        def er = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestData.getName() ,
                dInitMethod: TestData.totalInitialise,
                dInitData: [20],
                dCreateMethod: TestData.create)

        def resultDetails = new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er])

        def feedbackDetails = new FeedbackDetails(
                fName: FeedbackData.getName(),
                fInitMethod: FeedbackData.fbInit,
                fInitData: [limit],
                fMethod: FeedbackData.feedbackMethod )

        def emitter = new EmitWithFeedback(
                output: chan1.out(),
                feedback: chan4.in(),
                eDetails: emitterDetails )

        def worker = new Worker(
                input: chan1.in(),
                output: chan2.out(),
                function: TestData.f1
        )

        def feedBack = new FeedbackBool(
                input: chan2.in(),
                output: chan3.out(),
                feedback: chan4.out(),
                fDetails: feedbackDetails
        )

        def collector = new Collect( input: chan3.in(),
                rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, worker, feedBack, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "39: $er"

        assertTrue (er.dataSetCount == (limit))
        assertTrue (er.finalSum == 240)
        assertTrue (er.finalInstance == limit)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 0)
        assertTrue (er.w2 == 0)
        assertTrue (er.w3 == 0)


    }
}
