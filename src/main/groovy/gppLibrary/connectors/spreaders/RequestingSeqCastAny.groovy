package gppLibrary.connectors.spreaders

import gppLibrary.*
import jcsp.lang.*

/**
 * RequestingSeqCastAny writes a signal to the outRequestAny channel; it then reads an object from inResponseAny and then
 * writes the input object to all the processes connected to the outputAny channel.<p>
 * Once the UniversalTerminator is read it will be copied to all of the outputList channel ends.
 * The incoming data is not modified in any manner.<p>
 * Typically this process will be used to send a request for data to a process on another 
 * node in a cluster based system. <p>
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         outRequestAny.write(signal)
 *         o = inResponseAny.read()
 *         for ( i in 1..destinations) outputAny.write(o.clone())
 * </pre>
 * @param outRequestAny A one2any Channel used to write a signal to a previous OneFanRequestedAny process
 * @param inResponseAny A one2Any Channel used to read an incoming data object from the previous process
 * @param outputAny A channel output list to which the incoming data object is written 
 * @param destinations the number of processes connected to the outputAny channel
 *
 */

//@CompileStatic
class RequestingSeqCastAny  implements CSProcess{

	ChannelOutput outRequestAny
	ChannelInput inResponseAny
	ChannelOutput outputAny
	int destinations = 0

	void run() {
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
			outRequestAny.write(signal)
			o = inResponseAny.read()
			if ( !( o instanceof UniversalTerminator)){
				for ( i in 1..destinations) outputAny.write(o.clone())
			}
			else
				running = false
		}
		for ( i in 1..destinations) outputAny.write(o)
	}

}
