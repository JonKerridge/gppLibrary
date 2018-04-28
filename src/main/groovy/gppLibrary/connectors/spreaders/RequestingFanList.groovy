package gppLibrary.connectors.spreaders

import gppLibrary.*
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * RequestingFanList writes a signal to the outRequestAny channel; it then reads an object from inResponseAny and then
 * writes the input object to the next element of outputList in sequence.<p>
 * Once the UniversalTerminator is read it will be copied to all of the outputList channel ends.
 * The incoming data is not modified in any manner.<p>
 * Typically this process will be used to send a request for data to a process on another 
 * node in a cluster based system. <p>
 * <pre>
 * <b>Behaviour:</b>
 *     currentIndex = 0
 *     while true
 *         outRequestAny.write(signal)
 *         outputList[currentIndex].write( inResponseAny.read() )
 *         currentIndex = (currentIndex + 1) modulus outputList.size()   
 * </pre>
 * @param outRequestAny A one2any Channel used to write a signal to a previous OneFanRequestedAny process
 * @param inResponseAny A one2Any Channel used to read an incoming data object from the previous process
 * @param outputList A channel output list to which the incoming data object is written 
 * 
 *
 */

@CompileStatic
class RequestingFanList  implements CSProcess{
	
	ChannelOutput outRequestAny
	ChannelInput inResponseAny
	ChannelOutputList outputList

	void run() {
		int destinations = outputList.size()
		int currentIndex = 0
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
			outRequestAny.write(signal)
			o = inResponseAny.read()
			if ( !( o instanceof UniversalTerminator)){
				((ChannelOutput)outputList[currentIndex]).write(o)
				currentIndex = currentIndex + 1
				if (currentIndex == destinations) currentIndex = 0
			}
			else
				running = false
		}
		int c = currentIndex
		while ( c < destinations){
			((ChannelOutput)outputList[c]).write(o)
			c = c + 1
		}
		c = 0
		while ( c < currentIndex){
			((ChannelOutput)outputList[c]).write(o)
			c = c + 1
		}
	}

}
