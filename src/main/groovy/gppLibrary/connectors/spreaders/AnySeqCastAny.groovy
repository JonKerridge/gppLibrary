package gppLibrary.connectors.spreaders

import gppLibrary.UniversalTerminator
import jcsp.lang.*

/**
 * AnySeqCastAny is used to connect many source processes to many destination process such that
 * an incoming data object read from inputAny will be written to all the processes connected to the outputAny channel.
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
 * @param inputAny An any2one channel from which incoming data objects are read
 * @param sources The number of source processes connected to the Any channel end. <p>
 * @param destinations The number of destination processes.
 */
//@CompileStatic
class AnySeqCastAny  implements CSProcess{

	ChannelInput inputAny
	ChannelOutput outputAny
	int sources = 0
	int destinations = 0
	
	void run() {
		def o = inputAny.read()
		boolean running = true
		int terminated = 0
		while ( running ){
			if ( !( o instanceof UniversalTerminator)){
				for ( i in 1..destinations) outputAny.write(o.clone())
				o = inputAny.read()
			}
			else {
				terminated = terminated + 1
				if (terminated == sources ) running = false
				else o = inputAny.read()
			}
		}
		for ( i in 1..destinations) outputAny.write(o)
	}

}
