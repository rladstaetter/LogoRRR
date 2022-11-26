/*
 *  Copyright (c) 2018 NEXTSENSE GmbH. All rights reserved.
 *  This file is subject to the terms and conditions defined in https://www.nextsense-worldwide.com/files/content/unternehmen/T&C.pdf
 */

package app.logorrr.util


import app.logorrr.io.FilePaths

import java.io.{PrintWriter, StringWriter}
import java.util.logging._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

object Constants {
  val R = "\r"
  val N = "\n"
  val RN = s"$R$N"
}


object CanLog {

  val handler = {
    val h = new FileHandler(FilePaths.logFilePath.toAbsolutePath.toString, true)
    h.setLevel(Level.ALL)
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

  lazy val log = {
    val lggr = Logger.getLogger(this.getClass.getName)
    lggr.setLevel(Level.ALL)
    lggr.addHandler(CanLog.handler)
    lggr
  }

  def logInfo(msg: String): Unit = Try(log.info(msg))

  def logWarn(msg: String): Unit = Try(log.warning(msg))

  def logTrace(msg: String): Unit = Try(log.finest(msg))

  def logException(msg: String, t: Throwable): Unit = {
    logError(msg)
    Option(t).foreach {
      t =>
        for (l <- CanLog.throwToString(t).split(Constants.N)) {
          val msg = l.replaceAll(Constants.R, "").replaceAll(Constants.RN, "").replaceAll(Constants.N, "")
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
   * @param a              action to perform
   * @param msg            message to decorate a in the log
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