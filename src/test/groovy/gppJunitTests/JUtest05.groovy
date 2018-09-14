package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue


class JUtest05 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect = Channel.any2any()
        def destinations = 3
        def sources = 3


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
            eDetails: emitterDetails )

        def outFan = new OneFanAny (input: chan1.in(),
            outputAny: connect.out(),
            destinations: destinations)

        def inFan = new AnyFanOne( inputAny: connect.in(),
            output: chan2.out(),
            sources: sources)

        def collector = new Collect( input: chan2.in(),
            rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "5: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }

}