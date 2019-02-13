package gppLibrary.functionals.evolutionary

import gppLibrary.*
import groovy.transform.CompileStatic
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class Client extends DataClass implements CSProcess{

    ChannelOutput send
    ChannelInput receive
    LocalDetails clientDetails
    int requiredParents = -1
    int resultantChildren = -1
    int initialPopulation = -1
    int clientId = -1
    String evolveFunction = ""  // returns true if evolute runs correctly; all children are expected to be returned
    String createIndividualFunction = ""  // returns completedOK and used in the creation of initialPopulation individuals
    String logPhaseName = ""
    String logPropertyName = ""
    
    @CompileStatic
    void runMethod() {
        boolean running = true
        Object inputObject = new Object()
        int returnCode
        Class clientClass = Class.forName(clientDetails.lName)
        Object individualInit = clientClass.newInstance()
        callUserMethod(individualInit, clientDetails.lInitMethod, clientDetails.lInitData, 27)
        def initialise = new UniversalRequest(tag: writeRequest, count: initialPopulation)
        for ( p in 1 .. initialPopulation) {
            Object individual = clientClass.newInstance()
            returnCode =  individual.&"$createIndividualFunction"()
            if (returnCode == completedOK)
                initialise.individuals << individual   // add an individual created initially by this client
        }   
        // send created individuals to server
        send.write(initialise)          
        // read signal from server to indicate all clients have sent their individuals
        inputObject = receive.read()    
        assert (inputObject instanceof UniversalSignal) : 
            "Client did not receive anticipated response after creating individual(s)"
        running = true
        while (running) {
            // inform server this client needs parents by sending a UniversalRequest read object
            send.write(new UniversalRequest(tag: readRequest, count: requiredParents))  
            // read response from server
            inputObject = receive.read()
            if (inputObject instanceof UniversalTerminator)
                running = false
            else {  //response will be a list of requiredParents and children
                assert (inputObject instanceof UniversalResponse): 
                    "Client did not receive instance of UniversalResponse"
                List parameters = []
                for ( i in 0..< requiredParents) {
                    parameters << ((List)((UniversalResponse)inputObject).payload)[i]
                }
                for ( i in 0..< resultantChildren) {
                    parameters << clientClass.newInstance()
                }
                boolean result = individualInit.&"$evolveFunction"(parameters)
                assert (result != null) :
                    "Client Process: unexpected error from $evolveFunction"
                if (result) {
                    List children = []
                    for ( i in 0..< resultantChildren) children << parameters[requiredParents + i]                       
                    def sendChildren = new UniversalRequest(tag: writeRequest, 
                                                            count: resultantChildren,
                                                            individuals: children)
                    send.write(sendChildren)
                }
            }
        } // while loop
    } // run method


    void run() {
        if (logPhaseName == "")
            runMethod()
        else {  // getProperty() of this code cannot be compiled statically
            println "logging version of Client Process not yet implemented"
        }

    }
}
