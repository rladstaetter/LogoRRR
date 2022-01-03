package app.logorrr.util

import org.apache.commons.io.output.ByteArrayOutputStream

import java.io.PrintStream

/**
 * to debug System.err and System.in streams
 */
object LogUtil {

  private def mkStream(baos: ByteArrayOutputStream): PrintStream = {
    val ps = new PrintStream(baos, true, StringUtil.utf8)
    ps
  }

  val errBackingStream = new ByteArrayOutputStream()
  val outBackingStream = new ByteArrayOutputStream()

  def init(): Unit = {
    System.setOut(mkStream(outBackingStream))
    System.setErr(mkStream(errBackingStream))
  }

}
