package gppLibrary.functionals.evolutionary

import gppLibrary.DataClass
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.Channel

@CompileStatic
class ParallelClientServerEngine extends DataClass implements CSProcess {
    
    GroupDetails clientDetails = null  // one entry per client Must not be null
    LocalDetails serverDetails
    int clients = -1
    int initialPopulation = -1
    int requiredParents = -1
    int resultantChildren = -1
    String evolveFunction = ""
    String createIndividualFunction = ""
    String selectParentsFunction = ""
    String incorporateChildrenMethod = ""
    String addIndividualsMethod = ""
    String carryOnFunction = "" // returns true or false but in error null

    String logPhaseName = ""
    String logPropertyName = ""
    
     
    void run() {
        assert (clients > 0): 
            "ParallelClientServerEngine property clients MUST be greater than 0 actually $clients"
        assert (clientDetails != null):
            "EAClientServerEngine property gDetails MUST NOT be null"
        def serverToClients = Channel.one2oneArray(clients)
        def clientsToServer = Channel.one2oneArray(clients)
        def serverToClientsListOut = new ChannelOutputList(serverToClients)
        def clientsToServerListIn = new ChannelInputList(clientsToServer)
        def clientsToServerListOut = new ChannelOutputList(clientsToServer)
        def serverToClientsListIn = new ChannelInputList(serverToClients)

        def server = new Server(
            request: clientsToServerListIn,
            response: serverToClientsListOut,
            clients: clients,
            serverDetails: serverDetails,
            selectParentsFunction: selectParentsFunction,
            incorporateChildrenMethod:incorporateChildrenMethod,
            addIndividualsMethod: addIndividualsMethod,
            carryOnFunction: carryOnFunction,
            logPhaseName: logPhaseName,
            logPropertyName: logPropertyName)
        
        def clientNetwork = new ClientGroup (            
                outputList: clientsToServerListOut,
                inputList: serverToClientsListIn,
                clientDetails: clientDetails,
                requiredParents: requiredParents,
                resultantChildren: resultantChildren,
                clients: clients,
                initialPopulation : initialPopulation,
                evolveFunction: evolveFunction,
                createIndividualFunction: createIndividualFunction,
                logPhaseName: logPhaseName,
                logPropertyName: logPropertyName)
        
        new PAR([clientNetwork, server ]).run()        
    }

}
