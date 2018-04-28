package gppLibrary.connectors.spreaders

import gppLibrary.UniversalSignal
import gppLibrary.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * RequestingFanAny writes a signal to the outRequestAny channel; it then reads an object from inResponseAny and then
 * writes the input object to the output channel.<p>
 * Once the UniversalTerminator is read it will be copied to all of the Any channel ends.
 * The incoming data is not modified in any manner.<p>
 * Typically this process will be used to send a request for data to a process on another 
 * node in a cluster based system. <p>
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         outRequestAny.write(signal)
 *         outputAny.write( inResponseAny.read() )   
 * </pre>
 * @param outRequestAny A one2any Channel used to write a signal to a previous OneFanRequestedAny process
 * @param inResponseAny A one2Any Channel used to read an incoming data object from the previous process
 * @param outputAny A one2Any or possibly one2one channel to which the incoming data object is written 
 * @param destinations The number of receiving processes connected to the Any channel end.<p>
 * 
 *
 */

@CompileStatic
class RequestingFanAny  implements CSProcess{
	
	ChannelOutput outRequestAny
	ChannelInput inResponseAny
	ChannelOutput outputAny
	int destinations = 1
	
	void run() {
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
			outRequestAny.write(signal)
			o = inResponseAny.read()
			if ( !( o instanceof UniversalTerminator)){
				outputAny.write(o)
			}
			else
				running = false
		}
		for ( i in 1..destinations) outputAny.write(o)
	}

}
