package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.FeedbackDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupAny
import gppLibrary.functionals.transformers.FeedbackProcess
import gppLibrary.functionals.transformers.FeedbackProcess
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithFeedback
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class JUtest40 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def chan3 = Channel.one2one()
        def chan4 = Channel.one2one()
        def chan5 = Channel.one2one()

        def anyChan1 = Channel.one2any()
        def anyChan2 = Channel.any2one()

        int limit = 20 // tested with 10, 15, 19, 20, 21, 25
        int workers = 3

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
                fEvalMethod: FeedbackData.fbEvalMethod,
//                fObjectName: FeedbackData.getName(),
                fCreateMethod: FeedbackData.fbCreateMethod
        )

        def emitter = new EmitWithFeedback(
                output: chan1.out(),
                feedback: chan4.in(),
                request: chan5.out(),
                eDetails: emitterDetails,
                emitFeedbackMethod: TestData.emitFeedbackMethod)

        def ofa = new OneFanAny(destinations: workers,
                input: chan1.in(),
                outputAny: anyChan1.out())

        def workerGroup = new AnyGroupAny(
                inputAny: anyChan1.in(),
                outputAny: anyChan2.out(),
                function: TestData.f1,
                workers: workers)

        def afo = new AnyFanOne(
                sources: workers,
                inputAny: anyChan2.in(),
                output: chan2.out())

        def feedBack = new FeedbackProcess(
                input: chan2.in(),
                output: chan3.out(),
                request: chan5.in(),
                feedback: chan4.out(),
                fDetails: feedbackDetails)

        def collector = new Collect( input: chan3.in(),
                rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, ofa, workerGroup, afo, feedBack, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "40: $er"

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
