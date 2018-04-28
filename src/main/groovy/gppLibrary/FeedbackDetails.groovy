package gppLibrary

import groovy.transform.CompileStatic

/**
 * FeedbackDetails is used to create a feedback object, that is either a simple boolean value
 * or more complex object as defined by the method fMethod.  This depends on which Feedback process
 * is used from the package {@link gppLibrary.functionals.transformers.FeedbackBool} or
 * {@link gppLibrary.functionals.transformers.FeedbackObject}.  These processes will send their output to an
 * {@link gppLibrary.terminals.EmitWithFeedback}.
 *
 *
 *@param fName A String containing the name of the Feedback object
 *@param fInitMethod A String containing the name of the Feedback object's InitMethod
 *@param fInitData A List containing the data used to initialise the object
 *@param fMethod A String containing the name of the method used to create and send the feedback object
 *@usage fMethod(def obj, ChannelOutput out) where obj is an instance of an object read into the process
 *read by a Feedback process in the transformers package.  The channel out will be the one used by the
 *Feedback process.  If fMethod returns a boolean then the connected EmitWithFeedback process will still
 *emit data objects unless the returned boolean is false.
 */

@CompileStatic
class FeedbackDetails {
	String fName = ""
	String fInitMethod = ""
	List fInitData = null
	String fMethod = ""

}
