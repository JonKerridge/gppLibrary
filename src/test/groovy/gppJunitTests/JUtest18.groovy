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


class JUtest18 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2oneArray(3)
        def chan3 = Channel.one2oneArray(3)
        def chan4 = Channel.any2one()
        def chan5 = Channel.one2one()
        def outList1 = new ChannelOutputList(chan2)
        def outList2 = new ChannelOutputList(chan3)

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

        def rpcl1 = new RequestingParCastList(outRequestAny: request.out(),
        inResponseAny: response.in(),
        outputList: outList1 )

        def rpcl2 = new RequestingParCastList(outRequestAny: request.out(),
        inResponseAny: response.in(),
        outputList: outList2 )

        def g1 = (0..<3).collect{i ->
            return new NullTestWorker(input: chan2[i].in(),
            output: chan4.out())
        }

        def g2 = (0..<3).collect{i ->
            return new NullTestWorker(input: chan3[i].in(),
            output: chan4.out())
        }

        def afo = new AnyFanOne(inputAny: chan4.in(),
        output: chan5.out(),
        sources: 6)

        def collector = new Collect( input: chan5.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, ofra, rpcl1, rpcl2, afo, collector]+ g1 + g2)
        testParallel.run()
        testParallel.removeAllProcesses()

        println "18: $er"

        assertTrue (er.finalSum == 630)
        assertTrue (er.dataSetCount == 60)
        assertTrue (er.finalInstance == 20)
    }
}
