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
        ChannelOutput fbChan = p[1]
        if (td.instanceNumber < limit)
            fbChan.write(true)
        else
            fbChan.write(false)
        return completedOK
    }
}
