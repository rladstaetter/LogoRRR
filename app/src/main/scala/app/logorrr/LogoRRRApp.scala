package app.logorrr

import app.logorrr.conf.{LogoRRRGlobals, Settings, SettingsIO}
import app.logorrr.meta.AppMeta
import app.logorrr.util.CanLog
import app.logorrr.views.main.LogoRRRStage
import javafx.application.Application
import javafx.stage.Stage

import java.nio.file.Paths

object LogoRRRApp extends CanLog {

  /** LogoRRRs own log formatting string */
  val logFormat = """[%1$tF %1$tT.%1$tN] %3$-40s %4$-13s %5$s %6$s %n"""

  def main(args: Array[String]): Unit = {
    System.setProperty("user.language", "en")
    System.setProperty("java.util.logging.SimpleFormatter.format", logFormat)
    logInfo(s"Started ${AppMeta.fullAppNameWithVersion} in ${Paths.get("").toAbsolutePath.toString}")
    LogoRRRNative.loadNativeLibraries()
    javafx.application.Application.launch(classOf[LogoRRRApp], args: _*)
  }

}

class LogoRRRApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit = {
    // make sure to set css before anything is initialized otherwise the rules won't apply
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")
    val settings: Settings = SettingsIO.fromFile()
    LogoRRRGlobals.set(settings, getHostServices)
    LogoRRRStage(stage).show()
  }

}