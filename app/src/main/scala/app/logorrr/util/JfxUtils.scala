package app.logorrr.util

import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ListChangeListener
import javafx.geometry.Rectangle2D
import javafx.scene.control.ListView
import javafx.scene.layout.{HBox, Priority, Region}
import javafx.scene.paint.Color
import javafx.stage.{Screen, Stage, WindowEvent}
import net.ladstatt.util.log.TinyLog

import scala.util.Random

object JfxUtils extends TinyLog:

  def calcDefaultScreenPosition(): Rectangle2D =
    val ps: Rectangle2D = Screen.getPrimary.getVisualBounds
    val originalX = ps.getMinX
    val originalY = ps.getMinY
    val originalWidth = ps.getWidth
    val originalHeight = ps.getHeight
    // Calculate the dimensions of the new rectangle (80% of original)
    val newWidth = originalWidth * 0.8
    val newHeight = originalHeight * 0.8
    // Calculate the new coordinates
    val newX = originalX + (originalWidth - newWidth) / 2
    val newY = originalY + (originalHeight - newHeight) / 2
    new Rectangle2D(newX, newY, newWidth, newHeight)


  def mkHgrowFiller(): Region =
    val filler = new Region()
    HBox.setHgrow(filler, Priority.ALWAYS)
    filler

  def scrollTo[T](lv: ListView[T], cellHeight: Int, relativeIndex: Int): Unit =
    val visibleItemCount = (lv.getHeight / cellHeight).asInstanceOf[Int] / 2
    lv.scrollTo(relativeIndex - visibleItemCount)

  def execOnUiThread(f: => Unit): Unit =
    if Platform.isFxApplicationThread then
      f
    else
      Platform.runLater(() => f)

  /** close given stage like it would have been closed via clicking 'x' in window frame */
  def closeStage(stage: Stage): Unit =
    stage.fireEvent(
      new WindowEvent(
        stage,
        WindowEvent.WINDOW_CLOSE_REQUEST
      )
    )

  def mkInvalidationListener(invalidated: Observable => Unit): InvalidationListener = (observable: Observable) => invalidated(observable)

  def onNew[T](f: T => Unit): ChangeListener[T] = (_: ObservableValue[? <: T], _: T, t1: T) => f(t1)

  def mkListChangeListener[T](fn: ListChangeListener.Change[? <: T] => Unit): ListChangeListener[T] = (change: ListChangeListener.Change[? <: T]) => fn(change)

  def randColor: Color = Color.color(Random.nextDouble()
    , Random.nextDouble()
    , Random.nextDouble()
  )
