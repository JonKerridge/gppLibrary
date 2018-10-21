package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.PipelineDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.pipelines.OnePipelineOne
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

// log imports
import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

class JUtest31Log {

    // previously 28

    @Test
    public void test() {
        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                collectors: 1,
                logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/31")

        def stages = 3

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()

        def m1 = [[0], [0], [0]]             // for stage 1
        def m2 = [[100], [100], [100]]       // for stage 2
        def m3 = [[1000], [1000], [1000]]    // for stage 3
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

        def pipeDetails = new PipelineDetails(stages: stages,
        stageDetails: new LocalDetails[stages])

        for ( s in 0..< stages){
            pipeDetails.stageDetails[s] = new LocalDetails()
            pipeDetails.stageDetails[s].lName = TestWorker.getName()
            pipeDetails.stageDetails[s].lInitMethod = TestWorker.init
            pipeDetails.stageDetails[s].lFinaliseMethod = TestWorker.finalise
        }

        pipeDetails.stageDetails[0].lInitData = [25, 10]
        pipeDetails.stageDetails[1].lInitData = [25, 100]
        pipeDetails.stageDetails[2].lInitData = [25, 200]

        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails,
                logPhaseName: "emit",
                logPropertyName: "w1" )

        def pipe = new OnePipelineOne( input: chan1.in(),
        output: chan2.out(),
        stages: 3,
        stageOp: [TestData.func1, TestData.func2, TestData.func3],
        stageModifier: [[0], [0], [0]],
        pDetails: pipeDetails,
                logPhaseNames: ["func1", "func2", "func3"],
                logPropertyName: "w1" )


        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails,
                logPhaseName: "collect",
                logPropertyName: "w1",
                visLogChan: logChan.out())

        PAR testParallel = new PAR([logVis, emitter, pipe, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "31Log: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 410)
        assertTrue (er.w2 == 2210)
        assertTrue (er.w3 == 4210)
    }
}
