package gppJunitTests

import gppLibrary.*

//@CompileStatic
class TestData extends DataClass {

    int data = 0
    int instanceNumber = 0
    int cloneNumber = 0
    int w1 = 0
    int w2 = 0
    int w3 = 0
    static String totalInitialise = "initClass"
    static String partialInitialise = "init"
    static String create = "createInstance"
    static String createFromInput = "createFromInput"
    static String createFromLocal = "createFromLocal"
    static String f1 = "doubleData"
    static String func1 = "func1"
    static String func2 = "func2"
    static String func3 = "func3"
    static String finaliseMethod = "finalise"
    static String combineMethod = "combine"

    int initClass ( List d){
        instances = d[0]
        instance = 1
        cloneInstance = 1
        w1 = 0
        w2 = 0
        w3 = 0
        return DataClassInterface.completedOK
    }

    int init ( List d){
        cloneInstance = 1
        return DataClassInterface.completedOK
    }

    static int instance
    static int instances

    int createInstance (List d){
        if ( instance > instances) return DataClassInterface.normalTermination
        else {
            data = instance
            instanceNumber = instance
            instance = instance + 1
            return DataClassInterface.normalContinuation
        }
    }

    int createFromInput (List d){
        TestData td = d[0]
        def createData = d[1] // null in this case
        if ( instance > instances) return DataClassInterface.normalTermination
        else {
            data = td.data
            instanceNumber = instance
            instance = instance + 1
            cloneNumber = td.cloneNumber
            return DataClassInterface.normalContinuation
        }
    }

    int createFromLocal(List d){
        TestWorker localWorker = d[0]
        if ( instance > instances) return DataClassInterface.normalTermination
        else {
            data = localWorker.consts[instance]
            instanceNumber = instance
            instance = instance + 1
            return DataClassInterface.normalContinuation
        }

        return DataClassInterface.normalContinuation
    }

    int doubleData(List p){
        data = 2 * data
        return DataClassInterface.completedOK
    }

    int func1(List p){
        List modifier = p[0]
        TestWorker wc = p[1]
        w1 += modifier[0] + wc.consts[instanceNumber]
        return DataClassInterface.completedOK
    }

    int func2(List p){
        List modifier = p[0]
        TestWorker wc = p[1]
        w2 += modifier[0] + wc.consts[instanceNumber]
        return DataClassInterface.completedOK
    }

    int func3(List p){
        List modifier = p[0]
        TestWorker wc = p[1]
        w3 += modifier[0] + wc.consts[instanceNumber]
        return DataClassInterface.completedOK
    }

    int finalise (TestData o){
        data = o.data
        instanceNumber = o.instanceNumber
        return DataClassInterface.completedOK
    }

    int combine(TestData o){
        data += o.data
        instanceNumber = o.instanceNumber
        return DataClassInterface.completedOK
    }

    static int cloneInstance = 1
	@Override
    public TestData clone() {
		//println "Creating clone $cloneInstance"
        TestData newTD = new TestData()
        newTD.data = this.data
        newTD.instanceNumber = this.instanceNumber
        newTD.cloneNumber = cloneInstance
        cloneInstance += 1
        return newTD
    }

    String toString() {
        String s = "TestData: $data, $instanceNumber, $instances, $instance, $cloneNumber"
        return s
    }
}
