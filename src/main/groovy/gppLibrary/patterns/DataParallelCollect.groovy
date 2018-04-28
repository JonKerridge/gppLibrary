package gppLibrary.patterns

import gppLibrary.DataDetails
import gppLibrary.GroupDetails
import gppLibrary.ResultDetails
import gppLibrary.connectors.*
import gppLibrary.connectors.reducers.AnyFanOne
import gppLibrary.connectors.spreaders.OneFanAny
import gppLibrary.functionals.groups.AnyGroupAny
import gppLibrary.terminals.Collect
import gppLibrary.terminals.Emit
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * A DataParallelCollect comprises a sequence of processes in a so-called Farm.  The sequence comprises
 * Emit; OneFanAny; AnyGroupAny; AnyFanOne; Collect. The properties of the pattern provide all the
 * external values required to run the pattern.  The internal channels required to
 * connect the processes together are all declared within the class outwith programmer concerns.
 * <p>
 * @param eDetails A {@link gppLibrary.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process
 * @param gDetails A {@link gppLibrary.GroupDetails}  object containing information concerning the contained group of workers
 * used by the Emit process
 * @param function A String value identifying the operation to be undertaken
 * 					by the Worker processes
 * @param modifier Contains a possible modifier for the operation, with each Worker
 * 					accessing the element that corresponds to the index of the Worker.
 * @param rDetails A {@link gppLibrary.ResultDetails} object containing data pertaining to result class used by the Collect process, it MUST be specified.
 * @param workers The number of Worker processes that will be created when the Group is run
 * @param outData If true the process will output each processed input object. If false the process will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output
 *                only happens after the finalise method has been called. outData defaults to true.
 * @see gpp.functionals.workers.WorkerTerminating
 * <p>
 *
*/

@CompileStatic
class DataParallelCollect {

	DataDetails eDetails
	int workers
	GroupDetails gDetails = null
	String function
	List modifier = null
	ResultDetails rDetails
	boolean outData = true


	def run = {
		def toFanOut = Channel.one2one()
		def toFarm = Channel.one2any()
		def fromFarm =Channel.any2one()
		def collectChan = Channel.one2one()

		def emitter = new Emit( output: toFanOut.out(),
								eDetails: eDetails)

		def fanOut = new OneFanAny(input: toFanOut.in(),
									outputAny: toFarm.out(),
									destinations: workers)

		def farmer = new AnyGroupAny ( inputAny: toFarm.in(),
										outputAny: fromFarm.out(),
										gDetails : gDetails,
										workers: workers,
										function: function,
										modifier: modifier,
										outData: outData )

		def fanIn = new AnyFanOne(inputAny: fromFarm.in(),
								 output: collectChan.out(),
								 sources: workers)

		def collector = new Collect( input: collectChan.in(),
									 rDetails: rDetails )

		new PAR([emitter, fanOut, farmer, fanIn, collector]).run()
	}
}
