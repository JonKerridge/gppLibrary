package gppLibrary.cluster.connectors

import gppLibrary.UniversalSignal
import gppLibrary.UniversalTerminator
import groovyJCSP.*
import jcsp.lang.*

/**
 * The process NodeRequestingSeqCastAny makes a request for data on its request channel and reads the
 * response on its response channel.  The object is output to any of the outputAny channels.<p>
 * 
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         request.write(signal)
 *         o = response.read()
 *         for ( i in 1..destinations) outputAny.write(o.clone())
 * </pre>
 * 
 * @param request A net output channel to which a request for data is written
 * @param response A net input channel from which an input data object is read
 * @param outputAny A one2any channel to which the received data object is written
 * @param destinations An int containing the number of processes connected to the any end of the outputAny channel
 */

class NodeRequestingSeqCastAny implements CSProcess {
	
	ChannelOutput request
	ChannelInput response
	ChannelOutput outputAny
	int destinations
	
	void run(){
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
			request.write(signal)
			o = response.read()
			if ( !( o instanceof UniversalTerminator)){
				for ( i in 1..destinations) outputAny.write(o.clone())
			}
			else
				running = false
		}
		for ( i in 1..destinations) outputAny.write(new UniversalTerminator())

	}

}

