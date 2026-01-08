package app.logorrr.io

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.file.{Files, Path}

object FEncoding:

  def apply(path: Path): FEncoding =
    val is = Files.newInputStream(path)
    apply(is)

  def apply(asBytes: Array[Byte]): FEncoding =
    apply(new ByteArrayInputStream(asBytes))

  private def apply(is: InputStream): FEncoding =
    try
      val bom = Array.fill[Byte](3)(0)
      is.read(bom)
      if bom.startsWith(Array(0xFF.toByte, 0xFE.toByte)) then
        UTF16LE
      else if bom.startsWith(Array(0xFE.toByte, 0xFF.toByte)) then
        UTF16BE
      else if bom.startsWith(Array(0xEF.toByte, 0xBB.toByte, 0xBF.toByte)) then
        UTF8
      else
        Unknown
    finally
      is.close()

abstract class FEncoding(val asString: String) extends Product

case object UTF8 extends FEncoding("UTF-8")

case object UTF16LE extends FEncoding("UTF-16LE")

case object UTF16BE extends FEncoding("UTF-16BE")

case object Unknown extends FEncoding("Unknown")