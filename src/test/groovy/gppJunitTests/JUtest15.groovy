package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.*
import gppLibrary.connectors.spreaders.*
import gppLibrary.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue


class JUtest15 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.any2one()
        def chan3 = Channel.one2one()
        def request = Channel.any2one()
        def response = Channel.one2any()

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

        def ofra = new OneFanRequestedAny(input: chan1.in(),
        inRequestAny: request.in(),
        outResponseAny: response.out(),
        destinations: 2)

        def rfa1 = new RequestingFanAny(outRequestAny: request.out(),
        inResponseAny: response.in(),
        outputAny: chan2.out(),
        destinations: 1)

        def rfa2 = new RequestingFanAny(outRequestAny: request.out(),
        inResponseAny: response.in(),
        outputAny: chan2.out(),
        destinations: 1)

        def afo = new AnyFanOne(inputAny: chan2.in(),
        output: chan3.out(),
        sources: 2)

        def collector = new Collect( input: chan3.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, ofra, rfa1, rfa2, afo, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "15: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
    }
}
