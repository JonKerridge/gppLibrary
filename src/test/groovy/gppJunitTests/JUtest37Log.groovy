package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.LocalDetails
import gppLibrary.ResultDetails
import gppLibrary.functionals.workers.ThreePhaseWorker
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

// log imports
import gppLibrary.Logger
import gppLibrary.LoggingVisualiser

class JUtest37Log {

    @Test
    public void test() {

        // log definitions
        def logChan = Channel.any2one()
        Logger.initLogChannel(logChan.out())
        def logVis = new LoggingVisualiser( logInput: logChan.in(),
                collectors: 1,
                logFileName: "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles/37")

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()

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

        def tpwLocal = new LocalDetails(lName: TPWdata.getName(),
            lInitMethod: TPWdata.initMethod,
            lInitData: [2])


        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails,
                logPhaseName: "emit",
                logPropertyName: "w1"  )

        def worker = new ThreePhaseWorker(input: chan1.in(),
        output: chan2.out(),
        lDetails: tpwLocal,
        inputMethod: TPWdata.inputMethod,
        workMethod: TPWdata.workMethod,
        outFunction: TPWdata.outFunction,
                inputLogPropertyName: "w1",
                outputLogPropertyName: "data",
        logPhaseName: "tpw")

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails,
                logPhaseName: "collect",
                logPropertyName: "data",
                visLogChan: logChan.out())

        PAR testParallel = new PAR([logVis, emitter, worker, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

         println "37Log: $er"

       assertTrue (er.finalSum == 840)
        assertTrue (er.dataSetCount == 2)
        assertTrue (er.finalInstance == 2)
    }
}
