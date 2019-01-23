package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.FeedbackDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupAny
import gppLibrary.functionals.transformers.FeedbackSensor
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithFeedback
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

// log imports
import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

class JUtest40Log {

    @Test
    void test() {
        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                collectors: 1,
                logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/40")

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def chan3 = Channel.one2one()
        def chan4 = Channel.one2one()
        def chan5 = Channel.one2one()

        def anyChan1 = Channel.one2any()
        def anyChan2 = Channel.any2one()

        int limit = 15
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
                fCreateMethod: FeedbackData.fbCreateMethod
        )

        def emitter = new EmitWithFeedback(
                output: chan1.out(),
                feedback: chan4.in(),
                eDetails: emitterDetails,
                emitFeedbackMethod: FeedbackData.emitFeedbackMethod,
                logPhaseName: "emit",
                logPropertyName: "instanceNumber"  )

        def ofa = new OneFanAny(destinations: workers,
                input: chan1.in(),
                outputAny: anyChan1.out())

        def workerGroup = new AnyGroupAny(
                inputAny: anyChan1.in(),
                outputAny: anyChan2.out(),
                function: TestData.f1,
                workers: workers,
                logPhaseName: "work",
                logPropertyName: "data")

        def afo = new AnyFanOne(
                sources: workers,
                inputAny: anyChan2.in(),
                output: chan2.out())

        def feedBack = new FeedbackSensor(
                input: chan2.in(),
                output: chan3.out(),
                feedback: chan4.out(),
                request: chan5.in(),
                fDetails: feedbackDetails,
                logPhaseName: "fback",
                logPropertyName: "data" )

        def collector = new Collect( input: chan3.in(),
                rDetails: resultDetails,
                logPhaseName: "collect",
                logPropertyName: "data",
                visLogChan: logChan.out())

        PAR testParallel = new PAR([logVis, emitter, ofa, workerGroup, afo, feedBack, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "40Log: $er"

        assertTrue (er.dataSetCount == (limit))
        assertTrue (er.finalSum == 240)
        assertTrue (er.finalInstance == limit)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 0)
        assertTrue (er.w2 == 0)
        assertTrue (er.w3 == 0)


    }
}
