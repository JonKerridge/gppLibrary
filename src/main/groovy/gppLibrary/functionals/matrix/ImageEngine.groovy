package gppLibrary.functionals.matrix

import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * ImageEngine provides a means of processing images in JPG and other standard formats and applying
 * image transformations by means of kernel operations.  The process has been designed so that the
 * output from an ImageEngine can form the input to another so that a sequence of transformations
 * can be undertaken on a sequence of images.<p>
 * Though primarily designed for image processing the engine can also be used for any file based input
 * where the data is processed once and then output, possibly to another ImageEngine for subsequent
 * processing where the transformation output does not have to be written to file
 * between each transformation but is saved in a memory based object. A reference to the object is passed
 * between ImageEngines.<p>
 * The ImageEngine comprises, internally, an ImageRoot or manager process and a number of ImageNode processes
 * that each carry out the required operation on a partition of the complete image.  Each partition must access
 * a distinct part of the total data structure. Each partition will be processed by one of the ImageNode processes.
 * The number of ImageNodes equals the number of partitions. The architecture assumes
 * the data structure is double buffered in that a transformation reads from one buffer to the other buffer.
 * The way in which the kernel convolution operation is carried out is left to the programmer as that is
 * part of the sequential code that has to be written.  In addition to convolutions the engine can carry out a
 * scaler operation such as convert to grey scale; these are defined by a function.  An ImageEngine can
 * carry out a function or a convolution but not both, hence the ability to chain ImageEngines together.<p>
 *
 * @param input The channel used to read the object containing the image
 * @param output The channel used to write the object after transformation
 * @param nodes The number of ImageNode processes
 * @param partitionMethod The name of the method that is used to partition the image.
 * The partitioning structure is saved within the image object.
 * @param convolutionMethod The name of the method that undertakes the convolution operation
 * @param convolutionData Any further data required by the convolution method; typically the kernel and
 * other kernel paramters that are required.
 * @param funtionMethod The name of a function that carries out a scaler operation on the image. For an instance of
 * an ImageEngine only ONE of ConvolutionMethod and FunctionMethod must be specified.
 * @param functionData Any further data required by the function
 * @param updateImageIndexMethod The name of the method used to modify the index of the current buffer holding
 * the transformed image. This means that internal buffering methods can be utilised that require more
 * than double buffering.  The method is only called if an instance of ImageEngine specifies the method name;
 * it defualts to the empty string.  An Image object may have more than one updateImageIndexMethod
 * depending on the application, but only one can be called by a specific ImageEngine instance.
 *
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified<p>
 *
 * An example of the use of ImageEngine can be found at {@link ..\gpp_demos\src\demos\imageProcessing\RunGSImage_gpp.groovy}<p>
 *
 */

@CompileStatic
class ImageEngine implements CSProcess {

    ChannelInput input
    ChannelOutput output
    int nodes
    String partitionMethod = ""
    String convolutionMethod = ""
    List convolutionData = null
    String functionMethod = ""
    List functionData = null
    String updateImageIndexMethod = ""

    String logPhaseName = ""
    String logPropertyName = ""

    void run(){
        assert nodes > 0 : "number of ImageEngine nodes must be greater than 0 : $nodes"
        assert (convolutionMethod == "") && (functionMethod != "") ||
            (convolutionMethod != "") && (functionMethod == ""):
            "Only one of $convolutionMethod or $functionMethod can be specified"
        def toNodes = Channel.one2oneArray(nodes)
        def toNodesList = new ChannelOutputList(toNodes)
        def fromNodes = Channel.any2one()
        def network = []
        for ( n in 0 ..< nodes){
            network << new ImageNode( input: toNodes[n].in(),
                                      output: fromNodes.out(),
                                      nodeId: n,
                                      convolutionMethod: convolutionMethod,
                                      convolutionData: convolutionData,
                                      functionMethod: functionMethod,
                                      functionData: functionData,
                                      logPhaseName: logPhaseName == "" ? "" : (String)"$n, "  + logPhaseName ,
                                      logPropertyName: logPropertyName)
        }
        network << new ImageManager( input: input,
                                     output: output,
                                     toNodesList: toNodesList,
                                     fromNodes: fromNodes.in(),
                                     nodes: nodes,
                                     partitionMethod: partitionMethod,
                                     logPhaseName: ""  + logPhaseName,
                                     logPropertyName: logPropertyName,
                                     updateImageIndexMethod: updateImageIndexMethod)
        new PAR(network).run()
    }
}
