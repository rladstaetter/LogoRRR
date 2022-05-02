package app.logorrr

import app.logorrr.conf.{LogoRRRGlobals, Settings, SettingsIO}
import app.logorrr.util.CanLog
import app.logorrr.views.main.LogoRRRStage
import javafx.stage.Stage

object LogoRRRApp {

  def main(args: Array[String]): Unit = {
    // LogUtil.init()
    javafx.application.Application.launch(classOf[LogoRRRApp], args: _*)
  }

}


class LogoRRRApp extends javafx.application.Application with CanLog {

  /**
   * will be called by the java bootstrapper
   */
  def start(stage: Stage): Unit = {
    val settings: Settings = SettingsIO.fromFile()
    LogoRRRGlobals.set(settings, getHostServices)
    LogoRRRStage(stage).show()
  }

}