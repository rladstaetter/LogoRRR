package app.logorrr.build

import net.ladstatt.util.io.Fs
import net.ladstatt.util.log.CanLog

import java.nio.file.{Files, Paths}
import java.util.UUID
import scala.jdk.CollectionConverters._

object ProductCodeUpdater extends CanLog with Fs {

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
        val content =
          (for (l <- Files.readAllLines(p).asScala) yield {
            if (l.contains(needle)) {
              l.replace(needle, s"""<ROW Property="ProductCode" Value="1033:{$uuid} " Type="16"/>""")
            } else l
          }).mkString("\r\n")
        write(p, content)
      } else {
        logError(s"${p.toAbsolutePath} does not exist. Aborting ...")
      }

    }
  }
}


