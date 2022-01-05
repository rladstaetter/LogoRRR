package app.logorrr.views.main

import app.logorrr.conf.Settings
import app.logorrr.util.OsUtil
import app.logorrr.views.menubar.{FileMenu, HelpMenu}
import javafx.application.HostServices
import javafx.beans.value.{ChangeListener, ObservableValue}
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

  val ambp = new AppMainBorderPane(settings.stageSettings.width, settings.squareImageSettings.width)

  val mB = new LogoRRRMenuBar(hostServices, settings)

  setTop(mB)
  setCenter(ambp)

  def setSceneWidth(sceneWidth: Int): Unit = ambp.setSceneWidth(sceneWidth)

  def addLogFile(p: Path): Unit = ambp.addLogFile(p)

  def selectLastLogFile(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}

object LogoRRRAppBuilder {

  def withStage(stage: Stage
                , params: Seq[String]
                , settings: Settings
                , hs: HostServices): Stage = {
    stage.setTitle(Settings.fullAppName)
    stage.getIcons.add(Settings.icon)
    val abbp = new LogorrrMainPane(hs, settings)
    val scene = new Scene(abbp, settings.stageSettings.width, settings.stageSettings.height)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        Option(abbp).foreach(_.setSceneWidth(t1.intValue))
      }
    })
    stage.setScene(scene)

    for (p <- params) {
      abbp.addLogFile(Paths.get(p).toAbsolutePath)
    }
    abbp.selectLastLogFile()

    // make sure to cleanup on close
    stage.setOnCloseRequest((_: WindowEvent) => abbp.shutdown())
    stage
  }
}
