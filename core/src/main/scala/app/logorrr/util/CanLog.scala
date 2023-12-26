package app.logorrr.util

import app.logorrr.io.FilePaths

import java.io.{PrintWriter, StringWriter}
import java.nio.file.Files
import java.util.logging._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

object CanLog {

  val R = "\r"
  val N = "\n"
  val RN = s"$R$N"

  /* before deploying make sure this is set to Level.INFO */
  val LogLevel = Level.INFO

  /** if set to true, the LogoRRRs log file grows without boundaries */
  val appendLogs = false

  val fileHandler: StreamHandler = {
    // check if we can log to the given log path, create the parent directory if it does not exist yet.
    // if this doesn't work, use a fallback to console logger - see https://github.com/rladstaetter/LogoRRR/issues/136
    val h = if (!Files.exists(FilePaths.logFilePath.getParent)) {
      Try(Files.createDirectories(FilePaths.logFilePath.getParent)) match {
        case Success(_) => new FileHandler(FilePaths.logFilePath.toAbsolutePath.toString, appendLogs)
        case Failure(_) => new ConsoleHandler()
      }
    } else {
      new FileHandler(FilePaths.logFilePath.toAbsolutePath.toString, appendLogs)
    }
    h.setLevel(LogLevel)
    h.setFormatter(new SimpleFormatter)
    h
  }

  def throwToString(t: Throwable): String = {
    val sw = new StringWriter
    val pw = new PrintWriter(sw)
    try {
      t.printStackTrace(pw)
      sw.toString // stack trace as a string
    } finally {
      Option(sw).foreach(_.close())
      Option(pw).foreach(_.close())
    }
  }

}

/**
 * Logging facility for our scala applications.
 */
trait CanLog {

  lazy val log: Logger = {
    val lggr = Logger.getLogger(this.getClass.getName)
    lggr.setLevel(CanLog.LogLevel)
    lggr.addHandler(CanLog.fileHandler)
    lggr
  }

  def logInfo(msg: String): Unit = Try(log.info(msg))

  def logWarn(msg: String): Unit = Try(log.warning(msg))

  def logTrace(msg: String): Unit = Try(log.finest(msg))

  def logException(msg: String, t: Throwable): Unit = {
    logError(msg)
    Option(t).foreach {
      t =>
        for (l <- CanLog.throwToString(t).split(CanLog.N)) {
          val msg = l.replaceAll(CanLog.R, "").replaceAll(CanLog.RN, "").replaceAll(CanLog.N, "")
          if (msg.nonEmpty) {
            log.severe(msg)
          }
        }
    }
  }

  def logError(msg: String): Unit = log.severe(msg)

  /**
   * If execution time of function 'a' exceeds errorThreshold, an error log message is written, otherwise a trace log
   *
   * @param a             action to perform
   * @param msg           message to decorate a in the log
   * @param warnThreshold max duration for an operation until a 'exceeded time' log message is issued
   * @tparam A type of result
   * @return
   */
  def timeR[A](a: => A
               , msg: String
               , warnThreshold: FiniteDuration = 500 millis
               , log: String => Unit = logTrace): A = {
    val now = System.nanoTime
    val result = a
    val millis = (System.nanoTime - now) / (1000 * 1000)
    if (millis <= warnThreshold.toMillis) {
      log(s"$msg (duration: $millis ms)")
    } else {
      logWarn(s"$msg (duration: $millis ms [LONG OPERATION])")
    }
    result
  }

}
