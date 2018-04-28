package gppLibrary.mapReduce

import gppLibrary.DataDetails
import groovyJCSP.ChannelInputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelOutput

class Reduce implements CSProcess {

    ChannelInputList inList
    ChannelOutput output

    DataDetails rDetails
    String reduceMethod = ""
    String getReduction = ""

    void run(){
        int partitions = inList.size()
        int returnCode = 0
        def rc = null
        Class reduceClass = Class.forName(rDetails.dName)
        rc = reduceClass.newInstance()
        returnCode = rc.&"${rDetails.dInitMethod}"(rDetails.dInitData)
        if (returnCode != DataClassInterface.completedOK)
            gpp.DataClass.unexpectedReturnCode("Failed to initialise ${rDetails.dName}", returnCode)
        // assume each preceding Partition outputs just one object
        // probably needs to be made into a loop whne Partition is also looped
        for ( i in 0..< partitions){
            def o = inList[i].read()
            returnCode = rc.&"$reduceMethod"(o)
            if (returnCode != DataClassInterface.completedOK)
                gpp.DataClass.unexpectedReturnCode("$reduceMethod failed in ${rDetails.dName}", returnCode)
        }
        // now output the final reduction
        output.write(rc.&"$getReduction"())
        // now read in the Universal terminators from the Partition processes
        for ( i in 0..< partitions){
            def o = inList[i].read()
            output.write(o)
        }
    }

}
