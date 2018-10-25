package gppJunitTests

// runs as a groovy script and provides a sequence of JUnit tests
String logFilePath = "D:\\IJGradle\\gppLibrary\\src\\test\\groovy\\gppJunitTests\\LogFiles"
def dir = new File(logFilePath)
dir.eachFile {file ->
    file.delete()
    println "${file.getName()} deleted"
}

