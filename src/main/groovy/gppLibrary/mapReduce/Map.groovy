package gppLibrary.mapReduce

import gppLibrary.UniversalTerminator
import jcsp.lang.*

class Map implements CSProcess {

    ChannelInput input
    ChannelOutput output

    String outClassName
    String mapFunction = ""
    String initClass = ""
    List initData = null

    String logPhaseName = ""
    String logPropertyName = ""

    void run(){
        def returnCode = 0
        def o = input.read()
        while ( ! (o instanceof UniversalTerminator)){
            Class outClass = Class.forName(outClassName)
            def oc = outClass.newInstance()
            returnCode = oc.&"$initClass"(initData)
            if (returnCode != DataClassInterface.completedOK)
                gpp.DataClass.unexpectedReturnCode("Failed to initialise $outClassName", returnCode)
            returnCode = oc.&"$mapFunction"(o)
            if (returnCode != DataClassInterface.completedOK)
                gpp.DataClass.unexpectedReturnCode("Map function $mapFunction failed in $outClassName", returnCode)
            output.write(oc)
            o = input.read()
        }
        output.write(o)
    }

}
