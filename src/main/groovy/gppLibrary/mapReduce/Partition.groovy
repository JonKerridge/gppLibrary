package gppLibrary.mapReduce

import gppLibrary.DataDetails
import gppLibrary.UniversalTerminator
import groovyJCSP.ChannelOutputList
import jcsp.lang.*

class Partition implements CSProcess {

    ChannelInput input
    ChannelOutputList outList

    DataDetails pDetails
    String partitionFunction = ""
    String getPartition = ""
    int partitions = -1

    void run(){
        assert (partitions == outList.size()) :
            "size of outlist ${outList.size()} should equal the number of partions $partitions"
        int returnCode = 0
        def pc = null
        Class partitionClass = Class.forName(pDetails.dName)
        pc = partitionClass.newInstance()
        returnCode = pc.&"${pDetails.dInitMethod}"(pDetails.dInitData)
        if (returnCode != DataClassInterface.completedOK)
            gpp.DataClass.unexpectedReturnCode("Failed to initialise ${pDetails.dName}", returnCode)
        def o = input.read()
        while (  ! (o instanceof UniversalTerminator) ){
            returnCode = pc.&"${partitionFunction}"(o)
            if (returnCode != DataClassInterface.completedOK)
                gpp.DataClass.unexpectedReturnCode("$partitionFunction failed in ${pDetails.dName}", returnCode)
            o = input.read()
        }
        // now output the partitions
        for ( p in 0..< partitions) outList[p].write(pc.&"$getPartition"(p))
        // now copy Universal terminator previously read
        for ( p in 0..< partitions) outList[p].write(o)
    }

}
