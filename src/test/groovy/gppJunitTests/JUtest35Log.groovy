package gppJunitTests

import gppLibrary.CompositeDetails
import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListFanOne
import gppLibrary.connectors.spreaders.OneFanList
import gppLibrary.functionals.composites.GroupOfPipelines
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

// log imports
import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

class JUtest35Log {

    // previously 31

    @Test
    public void test() {
        def stages = 3
        def workers = 3
        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                collectors: 1,
                logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/35")

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect1 = Channel.one2oneArray(workers)
        def connect2 = Channel.one2oneArray(workers)
        def c1ListIn = new ChannelInputList(connect1)
        def c2ListIn = new ChannelInputList(connect2)
        def clListOut = new ChannelOutputList(connect1)
        def c2ListOut = new ChannelOutputList(connect2)

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

        def ofl = new OneFanList(input: chan1.in(),
        outputList: clListOut)

        def gop = new GroupOfPipelines( groups: workers,
        stages: stages,
        inputList: c1ListIn,
        outputList: c2ListOut,
        cDetails: compDetails,
        stageOp: stageOps,
        stageModifier : pipeModifiers,
                logPhaseNames: ["func1", "func2", "func3"],
                logPropertyName: "w1" )

        def lfo = new ListFanOne ( inputList: c2ListIn,
        output: chan2.out())

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails,
                logPhaseName: "collect",
                logPropertyName: "w1",
                visLogChan: logChan.out())



        PAR testParallel = new PAR([logVis, emitter, ofl, gop, lfo, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        //

        println "35Log: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 210)
        assertTrue (er.w2 == 210)
        assertTrue (er.w3 == 210)
    }
}
