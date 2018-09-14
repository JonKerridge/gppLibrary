package gppLibrary

import groovy.transform.CompileStatic

/**
 * DataClass provides the super class for all data classes used in gpp, it provides
 * a null implementation of all the abstract methods defined in DataClassInterface,
 * thus  all data classes used in gpp should extend DataClass, overriding the methods required for that
 * data class only.<p>
 * The class also implements the interfaces Serializable, so that objects can be transferred over TCP/IP connections
 * and Cloneable so that copies of objects can be created.  If the Class contains some other classes then method clone
 * may need to be overwritten.
 *  <p>
 *
 */
@CompileStatic
class DataClass implements DataClassInterface, Serializable, Cloneable {

    List <String> callerMessages = [
        "Worker: error during Local Class Initialise: ",                        //0
        "Worker: error during Function: ",                                      //1
        "Worker: error during Local Class Finalise: ",                          //2
        "Emit: error during Emit Class Intialise: ",                            //3
        "Emit: error during Emit Class Create: ",                               //4
        "Collect: error during Collect Class Initialise: ",                     //5
        "Collect: error during Collecting: ",                                   //6
        "Collect: error during Finalise: ",                                     //8
        "ThreePhaseWorker: error during Local Class Initialise: ",              //9
        "Three PhaseWorker : error during Input Method: ",                      //10
        "Three PhaseWorker : error during Work method: ",                       //11
        "EmitWithLocal: error during Local Class Initialise: ",                 //12
        "EmitWithLocal: error during Emit Class Intialise: ",                   //13
        "EmitWithLocal: error during Emit Class Create: ",                      //14
        "EmitFromInput: error during Emit Class Create: ",                      //15
        "CombineNto1: error during Local Class Init: ",                         //16
        "CombineNto1: error during Output Class init: ",                        //17
        "CombineNto1: error during call of combineMethod: ",                    //18
        "CombineNto1: error during Output Class finaliseMethod:",               //19
        "EmitWithFeedback: error during Emit Class Intialise: ",                //20
        "EmitFromInput: error during Local Class Init: ",                       //21
        "FeedbackBool: error during Feedback Class Initialise",                 //22
        "N_WayMerge: error during mergeChoice method",                          //23
        "OneIndexedList: destination index invalid",                            //24
        "FeedbackObject: error during init phase",                              //25
        "FeedbackObject: error during invoke",                                  //26        
        "Client-Server: Client Process initialisation method failed",           //27
        "Client-Server: Client process call to request initialisation failed",  //28
        "Client-Server: Server process call to request initialisation failed",  //29
        "Client-Server: Server attempt to initialise server class failed",      //30
        "Client-Server: Server failed to incorporate a new child",              //31
        "Client-Server: Server failed to finalise correctly",                   //32
        "Client-Server: Client process failed to create new individual",        //33
        
        
        
        ]


    /**
     * @param o
     * @param methodName
     * @param parameters
     * @param caller
     * @return
     */
    int callUserMethod ( Object o, String methodName, List parameters, int caller){
        int returnCode
        returnCode = o.&"$methodName" ( parameters)
        if ( returnCode < 0){
            unexpectedReturnCode (callerMessages[caller], methodName, parameters, returnCode)
            return returnCode  // will never be invoked as system exits on error
        }
        else
            return returnCode
    }

    /**
     * @param o
     * @param methodName
     * @param caller
     * @return
     */
    int callUserMethod ( Object o, String methodName, int caller){
        int returnCode
        returnCode = o.&"$methodName" ( )
        if ( returnCode < 0){
            unexpectedReturnCode (callerMessages[caller], methodName, null, returnCode)
            return returnCode  // will never be invoked as system exits on error
        }
        else
            return returnCode
    }
    /**
     * @param o
     * @param methodName
     * @param parameters
     * @param caller
     * @return
     */
    int callUserMethod ( Object o, String methodName, Object parameter, int caller){
        int returnCode
        returnCode = o.&"$methodName" ( parameter)
        if ( returnCode < 0){
            unexpectedReturnCode (callerMessages[caller], methodName, [parameter.toString()], returnCode)
            return returnCode  // will never be invoked as system exits on error
        }
        else
            return returnCode
    }

	/**
	 *
	 * unexpectedReturnCode is called by any process that receives an error code as the return value
	 * from a call to the invoke method.
	 * It causes the whole process network to terminate.
	 *
	 * @param component The name of the process creating the error
	 * @param errCode The negative error code that caused the call to the method
	 */
	static void unexpectedReturnCode ( String component,
                                               String methodName,
                                               List parameters,
                                               int errCode){
		println "Unexpected Error from $component value $errCode"
        println "Calling $methodName with parameters $parameters"
		System.exit(errCode)
	}

	/**
	 *
	 * unexpectedReturnCode is called by any process that receives an error code as the return value
	 * from a call to the invoke method.
	 * It causes the whole process network to terminate.
	 *
	 * @param component The name of the process creating the error
	 * @param errCode The negative error code that caused the call to the method
	 */
	static void unexpectedReturnCode ( String component,
											   int errCode){
		println "Unexpected Error from $component value $errCode"
		System.exit(errCode)
	}



	/**
	 * A null implementation of {@link gppLibrary.DataClassInterface#clone()}<p>
	 * Any clone method MUST ensure that a deep copy is created rather then the default shallow copy.
	 * @return null is returned by default
	 */
	@Override
	public Object clone() {
		return null
	}


	/**
	 * A null implementation of {@link gppLibrary.DataClassInterface#Serialise}
	 *
	 * @usage o.Serialize(), where o is the non-Serializable object
	 * @return the Serializable version of o
	 */
	@Override
	public Object serialize() {
		return null;
	}
}