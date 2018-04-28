package gppJunitTests

import gppLibrary.DataDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.AnyFanAny
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue


class JUtest06 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect1 = Channel.any2any()
        def connect2 = Channel.any2any()
        def size1 = 3
        def size2 = 4

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
                                    outputAny: connect1.out(),
                                    destinations: size1)
        def anyFan = new AnyFanAny( inputAny: connect1.in(),
                                    outputAny: connect2.out(),
                                    sources: size1,
                                    destinations: size2)

        def inFan = new AnyFanOne( inputAny: connect2.in(),
                                    output: chan2.out(),
                                    sources: size2)

        def collector = new Collect( input: chan2.in(),
            rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, anyFan, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "6: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }

}
