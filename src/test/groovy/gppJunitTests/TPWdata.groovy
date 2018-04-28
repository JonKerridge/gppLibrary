package gppJunitTests

import gppLibrary.*

class TPWdata extends DataClass{

    static String initMethod = "initClass"
    static String inputMethod = "inputMethod"
    static String workMethod = "workMethod"
    static String outFunction = "outFunction"

    int wData = 0
    static int maxInstances = 0
    static int currentInstance = 1

    int initClass (List d){
        maxInstances = d[0]
        return DataClassInterface.completedOK
    }

    int inputMethod(List params){ //[ [null, inputObject]
        TestData inputObject = params[1]
        wData += inputObject.data
//        println "$wData, ${inputObject.data}"
        return DataClassInterface.completedOK
    }

    int workMethod() {
        wData = wData * 2
//        println "new wData = $wData"
        return DataClassInterface.completedOK
    }

    TestData outFunction() {
        if (currentInstance > maxInstances) return null
        else {
            TestData td = new TestData(data: wData)
            td.instanceNumber = currentInstance
            currentInstance += 1
//            println "returning ${td.toString()}"
            return td
        }
    }

}
