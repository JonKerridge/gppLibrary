package gppLibrary.connectors.spreaders

import gppLibrary.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * OneSeqCastAny is used to connect many source processes to many destination process such that
 * an incoming data object read from input will be written to all the processes connected to the outputAny channel.
 * It essentially provides a one place buffer in that as soon as input is ready 
 * it can be read and then written to the outputAny channels. <p>
 * Once the UniversalTerminator is read from the any end of the input channel, a
 * tally will be kept until all the UniversalTerminator objects are read from all the source processes.
 * The process will then output a UniversalTerminator object to each of the destination processes.
 * The incoming data is not modified in any manner.<p>
 * 
 * <pre>
 * <b>Behaviour:</b>
 *     while true  
 *         o = input.read()     
 *         for ( i in 1..destinations) outputAny.write( o.clone() ) 
 * </pre>
 * @param outputAny A one2any Channel used to write data objects to the next process
 * @param input A  one2one channel from which incoming data objects are read
 * @param destinations The number of destination processes.
 */
//@CompileStatic
class OneSeqCastAny  implements CSProcess{

	ChannelInput input
	ChannelOutput outputAny
	int destinations = 0
	
	void run() {
		def o = input.read()
		boolean running = true
		int terminated = 0
		while ( running ){
			if ( !( o instanceof UniversalTerminator)){
				for ( i in 1..destinations) outputAny.write(o.clone())
				o = input.read()
			}
			else {
				running = false
			}
		}
		for ( i in 1..destinations) outputAny.write(o)
	}

}

