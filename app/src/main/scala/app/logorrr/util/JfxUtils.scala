package app.logorrr.util

import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ListChangeListener
import javafx.scene.control.ListView
import javafx.scene.paint.Color
import javafx.stage.{Stage, WindowEvent}
import net.ladstatt.util.log.CanLog

import scala.util.Random

object JfxUtils extends CanLog {

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

  /** close given stage like it would have been closed via clicking 'x' in window frame */
  def closeStage(stage: Stage): Unit = {
    stage.fireEvent(
      new WindowEvent(
        stage,
        WindowEvent.WINDOW_CLOSE_REQUEST
      )
    )
  }

  def mkInvalidationListener(invalidated: Observable => Unit): InvalidationListener = (observable: Observable) => invalidated(observable)

  def onNew[T](f: T => Unit): ChangeListener[T] = (_: ObservableValue[_ <: T], _: T, t1: T) => f(t1)

  def mkListChangeListener[T](fn: ListChangeListener.Change[_ <: T] => Unit): ListChangeListener[T] = (change: ListChangeListener.Change[_ <: T]) => fn(change)

  def randColor: Color = Color.color(Random.nextDouble()
    , Random.nextDouble()
    , Random.nextDouble()
  )
}
