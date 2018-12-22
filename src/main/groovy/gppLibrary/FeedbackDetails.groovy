package gppLibrary

import groovy.transform.CompileStatic

/**
 * FeedbackDetails is used to create a feedback object, that is either a simple boolean value
 * or more complex object as defined by the method fMethod.  This depends on which Feedback process
 * is used from the package {@link gppLibrary.functionals.transformers.FeedbackProcess} This
 * processes will send their output to an  {@link gppLibrary.terminals.EmitWithFeedback}.
 *
 *
 *@param fName A String containing the name of the Feedback object
 *@param fInitMethod A String containing the name of the Feedback object's InitMethod
 *@param fInitData A List containing the data used to initialise the object
 *@param fEvalMethod A String containing the name of the method defined in the definition object used
 * to evaluate whether feedback is required, called in the FeedbackProcess.
 *@param fCreateMethod the name of a method in the Feedback object that is used to populate an instance of the
 * class fName
 *@usage fEvalMethod(inputObject) where inputObject is an instance of an object read into the process
 * read by a FbProcess in the transformers package.  The method fEvalMethod should return normalContinuation
 * if the input object does not create any feedback.
 * If feedback is required fEvalMethod should return a different value, eg normalTermination.
 * In this case a new instance of fName (fbObject) is created  and initialised using the method (function)
 * fbCreateMethod( inputObject), which modifies the values in fbObject appropriately.
 * The input object will be written to the FeedbackProcess output channel.
 * The fbObject will be written to the FeedbackProcess feedback channel.
 */

@CompileStatic
class FeedbackDetails {
    String fName = ""
    String fInitMethod = ""
    List fInitData = null
    String fEvalMethod = ""
    String fCreateMethod = ""
}
