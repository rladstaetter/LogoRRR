package net.ladstatt.logboard
/*
import java.nio.file.{Files, Paths}
import java.util.logging.{Formatter, Level, LogManager, LogRecord, Logger}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

case class FormatterWrapper(formatter: Formatter) extends Formatter {
  def format(record: LogRecord): String = {
    record.setSourceClassName("ABCD")
    formatter.format(record)
  }
}

object CanLog {

  @transient
  private final val log: Logger = {
    // initialize logging system
    CanLog.initialize()
    val localLog = Logger.getLogger(this.getClass.getName)
    localLog.getParent.getHandlers.foreach { handler =>
      val formatter = handler.getFormatter
      if (!formatter.isInstanceOf[FormatterWrapper]) {
        handler.setFormatter(FormatterWrapper(formatter))
      }
    }
    localLog
  }

  private[CanLog] def initialize(): Unit = synchronized {
    Try {
      val defaultProperties: String = "/net/ladstatt/logboard/logging.properties"

      val someEnvVar: Option[String] = Option(System.getProperty("java.util.logging.config.file"))

      someEnvVar match {
        case Some(prop) =>
          Try {
            LogManager.getLogManager.readConfiguration(Files.newInputStream(Paths.get(prop)))
            val anonymousLogger = Logger.getAnonymousLogger
            anonymousLogger.setLevel(Level.INFO)
            anonymousLogger.info(s"Logging properties successfully loaded from file system $prop!")
          } match {
            case Success(_) =>
            case Failure(e) =>
              LogManager.getLogManager.readConfiguration(this.getClass.getResourceAsStream(defaultProperties))
              val anonymousLogger = Logger.getAnonymousLogger
              anonymousLogger.setLevel(Level.INFO)
              anonymousLogger.info(s"Could not load logging properties file from $prop, using fallback classpath $defaultProperties.")
          }
        case None =>
          LogManager.getLogManager.readConfiguration(this.getClass.getResourceAsStream(defaultProperties))
          val anonymousLogger = Logger.getAnonymousLogger
          anonymousLogger.setLevel(Level.INFO)
          anonymousLogger.info(s"Logging properties successfully loaded from $defaultProperties.")
      }

      val oldDefaultUncaughtExceptionHandler = Option(Thread.getDefaultUncaughtExceptionHandler)
      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        def uncaughtException(t: Thread, e: Throwable): Unit = {
          Logger.getLogger(this.getClass.getName).log(Level.SEVERE, s"Uncaught exception in thread '$t': $e", e)
          oldDefaultUncaughtExceptionHandler.foreach(_.uncaughtException(t, e))
        }
      })
    }
  }

}

trait CanLog {

  @transient
  private final val log: Logger = {
    CanLog.initialize()
    val localLog = Logger.getLogger(this.getClass.getName)
    localLog.getParent.getHandlers.foreach { handler =>
      val formatter = handler.getFormatter
      if (!formatter.isInstanceOf[FormatterWrapper]) {
        handler.setFormatter(FormatterWrapper(formatter))
      }
    }
    localLog
  }


  def logTrace(msg: String): Unit = Try(log.finest(msg))

  def time[A](a: => A, display: Long => Unit = s => (), divisor: Int = 1000 * 1000): A = {
    val now = System.nanoTime
    val result = a
    val millis = (System.nanoTime - now) / divisor
    display(millis)
    result
  }

  def timeR[A](a: => A, msg: String, errorThreshold: FiniteDuration = 200 millis): A = {
    time(a, t => {
      if (t <= errorThreshold.toMillis) {
        logTrace(s"$msg (duration: $t ms)")
      } else {
        logTrace(s"$msg (duration: $t ms [exceeded threshold of ${errorThreshold.toMillis}])")
      }
    })
  }
}


 */