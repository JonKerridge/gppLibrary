package gppJunitTests

import gppLibrary.DataClass
import groovy.transform.CompileStatic
import jcsp.lang.ChannelOutput

@CompileStatic
class FeedbackData extends DataClass {
    static String fbInit = "feedbackInit"
    static String feedbackMethod = "fMethod"
    static int limit = 0

    int feedbackInit (List p){
        limit = p[0]
        return completedOK
    }

    int fMethod (List p){
        TestData td = p[0]
        if (td.instanceNumber > limit) {
//            println "fMethod termination ${td.instanceNumber} limit = $limit"
            return normalTermination
        }
        else {
//            println "fMethod continue ${td.instanceNumber} limit = $limit"
            return normalContinuation
        }
    }
}
