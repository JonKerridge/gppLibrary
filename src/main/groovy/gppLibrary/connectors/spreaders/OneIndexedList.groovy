package gppLibrary.connectors.spreaders

import gppLibrary.DataClass
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The OneDirectedList process reads a data object from its input channel and depending
 * on the value contained in its indexProperty will write the object to the corresponding element
 * of the channel output list outputLList.<p>
 * The process does NOT check that the value of the indexProperty to ensure that<br>
 * 0 <= indexProperty < outputList.szie()
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
		def o = input.read()
		while ( ! (o instanceof UniversalTerminator ) ){
//			int index = o.&"$indexFunction"(indexBounds)
            int index = callUserMethod(o, indexFunction, indexBounds, 24)
//			if ((index < 0) || (index >= destinations))
//				gpp.DataClass.unexpectedReturnCode("OneIndexedList: destination index invalid", index)
			((ChannelOutput)outputList[index]).write(o)
			o = input.read()
		}
		for ( i in 0 ..< destinations) ((ChannelOutput)outputList[i]).write(o)
	}

}
