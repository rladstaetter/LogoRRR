package app.logorrr.build

import net.ladstatt.app.{AppId, AppMeta}
import net.ladstatt.util.io.Fs
import net.ladstatt.util.log.CanLog

import java.nio.file.{Files, Paths}
import java.util.UUID
import scala.jdk.CollectionConverters._

object ProductCodeUpdater extends CanLog with Fs:

  val appMeta: AppMeta = net.ladstatt.app.AppMeta(AppId("ProductCodeUpdater", "productcodeupdater", "productcodeupdater.app"), AppMeta.LogFormat)

  val needle = """<ROW Property="ProductCode" Value="1033:{aaaaaaaa-bbbb-cccc-dddd-ffffffffffff} " Type="16"/>"""

  def main(args: Array[String]): Unit =
    net.ladstatt.app.AppMeta.initApp(appMeta)
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



