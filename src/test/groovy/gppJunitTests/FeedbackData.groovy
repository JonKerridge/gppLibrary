package gppJunitTests

import gppLibrary.DataClass
import groovy.transform.CompileStatic
import jcsp.lang.ChannelOutput

@CompileStatic
class FeedbackData extends DataClass {
    static String fbCreateMethod = "createFbObject"
    static String fbInit = "feedbackInit"
    static String fbEvalMethod = "evalMethod"
    static int limit = 0

    boolean limitReached

    int createFbObject (List p){
        TestData td = p[0]
        limitReached = true
//        println "FbData: $limitReached, $td"
        return completedOK
    }

    int feedbackInit (List p){
        limit = p[0]
        return completedOK
    }

    int evalMethod (List p){
        TestData td = p[0]
        if (td.instanceNumber > limit) {
//            println "FbDef: evalMethod termination ${td.instanceNumber} limit = $limit"
            return normalTermination
        }
        else {
//            println "FbDef: evalMethod continue ${td.instanceNumber} limit = $limit"
            return normalContinuation
        }
    }


}
