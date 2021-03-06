package gppLibrary.functionals.matrix

import gppLibrary.UniversalSignal
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

@CompileStatic
class ImageNode implements CSProcess {

    ChannelInput input
    ChannelOutput output
    int nodeId
    String convolutionMethod = ""
    List convolutionData = null
    String functionMethod = ""
    List functionData = null

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void run(){
        if (functionData == null) functionData = []
        if (convolutionData == null) convolutionData = []
        boolean running = true
        Object o = input.read()
        while (running){
            if ( o instanceof UniversalTerminator)
                running = false
            else {
                if (functionMethod == ""){
                    // doing a convolution
                    def parameterList = [nodeId] + convolutionData
//                    println "Manager-$id node $nodeId calling $convolutionMethod $parameterList"
                    o.&"$convolutionMethod"(parameterList)
                }
                else {
                    def parameterList = [nodeId] + functionData
//                    println "Manager-$id node $nodeId calling $functionMethod $parameterList"
                    o.&"$functionMethod"(parameterList)
                }
//                println "Manager-$id node $nodeId returning signal"
                output.write(new UniversalSignal())
                o = input.read()
            }
        } // running
//        println "Manager-$id node $nodeId  has read terminator"
        output.write(o) // the UT previously read
//        println "Manager-$id node $nodeId  has written terminator back to Manager"
    }
}
