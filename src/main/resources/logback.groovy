scan("30 seconds")
statusListener(OnConsoleStatusListener)

def appenderPattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

appender("FILE", FileAppender) {
  file = "logs/microservice.log"
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = appenderPattern
  }
}
appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
      pattern = appenderPattern
    }
  }

root(INFO, ["STDOUT"])
logger("org.eclipse.jetty", DEBUG)
logger("org.eclipse.jetty.jmx", INFO)
logger("org.eclipse.jetty.util", INFO)
logger("org.eclipse.jetty.io", INFO)
logger("org.eclipse.jetty.http", INFO)


logger("org.jboss", DEBUG)

jmxConfigurator()