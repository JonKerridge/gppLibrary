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
            JUtest21Log,
            JUtest22,
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
            JUtest27Log,
            JUtest28,
            JUtest28Log,
            JUtest29,
            JUtest29Log,
            JUtest30,
            JUtest30Log,
            JUtest31,
            JUtest31Log,
            JUtest32,
            JUtest32Log,
            JUtest33,
            JUtest33Log,
            JUtest34,
            JUtest34Log,
            JUtest35,
            JUtest35Log,
            JUtest35a,
            JUtest35aLog,
            JUtest36,
            JUtest36Log,
            JUtest37,
            JUtest37Log, //TPWdata required change to outFunction to make work
            JUtest38,
            JUtest38Log,
            JUtest39,
//            JUtest39Log,
            JUtest40,
//            JUtest40Log,
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