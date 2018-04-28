package gppJunitTests

import gppLibrary.UniversalTerminator
import jcsp.lang.*

class NullTestWorker implements CSProcess {
	
	ChannelInput input
	ChannelOutput output
	
	void run(){
		def o = input.read()
		while (!( o instanceof UniversalTerminator)){
			output.write(o)
			o = input.read()
		}
		output.write(new UniversalTerminator())
	}

}
