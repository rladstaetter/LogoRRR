package app.logorrr

import app.logorrr.conf.Settings
import app.logorrr.util.{CanLog, LogUtil}
import app.logorrr.views.main.LogoRRRAppBuilder
import javafx.stage.Stage

import scala.jdk.CollectionConverters._

object LogoRRRApp  {

  def main(args: Array[String]): Unit = {
    //LogUtil.init()
    javafx.application.Application.launch(classOf[LogoRRRApp], args: _*)
  }

}



class LogoRRRApp extends javafx.application.Application with CanLog {

  /**
   * will be called by the java bootstrapper
   */
  def start(stage: Stage): Unit = {
    val params: Seq[String] = getParameters.getRaw.asScala.toSeq
    Settings.someSettings.foreach(settings => LogoRRRAppBuilder.withStage(stage, params, settings, getHostServices).show())
  }

}