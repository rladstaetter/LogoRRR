package app.logorrr

import app.logorrr.conf.{LogoRRRGlobals, Settings, SettingsIO}
import app.logorrr.util.CanLog
import app.logorrr.views.main.LogoRRRStage
import javafx.stage.Stage

object LogoRRRApp {

  /** LogoRRRs own log formatting string */
  val logFormat = """[%1$tF %1$tT.%1$tN] %3$-40s %4$-13s %5$s %6$s %n"""

  def main(args: Array[String]): Unit = {
    // LogUtil.init()
    javafx.application.Application.launch(classOf[LogoRRRApp], args: _*)
  }

}


class LogoRRRApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit = {
    val settings: Settings = SettingsIO.fromFile()
    LogoRRRGlobals.set(settings, getHostServices)
    LogoRRRStage(stage).show()
  }

}