package app.logorrr.build

import net.ladstatt.util.io.TinyIo
import net.ladstatt.util.log.TinyLog

import java.nio.file.{Files, Paths}
import java.util.UUID
import scala.jdk.CollectionConverters.*

object ProductCodeUpdater extends TinyIo with TinyLog:

  val needle = """<ROW Property="ProductCode" Value="1033:{aaaaaaaa-bbbb-cccc-dddd-ffffffffffff} " Type="16"/>"""

  def main(args: Array[String]): Unit =
    TinyLog.init(Paths.get("target/productcodeupdater.log"), limit = 1024 * 1024 * 100,count = 1)
    if args.length != 2 then
      logError(s"Wrong number of arguments, expected 2 but was ${args.length} ")
    else
      val installerPath = args(0)
      val p = Paths.get(installerPath)
      logInfo(s"installer path: ${p.toAbsolutePath}")
      logInfo(s"       version: ${args(1)}")
      if Files.exists(p) then
        val version = args(1)
        val installerName = p.getFileName.toString
        val uuid = UUID.nameUUIDFromBytes((installerName + version).map(_.toByte).toArray).toString
        val content =
          (for l <- Files.readAllLines(p).asScala yield {
            if l.contains(needle) then {
              l.replace(needle, s"""<ROW Property="ProductCode" Value="1033:{$uuid} " Type="16"/>""")
            } else l
          }).mkString("\r\n")
        write(p, content)
      else
        logError(s"${p.toAbsolutePath} does not exist. Aborting ...")



