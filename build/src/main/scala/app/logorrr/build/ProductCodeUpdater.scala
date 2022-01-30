package app.logorrr.build

import app.logorrr.util.CanLog

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}
import java.util.UUID
import scala.jdk.CollectionConverters._

object ProductCodeUpdater extends CanLog {

  val needle = """<ROW Property="ProductCode" Value="1033:{aaaaaaaa-bbbb-cccc-dddd-ffffffffffff} " Type="16"/>"""

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      logError(s"Wrong number of arguments, expected 2 but was ${args.length} ")
    } else {
      val installerPath = args(0)
      val p = Paths.get(installerPath)

      if (Files.exists(p)) {
        val version = args(1)
        val installerName = p.getFileName.toString
        val uuid = UUID.nameUUIDFromBytes((installerName + version).map(_.toByte).toArray).toString
        val lines =
          (for (l <- Files.readAllLines(p).asScala) yield {
            if (l.contains(needle)) {
              l.replace(needle, s"""<ROW Property="ProductCode" Value="1033:{${uuid}} " Type="16"/>""")
            } else l
          }).mkString
        Files.write(p, lines.getBytes(Charset.forName("UTF-8")))
      } else {
        logError(s"${p.toAbsolutePath} does not exist. Aborting ...")
      }

    }
  }
}


