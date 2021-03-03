package net.ladstatt.util

import javafx.application.Platform

import java.util.UUID

object JfxUtils extends CanLog {

  def execOnUiThread(f: => Unit): Unit = {
    val uuid = UUID.randomUUID().toString
    if (!Platform.isFxApplicationThread) {
      Platform.runLater(() => timeR(f, uuid))
    } else {
      f
    }
  }
}
