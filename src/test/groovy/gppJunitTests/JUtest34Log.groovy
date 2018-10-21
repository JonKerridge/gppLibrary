package gppJunitTests

import gppLibrary.CompositeDetails
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.spreaders.OneSeqCastAny
import gppLibrary.functionals.composites.PipelineOfGroupCollects
import gppLibrary.terminals.Emit
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

// log imports
import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

class JUtest34Log {

    // previously 33

    @Test
    public void test() {
        def stages = 3
        def workers = 3
        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                collectors: workers,
                logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/34")


        def chan1 = Channel.one2one()
        def chan2 = Channel.one2any()

        def er = new TestExtract()
        def er1 = new TestExtract()
        def er2 = new TestExtract()
        def er3 = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestData.getName() ,
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

        List resultDetails = []

        resultDetails <<
                new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er1])

        resultDetails <<
                new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er2])

        resultDetails <<
                new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er3])

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
        eDetails: emitterDetails ,
                logPhaseName: "emit",
                logPropertyName: "w1" )

        def ofa = new OneSeqCastAny( input: chan1.in(),
        outputAny: chan2.out(),
        destinations: workers)

        def pog = new PipelineOfGroupCollects( workers: workers,
        stages: stages,
        inputAny: chan2.in(),
        cDetails: compDetails,
        stageOp: stageOps,
        stageModifier : pipeModifiers,
        rDetails: resultDetails,
                logPhaseNames: ["func1", "func2", "func3", "collect"],
                logPropertyName: "w1" ,
                visLogChan: logChan.out())


        PAR testParallel = new PAR([logVis, emitter, ofa, pog])
        testParallel.run()
        testParallel.removeAllProcesses()

        er.finalSum = er1.finalSum + er2.finalSum + er3.finalSum
        er.dataSetCount = er1.dataSetCount + er2.dataSetCount + er3.dataSetCount
        er.finalInstance = Math.max(er1.finalInstance, er2.finalInstance)
        er.finalInstance = Math.max(er.finalInstance, er3.finalInstance)
        er.maxClone = Math.max(er1.maxClone, er2.maxClone)
        er.maxClone = Math.max(er.maxClone, er3.maxClone)
        er.w1 = er1.w1 + er2.w1 + er3.w1
        er.w2 = er1.w2 + er2.w2 + er3.w2
        er.w3 = er1.w3 + er2.w3 + er3.w3

        println "34Log: $er"

        assertTrue (er.finalSum == 630)
        assertTrue (er.dataSetCount == 60)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 60)
        assertTrue (er.w1 == 630)
        assertTrue (er.w2 == 630)
        assertTrue (er.w3 == 630)
    }
}
