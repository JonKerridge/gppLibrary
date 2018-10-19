package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.ListMergeOne
import gppLibrary.connectors.spreaders.OneFanList
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

class JUtest08aLog {

    @Test
    public void test() {

        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                                            collectors: 1,
                                            logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/8a")

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect = Channel.one2oneArray(4)
        def inList = new ChannelInputList (connect)
        def outList = new ChannelOutputList(connect)

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

        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails,
        logPhaseName: "emit",
        logPropertyName: "w1")

        def outFan = new OneFanList (input: chan1.in(),
        outputList: outList)

        def inFan = new ListMergeOne( inputList: inList,
        output: chan2.out())

        def collector = new Collect( input: chan2.in(),
                rDetails: resultDetails,
                logPhaseName: "collect",
                logPropertyName: "w1",
                visLogChan: logChan.out())

        PAR testParallel = new PAR([logVis, emitter, inFan, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "8aLog: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
    }
}
