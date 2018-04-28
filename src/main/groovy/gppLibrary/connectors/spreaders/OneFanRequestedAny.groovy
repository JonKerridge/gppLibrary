package gppLibrary.connectors.spreaders

import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * OneFanRequestedAny reads a value from the input channel; it then reads a signal from the inRequestAny and then
 * writes the input object to the outResponseAny channel.<p>
 * Once the UniversalTerminator is read it will be copied to all of the Any channel ends.
 * The incoming data is not modified in any manner.<p>
 * Typically this process will be used to respond to a request for data by a process on another 
 * node in a cluster based system. <p>
 * <pre>
 * <b>Behaviour:</b>
 *     o = input.read()
 *     while true  
 *         inRequestAny.read()     
 *			outResponseAny.write(o)
 *			o = input.read() 
 * </pre>
 * @param input A one2one Channel used to read data objects from the previous process
 * @param inRequestAny A one2Any Channel used to read a signal from the requesting process
 * @param outResponseAny A one2Any or one2one channel to which the incoming data object is written to the requesting process
 * @param destinations The number of receiving processes connected to the Any channel end.<p>
 * 
 *
 */

@CompileStatic
class OneFanRequestedAny  implements CSProcess{
	
	ChannelInput input
	ChannelInput inRequestAny
	ChannelOutput outResponseAny
	int destinations = 1
	
	void run() {
		def o = input.read()
		def req = null
		while ( ! ( o instanceof UniversalTerminator)){
			req = inRequestAny.read()
			outResponseAny.write(o)
			o = input.read()
		}
		for ( i in 1 .. destinations) {
			req = inRequestAny.read()
			outResponseAny.write(o)
		}
	}

}
