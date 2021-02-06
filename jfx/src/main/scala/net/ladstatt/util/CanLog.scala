package net.ladstatt.util

trait CanLog {

  def logTrace(s: String): Unit = println("FINEST:" + s)

  def timeR[T](a: => T, s: String): T = {
    val before = System.currentTimeMillis()
    val r = a
    val after = System.currentTimeMillis()
    println(s + s" (duration: ${after - before} millis)")
    r
  }
}
