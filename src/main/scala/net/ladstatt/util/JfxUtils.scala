package net.ladstatt.util

import javafx.application.Platform

object JfxUtils extends CanLog {

  def execOnUiThread(f: => Unit): Unit = {
    if (!Platform.isFxApplicationThread) {
      Platform.runLater(() => f)
    } else {
      f
    }
  }
}
