package gppLibrary.functionals.evolutionary

import gppLibrary.GroupDetails
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

@CompileStatic
class ClientGroup implements CSProcess{

    ChannelInputList inputList
    ChannelOutputList outputList
    GroupDetails clientDetails = null  // one entry per client MUST be present
    int requiredParents = -1
    int resultantChildren = -1
    int initialPopulation = 0
    String evolveFunction = ""
    String createIndividualFunction = "" 
    int clients = -1

    String logPhaseName = ""
    String logPropertyName = ""

    void run() {
        assert (clientDetails != null): "ClientGroup: clientDetails MUST be specified"
        assert (clients == clientDetails.workers): "ClientGroup: Number of workers mismatch, Process exepcted $clients, Details specified ${clientDetails.workers}"
        List network = (0 ..< clients).collect {c ->
            new Client (
                    receive: (ChannelInput)inputList[c],
                    send: (ChannelOutput)outputList[c],
                    clientDetails: clientDetails.groupDetails[c],
                    evolveFunction: evolveFunction,
                    createIndividualFunction: createIndividualFunction,
                    clientId: c,
                    initialPopulation: initialPopulation,
                    requiredParents: requiredParents,
                    resultantChildren: resultantChildren,
                    logPhaseName: logPhaseName == "" ? "" : (String)"$c, "  + logPhaseName ,
                    logPropertyName: logPropertyName)
        }

        new PAR (network).run()

    }

}
