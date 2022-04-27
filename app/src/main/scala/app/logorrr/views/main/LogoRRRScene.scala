package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.util.JfxUtils
import javafx.beans.value.ChangeListener
import javafx.scene.Scene


object LogoRRRScene {

  /** after scene got initialized and scene was set to stage immediately set position of stage */
  val sceneListener = LogoRRRScene.mkSceneListener()

  /**
   * @param x x coordinate of upper left corner of scene from last execution
   * @param y y coordinate of upper left corner of scene from last execution
   */
  def mkSceneListener(): ChangeListener[Scene] =
    JfxUtils.onNew[Scene](scene => {
      val (x, y) = (LogoRRRGlobals.getStageX(), LogoRRRGlobals.getStageY())
      scene.getWindow.setX(x)
      scene.getWindow.setY(y)
    })
}

