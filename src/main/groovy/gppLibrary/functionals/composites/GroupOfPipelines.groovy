package gppLibrary.functionals.composites

import gppLibrary.CompositeDetails
import gppLibrary.functionals.pipelines.OnePipelineOne
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * A GroupOfPipeline comprises a network of groups parallel occurrences and each pipeline comprises stages
 * Worker processes.  Inputs from the preceding process are assumed
 * to come from a channel input list.
 *
 * @param inputList the ChannelInputList used to read objects into the network created by GroupOfPipeline
 * @param outputList the ChannelOutputList used to output processed data or worker objects
 * @param stages the number of stages in each pipeline
 * @param groups the number of parallel pipelines in the network
 * @param stageOp a list of function identifiers to be associated with each stage of the pipeline
 * @param stageModifier a list of groups lists, each containing stages elements that are the
 * 			stage modifier data for that combination of group and stage. Each entry in could itself be a list of values.
 * @param cDetails A {@link gppLibrary.CompositeDetails} object defining the object that defines each of the stages and groups
 * @param outData a list of groups lists each entry of which comprises stages entries.  Each entry is a boolean value
 * 			such that if true the worker processes in that stage
 * 			will output each processed input object. If false the process will output
 * 			the workerClass once only, after it has processed all the input data objects. If omitted the value defaults to true.
 * @param logPhaseName an optional list of string values, which if specified indicates that the processes in the Pipeline should be logged
 * otherwise the process will not be logged.  Specific stages in the Pipeline can be logged by specifying a string value, otherwise the value must
 * be an empty string
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 */

@CompileStatic
class GroupOfPipelines implements CSProcess{

	ChannelInputList inputList
	ChannelOutputList outputList
	int stages = 2
	CompositeDetails cDetails = null
	int groups = 2
	List <String> stageOp = null
	List stageModifier = null
    List <List <Boolean> > outData = null  //list of lists one set per group then one boolean entry per stage

    List <String> logPhaseNames = null
	String logPropertyName = ""

	void run(){
        int inListSize = inputList.size()
        int outListSize = outputList.size()
        assert stages >= 2 : "GroupOfPipelines: insufficient worker stages, value supplied $stages "
        assert (stageOp != null): "GroupOfPipelines: stageOp MUST be specified, one for each stage of the pipeline"
        assert stageOp.size() == stages : "Group of Pipelines : size of stageOp, ${stageOp.size()}, not equal to number of stages, $stages"
        assert groups == inListSize : "Group of Pipelines: inputList size , $inListSize, not equal to number of groups, $groups"
        assert groups == outListSize : "Group of Pipelines: outputList size , $outListSize, not equal to number of groups, $groups"
        if ( cDetails != null){
            int cgSize = cDetails.cDetails.size() // number of groups
            int csSize = cDetails.cDetails[0].size() // number of stages
            assert cgSize == groups : "Pipeline of Group Collects:  number of groups in cDetails, $cgSize, not equal number of workers, $groups"
            assert csSize == stages : "Pipeline of Group Collects:  number of stages in cDetails, $csSize, not equal number of groups, $stages"
        }
        List <List <String> >  logNames = []
        if (logPhaseNames != null) {
            for ( g in 0 ..< groups){
                List <String> phaseNames = (0..<stages).collect{s -> return (String)"$g, "  + logPhaseNames[s]}
                logNames[g] = phaseNames
            }
        }
        else {
            List <String> phaseNames = (0..<stages).collect{s -> return ""}            
            logNames[0] = phaseNames
        }
        if (outData == null) {
            outData = []
            for ( g in 0 ..< groups){
                List <Boolean> gList = (0..<stages).collect{i -> return true}
                outData << gList
            }
        }
		//TODO  need to organise the list of lists of lists for lDetails initData and finalise data look at outData
		def network = (0 ..< groups).collect { g ->
			new OnePipelineOne( input: (ChannelInput)inputList[g],
							    output: (ChannelOutput)outputList[g],
							    stages: stages,
								pDetails: cDetails == null ? null : cDetails.extractByPipe(g),
                                stageOp: stageOp,                                
                                stageModifier: stageModifier == null ? null : (List)stageModifier[g],
                                outData: outData[g],
                                logPhaseNames: logPhaseNames == null ?  logNames[0] :  logNames[g],
								logPropertyName: logPropertyName)
			}
			new PAR (network).run()
	}

}
