package gppLibrary.connectors.spreaders

import gppLibrary.DataClass
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The OneIndexedList process reads a data object from its input channel and depending
 * on the value contained in its indexProperty will write the object to the corresponding element
 * of the channel output list outputLList.<p>
 * The process does NOT check that the value of the indexProperty to ensure that<br>
 * 0 <= indexProperty < outputList.size().  This should be checked in the called function,
 * which should return a negative return code that will cause the process network to terminate
 *
 * @param input The channel input from which data objects are read
 * @param outputList the channel output list to which data objects are written
 * @param indexMethod A String containing the name of a function in the input object that returns
 * the element of the outputList to which the object should be written.
 * @param indexBounds a list of values passed to indexMethod that can be used to check
 * that the index returned is within required limits.
 */
@CompileStatic
class OneIndexedList extends DataClass implements CSProcess {

	ChannelInput input
	ChannelOutputList outputList
	String indexFunction
	List indexBounds = null

	void run(){
		int destinations = outputList.size()
		Object inputObject = input.read()
		while ( ! (inputObject instanceof UniversalTerminator ) ){
            int index = callUserFunction(inputObject, indexFunction, indexBounds, 24)
			((ChannelOutput)outputList[index]).write(inputObject)
			inputObject = input.read()
		}
		for ( i in 0 ..< destinations) ((ChannelOutput)outputList[i]).write(inputObject)
	}

}
