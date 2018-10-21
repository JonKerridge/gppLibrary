package gppJunitTests

import gppLibrary.CompositeDetails
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.composites.PipelineOfGroups
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

// log imports
import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

class JUtest36Log {

    // previously 32

    @Test
    public void test() {
        def stages = 3
        def workers = 3

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2any()
        def chan3 = Channel.any2one()
        def chan4 = Channel.one2one()
        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                collectors: 1,
                logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/36")

        def er = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestData.getName() ,
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

        def resultDetails =
                new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er])


        List [][] initData = new List[workers][stages]
        initData[0][0] = [100, 0]
        initData[1][0] = [100, 0]
        initData[2][0] = [100, 0]

        initData[0][1] = [100, 0]
        initData[1][1] = [100, 0]
        initData[2][1] = [100, 0]

        initData[0][2] = [100, 0]
        initData[1][2] = [100, 0]
        initData[2][2] = [100, 0]

        String wName = TestWorker.getName()
        String initMethod = TestWorker.init
        String finaliseMethod = TestWorker.finalise
        List finalData = null

        CompositeDetails compDetails = new CompositeDetails(workers, stages)
        for ( w in 0..< workers)
            for ( s in 0 ..< stages)
                compDetails.insertCompositeDetails( w,
                        s,
                        wName,
                        initMethod,
                        initData[w][s],
                        finaliseMethod,
                        finalData)

        //println "${compDetails.toString()}"

        def stageOps = [TestData.func1, TestData.func2, TestData.func3]

        def pipeModifiers = [[[0], [0], [0]], [[0], [0], [0]], [[0], [0], [0]]]

        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails,
                logPhaseName: "emit",
                logPropertyName: "w1"  )

        def ofa = new OneFanAny( input: chan1.in(),
        outputAny: chan2.out(),
        destinations: workers)


        def pog = new PipelineOfGroups( workers: workers,
        stages: stages,
        inputAny: chan2.in(),
        outputAny: chan3.out(),
        cDetails: compDetails,
        stageOp: stageOps,
        stageModifier : pipeModifiers,
                logPhaseNames: ["func1", "func2", "func3"],
                logPropertyName: "w1" )

        def afo = new AnyFanOne ( inputAny: chan3.in(),
        output: chan4.out(),
        sources: workers)

        def collector = new Collect( input: chan4.in(),
        rDetails: resultDetails,
                logPhaseName: "collect",
                logPropertyName: "w1",
                visLogChan: logChan.out())




        PAR testParallel = new PAR([logVis, emitter, ofa, pog, afo, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        //

        println "36Log: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 210)
        assertTrue (er.w2 == 210)
        assertTrue (er.w3 == 210)
    }
}
