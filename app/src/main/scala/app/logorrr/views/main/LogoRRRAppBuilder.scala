package app.logorrr.views.main

import app.logorrr.conf.{Settings, StageSettings}
import app.logorrr.util.{CanLog, JfxUtils, OsUtil}
import app.logorrr.views.menubar.{FileMenu, HelpMenu}
import javafx.application.HostServices
import javafx.beans.value.ChangeListener
import javafx.scene.Scene
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import javafx.stage.{Stage, WindowEvent}

import java.nio.file.{Path, Paths}

class LogoRRRMenuBar(hostServices: HostServices, settings: Settings) extends MenuBar {
  def init(): Unit = {
    if (OsUtil.isMac) {
      setUseSystemMenuBar(OsUtil.isMac)
    }
    getMenus.addAll(new FileMenu(settings), new HelpMenu(hostServices))
  }

  init()
}

class LogorrrMainPane(hostServices: HostServices
                      , settings: Settings) extends BorderPane {

  val width = settings.stageSettings.width
  val height = settings.stageSettings.height

  val ambp = new AppMainBorderPane(settings.stageSettings.width, settings.squareImageSettings.width)

  val mB = new LogoRRRMenuBar(hostServices, settings)

  setTop(mB)
  setCenter(ambp)

  def setSceneWidth(sceneWidth: Int): Unit = ambp.setSceneWidth(sceneWidth)

  def addLogFile(p: Path): Unit = ambp.addLogFile(p)

  def selectLastLogFile(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}


object LogoRRRAppBuilder extends CanLog {

  /**
   * @param x x coordinate of upper left corner of scene from last execution
   * @param y y coordinate of upper left corner of scene from last execution
   */
  def mkSceneListener(x: Double, y: Double)(): ChangeListener[Scene] =
    JfxUtils.onNew[Scene](scene => {
      scene.getWindow.setX(x)
      scene.getWindow.setY(y)
      StageSettings.addWindowListeners(scene.getWindow)
    })

  def withStage(stage: Stage
                , params: Seq[String]
                , settings: Settings
                , hs: HostServices): Stage = {
    stage.setTitle(Settings.fullAppName)
    stage.getIcons.add(Settings.icon)
    val abbp = new LogorrrMainPane(hs, settings)
    val scene = new Scene(abbp, abbp.width, abbp.height)

    val sceneListener = mkSceneListener(settings.stageSettings.x, settings.stageSettings.y)()
    val abbWidthListener = JfxUtils.onNew[Number](width => abbp.setSceneWidth(width.intValue))

    stage.sceneProperty().addListener(sceneListener)
    scene.widthProperty().addListener(abbWidthListener)

    stage.setScene(scene)

    for (p <- params) {
      abbp.addLogFile(Paths.get(p).toAbsolutePath)
    }
    abbp.selectLastLogFile()

    // make sure to cleanup on close
    stage.setOnCloseRequest((_: WindowEvent) => {
      abbp.shutdown()
      StageSettings.removeWindowListeners(scene.getWindow)
      scene.widthProperty().removeListener(abbWidthListener)
      stage.sceneProperty.removeListener(sceneListener)
    })
    stage
  }
}
