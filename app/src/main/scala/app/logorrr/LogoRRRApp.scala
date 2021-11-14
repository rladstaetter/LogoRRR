package app.logorrr

import javafx.stage.Stage

import scala.jdk.CollectionConverters._

object LogoRRRApp {

  /** application name */
  val ApplicationName = "LogoRRR"

  /** version which is displayed to user */
  val ApplicationVersion = "21.3.2"

  /** initial width of main application scene */
  val InitialSceneWidth = 1000

  /** initial height of main application scene */
  val InitialSceneHeight = 600

  /** width of squares which are painted for each log entry */
  val InitialSquareWidth = 7

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[LogoRRRApp], args: _*)
  }

}

/*
class LogoRRRAppMenuBar extends MenuBar {
  //setUseSystemMenuBar(true)
  private val systemMenu = new Menu("EXTREME")
  val aboutMenuItem = new MenuItem("JOJOJO " + LogoRRRApp.ApplicationName)
  val quitMenuItem = new MenuItem("Quit" + LogoRRRApp.ApplicationName)
  systemMenu.getItems.addAll(aboutMenuItem, quitMenuItem)
  getMenus.add(systemMenu)
}
*/


class LogoRRRApp extends javafx.application.Application {

  /**
   * will be called by the java bootstrapper
   */
  def start(stage: Stage): Unit = {
    val params: Seq[String] = getParameters.getRaw.asScala.toSeq
    LogoRRRAppBuilder.withStage(stage, params, LogoRRRApp.InitialSceneWidth, LogoRRRApp.InitialSceneHeight).show()
  }

}