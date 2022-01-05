package app.logorrr

import app.logorrr.conf.Settings
import app.logorrr.util.OsUtil
import javafx.application.HostServices
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.scene.control.{Menu, MenuBar, MenuItem}
import javafx.scene.layout.BorderPane
import javafx.stage.{Stage, WindowEvent}

import java.nio.file.{Path, Paths}

class LogoRRRMenuBar extends MenuBar {
  if (OsUtil.isMac) {
    setUseSystemMenuBar(OsUtil.isMac)
  } else {
    //    prefWidthProperty.bind(parent.widthProperty)
  }
  val m = new Menu("asf")
  m.getItems.add(new MenuItem("jo"))
  getMenus.add(m)
}

class LogorrrMainPane(hostServices: HostServices
                      , settings: Settings) extends BorderPane {

  val ambp = new AppMainBorderPane(settings.stageSettings.width, settings.squareImageSettings.width)

  val mB = new LogoRRRMenuBar
  if (OsUtil.isWin) {
    setTop(mB)
  } else {
    // on mac the menu bar will be shown as system menu bar
  }
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
    stage.setTitle(Settings.meta.appName + " " + Settings.meta.appVersion)
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
