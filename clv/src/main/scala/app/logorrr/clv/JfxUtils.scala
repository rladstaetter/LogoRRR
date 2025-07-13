package app.logorrr.clv

import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.ListView

object JfxUtils {

  def onNew[T](f: T => Unit): ChangeListener[T] = (_: ObservableValue[_ <: T], _: T, t1: T) => f(t1)

  def mkInvalidationListener(invalidated: Observable => Unit): InvalidationListener = (observable: Observable) => invalidated(observable)

  def scrollTo[T](lv: ListView[T], cellHeight: Int, relativeIndex: Int): Unit = {
    val visibleItemCount = (lv.getHeight / cellHeight).asInstanceOf[Int] / 2
    lv.scrollTo(relativeIndex - visibleItemCount)
  }

  def execOnUiThread(f: => Unit): Unit = {
    if (Platform.isFxApplicationThread) {
      f
    } else {
      Platform.runLater(() => f)
    }
  }
}
