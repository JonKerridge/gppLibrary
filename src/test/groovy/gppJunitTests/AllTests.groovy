package gppJunitTests

import org.junit.runner.JUnitCore
// runs as a groovy script and provides a sequence of JUnit tests
String logFilePath = "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles"
def dir = new File(logFilePath)
dir.eachFile {file ->
    file.delete()
    println "${file.getName()} deleted"
}

result = JUnitCore.runClasses (
            JUtest01,
            JUtest02,
            JUtest03,
            JUtest04,
            JUtest05,
            JUtest06,
            JUtest07,
        JUtest08,
        JUtest08a,
        JUtest08aLog,
            JUtest09,
            JUtest10,
            JUtest11,
            JUtest12,
            JUtest13,
            JUtest14,
            JUtest15,
            JUtest16,
            JUtest17,
            JUtest18,
            JUtest19,
        JUtest20,
        JUtest20Log,
        JUtest21,
        JUtest22,
        JUtest21Log,
        JUtest22Log,
        JUtest23,
        JUtest23Log,
        JUtest24,
        JUtest24Log,
        JUtest23a,
        JUtest23aLog,
        JUtest24a,
        JUtest24aLog,
        JUtest25,
        JUtest25Log,
        JUtest26,
        JUtest26Log,
            JUtest27,
            JUtest28,
            JUtest29,
            JUtest30,
            JUtest31,
            JUtest32,
            JUtest33,
            JUtest34,
        JUtest35,
        JUtest35a,
            JUtest36,
            JUtest37,
            JUtest38,
            )

String message = "Ran: " + result.getRunCount() +
                 ", Ignored: " + result.getIgnoreCount() +
                  ", Failed: " + result.getFailureCount()
if (result.wasSuccessful()) {
    println "\nSUCCESS! " + message
    println "$logFilePath contains the log files"
} else {
    println "\nFAILURE! " + message + "\n"
    result.getFailures().each {
        println it.toString()
    }
}