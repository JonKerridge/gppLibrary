package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.FeedbackDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.transformers.FeedbackSensor
import gppLibrary.functionals.workers.Worker
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithFeedback
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class JUtest39 {

    @Test
    void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def chan3 = Channel.one2one()
        def chan4 = Channel.one2one()
        def chan5 = Channel.one2one()

        int limit = 15  // tested with 15, 19, 20, 21 and 25

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

//        def feedbackDetails = new FeedbackDetails(
//                fName: FeedbackDefinition.getName(),
//                fInitMethod: FeedbackDefinition.fbInit,
//                fInitData: [limit],
//                fEvalMethod: FeedbackDefinition.fbEvalMethod,
//                fObjectName: FeedbackData.getName(),
//                fCreateMethod: FeedbackData.fbCreateMethod
//        )

        def feedbackDetails = new FeedbackDetails(
                fName: FeedbackData.getName(),
                fInitMethod: FeedbackData.fbInit,
                fInitData: [limit],
                fEvalMethod: FeedbackData.fbEvalMethod,
//                fObjectName: FeedbackData.getName(),
                fCreateMethod: FeedbackData.fbCreateMethod,
//                emitFeedbackMethod: FeedbackData.emitFeedbackMethod
        )

        def emitter = new EmitWithFeedback(
                output: chan1.out(),
                feedback: chan4.in(),
                request: chan5.out(),
                eDetails: emitterDetails,
                emitFeedbackMethod: FeedbackData.emitFeedbackMethod
                )

        def worker = new Worker(
                input: chan1.in(),
                output: chan2.out(),
                function: TestData.f1 )

        def feedBack = new FeedbackSensor(
                input: chan2.in(),
                output: chan3.out(),
                request: chan5.in(),
                feedback: chan4.out(),
                fDetails: feedbackDetails)

        def collector = new Collect( input: chan3.in(),
                rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, worker, feedBack, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "39: $er"

        int sum = 0
        int endVal = limit > 20 ? 20 : limit
        for ( i in 1 .. endVal) sum = sum + i
        sum = sum * 2

        assertTrue (er.dataSetCount == endVal)
        assertTrue (er.finalSum == sum)
        assertTrue (er.finalInstance == endVal)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 0)
        assertTrue (er.w2 == 0)
        assertTrue (er.w3 == 0)
    }
}
