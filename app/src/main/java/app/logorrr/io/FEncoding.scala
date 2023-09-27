package app.logorrr.io

import java.nio.file.{Files, Path}

object FEncoding {

  def apply(path: Path): FEncoding = {
    val is = Files.newInputStream(path)
    try {
      val bom = Array.fill[Byte](3)(0)
      is.read(bom)
      if (bom.startsWith(Array(0xFF.toByte, 0xFE.toByte))) {
        UTF16LE
      } else if (bom.startsWith(Array(0xFE.toByte, 0xFF.toByte))) {
        UTF16BE
      } else if (bom.startsWith(Array(0xEF.toByte, 0xBB.toByte, 0xBF.toByte))) {
        UTF8
      } else {
        Unknown
      }
    } finally {
      is.close()
    }
  }
}

class FEncoding(val asString: String)

case object UTF8 extends FEncoding("UTF-8")

case object UTF16LE extends FEncoding("UTF-16LE")

case object UTF16BE extends FEncoding("UTF-16BE")

case object Unknown extends FEncoding("Unknown")