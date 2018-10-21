package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupAny
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

// log imports
import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

class JUtest30Log {

    // previously 27

    @Test
    public void test() {
        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                collectors: 1,
                logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/30")

        def workers = 3

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def any1 = Channel.one2any()
        def any2 = Channel.any2any()
        def any3 = Channel.any2any()
        def any4 = Channel.any2one()

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

        def group1Details  = new GroupDetails( workers : workers,
        groupDetails: new LocalDetails[workers])
        def group2Details  = new GroupDetails( workers : workers,
        groupDetails: new LocalDetails[workers])
        def group3Details  = new GroupDetails( workers : workers,
        groupDetails: new LocalDetails[workers])

        for ( w in 0..< workers){
            group1Details.groupDetails[w] = new LocalDetails( lName: TestWorker.getName(),
            lInitMethod: TestWorker.init,
            lFinaliseMethod: TestWorker.finalise )
            group2Details.groupDetails[w] = new LocalDetails( lName: TestWorker.getName(),
            lInitMethod: TestWorker.init,
            lFinaliseMethod: TestWorker.finalise )
            group3Details.groupDetails[w] = new LocalDetails( lName: TestWorker.getName(),
            lInitMethod: TestWorker.init,
            lFinaliseMethod: TestWorker.finalise )
        }
        group1Details.groupDetails[0].lInitData = [100, 0]
        group1Details.groupDetails[1].lInitData = [100, 0]
        group1Details.groupDetails[2].lInitData = [100, 0]

        group2Details.groupDetails[0].lInitData = [100, 0]
        group2Details.groupDetails[1].lInitData = [100, 0]
        group2Details.groupDetails[2].lInitData = [100, 0]

        group3Details.groupDetails[0].lInitData = [100, 0]
        group3Details.groupDetails[1].lInitData = [100, 0]
        group3Details.groupDetails[2].lInitData = [100, 0]


        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails,
                logPhaseName: "emit",
                logPropertyName: "w1"  )

        def outFan = new OneFanAny (input: chan1.in(),
        outputAny: any1.out(),
        destinations: workers)

        def stage1 = new AnyGroupAny(inputAny: any1.in(),
        outputAny : any2.out(),
        gDetails: group1Details,
        function: TestData.func1,
        modifier: m1,
        workers: workers,
                logPhaseName: "stage1",
                logPropertyName: "w1")

        def stage2 = new AnyGroupAny(inputAny: any2.in(),
        outputAny: any3.out(),
        gDetails: group2Details,
        function: TestData.func2,
        modifier: m2,
        workers: workers,
                logPhaseName: "stage2",
                logPropertyName: "w1")

        def stage3 = new AnyGroupAny(inputAny: any3.in(),
        outputAny: any4.out(),
        gDetails: group3Details,
        function: TestData.func3,
        modifier: m3,
        workers: workers,
                logPhaseName: "stage3",
                logPropertyName: "w1")

        def inFan = new AnyFanOne( inputAny: any4.in(),
        output: chan2.out(),
        sources: workers)

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails,
                logPhaseName: "collect",
                logPropertyName: "w1",
                visLogChan: logChan.out())

        PAR testParallel = new PAR([logVis, emitter, inFan, stage1, stage2, stage3, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "30Log: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 210)
        assertTrue (er.w2 == 2210)
        assertTrue (er.w3 == 20210)
    }
}