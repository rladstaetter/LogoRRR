package app.logorrr.util

import app.logorrr.views.Filter
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ListChangeListener
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.{Stage, WindowEvent}

object JfxUtils extends CanLog {


  def execOnUiThread(f: => Unit): Unit = {
    if (!Platform.isFxApplicationThread) {
      Platform.runLater(() => f)
    } else {
      f
    }
  }

  /** close given stage like it would have been closed via clicking 'x' in window frame */
  def closeStage(stage: Stage): Unit = {
    stage.fireEvent(
      new WindowEvent(
        stage,
        WindowEvent.WINDOW_CLOSE_REQUEST
      )
    )
  }


  def onNew[T](f: T => Unit): ChangeListener[T] = (observableValue: ObservableValue[_ <: T], t: T, t1: T) => f(t1)


  def mkListChangeListener[T](fn: ListChangeListener.Change[_ <: T] => Unit): ListChangeListener[T] = (change: ListChangeListener.Change[_ <: T]) => fn(change)

}
