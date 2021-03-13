package net.ladstatt.util

trait CanLog {

  // ironic that this application doesn't use proper logging currently ;-)
  def logTrace(s: String): Unit = System.out.println("FINEST: " + s)

  def logError(s: String): Unit = System.err.println("ERROR: " + s)

  def logWarn(s: String): Unit = System.out.println("WARN: " + s)

  def timeR[T](a: => T, s: String): T = {
    val before = System.currentTimeMillis()
    val r = a
    val after = System.currentTimeMillis()
    logTrace(s + s" (duration: ${after - before} millis)")
    r
  }
}
