package app.logorrr.util

import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}

object JfxUtils extends CanLog {

  def execOnUiThread(f: => Unit): Unit = {
    if (!Platform.isFxApplicationThread) {
      Platform.runLater(() => f)
    } else {
      f
    }
  }

  def onNew[T](f: T => Unit): ChangeListener[T] = (observableValue: ObservableValue[_ <: T], t: T, t1: T) => f(t1)


}
