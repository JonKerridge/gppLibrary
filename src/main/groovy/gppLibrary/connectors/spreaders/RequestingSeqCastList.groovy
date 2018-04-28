package gppLibrary.connectors.spreaders

import gppLibrary.*
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * RequestingSeqCastList writes a signal to the outRequestAny channel; it then reads an object from inResponseAny and then
 * writes, in sequence, the input object to all the elements of outputList.<p>
 * Once the UniversalTerminator is read it will be copied to all of the outputList channel ends.
 * The incoming data is not modified in any manner.<p>
 * Typically this process will be used to send a request for data to a process on another 
 * node in a cluster based system. <p>
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         outRequestAny.write(signal)
 *         outputList.broadcastSeq( inResponseAny.read() )
 * </pre>
 * @param outRequestAny A one2any Channel used to write a signal to a previous OneFanRequestedAny process
 * @param inResponseAny A one2Any Channel used to read an incoming data object from the previous process
 * @param outputList A channel output list to which the incoming data object is written 
 * 
 *
 */

@CompileStatic
class RequestingSeqCastList  implements CSProcess{

	ChannelOutput outRequestAny
	ChannelInput inResponseAny
	ChannelOutputList outputList
	
	void run() {
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
			outRequestAny.write(signal)
			o = inResponseAny.read()
			if ( !( o instanceof UniversalTerminator))
				outputList.broadcastSeq(o)
			else
				running = false
		}
		outputList.broadcastSeq(o)

	}

}
