package gppLibrary.functionals.evolutionary

import gppLibrary.*
import groovy.transform.CompileStatic
import groovyJCSP.ALT
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class Server extends DataClass implements CSProcess{

    ChannelInputList request
    ChannelOutputList response
    int clients = -1
    LocalDetails serverDetails
    String selectParentsFunction = ""
    String incorporateChildrenMethod = ""
    String addIndividualsMethod = ""
    String carryOnFunction = "" // returns true or false but in error null

    String logPhaseName = ""
    String logPropertyName = ""
    
    @CompileStatic
    void runMethod() {
        boolean running = true
        Object inputObject = new Object()
        int returnCode = -1
        int finished = 0
        Class serverClass = Class.forName(serverDetails.lName)
        def server = serverClass.newInstance()
        //initialise the server class
        returnCode = callUserMethod(server, serverDetails.lInitMethod, serverDetails.lInitData, 29) 
        // now read all the initialised individuals into server
        for ( c in 0 ..< clients) {
            def initialPopulation = (UniversalRequest) ((ChannelInput)request[c]).read()
            // now add the enclosed individuals to the population
            assert (initialPopulation.tag == writeRequest) : 
                "Server expecting writeRequest UniversalRequest"
            returnCode = callUserMethod(server, addIndividualsMethod, initialPopulation.individuals, 30)
        }  
        // now send signal in parallel to the clients to start main processing loop
        def startSignal = []
        for ( i in 0..< clients) startSignal << new UniversalSignal()
        response.write(startSignal)
        // now create the ALT required to access the requestList
        def alt = new ALT(request)
        running = true
        int index = -1
        while (running) {
            index = (clients == 1) ? 0 : alt.fairSelect()
            def input = (UniversalRequest)((ChannelInput)request[index]).read() 
            //input is either a UniversalRequest or UniversalResponse
            if (input.tag == readRequest) {
                int parents = input.count
                UniversalResponse respond = server.&"$selectParentsFunction"(parents)
                assert (respond != null) : 
                        "Client-Server: Server Process $selectParentsFunction returned null"
                // and write the response
                ((ChannelOutput)response[index]).write(respond)
            }
            else { // must be a List of child individuals
                assert (input.tag == writeRequest): 
                    "Client-Server: Server Process expecting request to write evolved children into population"
      //          input.individuals.each{println "$it"}
                returnCode = callUserMethod(server, incorporateChildrenMethod, input.individuals, 31)
            }
            // see if we are terminating
            running = server.&"$carryOnFunction"()  // returns false when loop should terminate
            assert (running != null) : 
                "Client-Server: Server Process $carryOnFunction returned null"
        } // running loop
        // now terminate all the clients some of which will still be working on an evolution
        int terminated = 0
        running = true  //while we terminate the process
        while (running) {
            index = (clients == 1) ? 0 : alt.fairSelect()
            def input = (UniversalRequest)((ChannelInput)request[index]).read() 
            //input is either a UniversalRequest or UniversalResponse
            if (input.tag == readRequest) {
                terminated = terminated + 1 // wait until all clients are awaiting a response
            }
            else { // must be an evolved child being returned
                returnCode = callUserMethod(server, incorporateChildrenMethod, input.individuals, 31)
            }
            if (terminated == clients) running = false
        }
        // now do server finalisation
        returnCode = callUserMethod(server, serverDetails.lFinaliseMethod, serverDetails.lFinaliseData, 32)
        // now send signal in parallel to the clients to terminate main processing loop
        def endSignal = []
        for ( i in 0..< clients) endSignal << new UniversalTerminator()
        response.write(endSignal)
    }
    
    void run() {
        if (logPhaseName == "")
            runMethod()
        else {  // getProperty() of this code cannot be compiled statically
            println "logging version of Server Process not yet implemented"
        }
    }
}
