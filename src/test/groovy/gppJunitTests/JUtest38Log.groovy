package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.terminals.Collect
import gppLibrary.terminals.EmitWithLocal
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

// log imports
import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

class JUtest38Log {

    @Test
    public void test() {
        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                collectors: 1,
                logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/38")

        def chan = Channel.one2one()

        def er = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestData.getName() ,
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.createFromLocal,
        lName: TestWorker.getName(),
        lInitMethod: TestWorker.init,
        lInitData: [25, 100])

        def resultDetails = new ResultDetails(rName: TestResult.getName(),
        rInitMethod: TestResult.init,
        rCollectMethod:  TestResult.collector,
        rFinaliseMethod: TestResult.finalise,
        rFinaliseData: [er])


        def emitter = new EmitWithLocal( output: chan.out(),
        eDetails: emitterDetails,
                logPhaseName: "emit",
                logPropertyName: "w1" )

        def collector = new Collect( input: chan.in(),
        rDetails: resultDetails,
                logPhaseName: "collect",
                logPropertyName: "w1",
                visLogChan: logChan.out())

        PAR testParallel = new PAR([logVis, emitter, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "38Log: $er"

        assertTrue (er.finalSum == 2210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }
}
