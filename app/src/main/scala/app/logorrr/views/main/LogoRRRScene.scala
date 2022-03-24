package app.logorrr.views.main

import app.logorrr.conf.StageSettings
import app.logorrr.util.JfxUtils
import javafx.beans.value.ChangeListener
import javafx.scene.Scene


object LogoRRRScene {

  /**
   * @param x x coordinate of upper left corner of scene from last execution
   * @param y y coordinate of upper left corner of scene from last execution
   */
  def mkSceneListener(x: Double, y: Double)(): ChangeListener[Scene] =
    JfxUtils.onNew[Scene](scene => {
      scene.getWindow.setX(x)
      scene.getWindow.setY(y)
    })
}

case class LogoRRRScene(mainPane: LogoRRRMain, stageSettings: StageSettings) extends Scene(mainPane, stageSettings.width, stageSettings.height)
