package app.logorrr.views.visual

import app.logorrr.Filter
import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import javafx.scene.image._
import javafx.scene.paint.Color

import scala.collection.mutable
import scala.util.{Failure, Success, Try}


object SquareImageView extends CanLog {

  def mkBareImage(totalSize: Int, squareWidth: Int, canvasWidth: Int): WritableImage = {
    val numberCols = canvasWidth / squareWidth
    val numRows = totalSize / numberCols
    val height = squareWidth * numRows
    new WritableImage(canvasWidth + squareWidth, height + squareWidth)
  }

  def paint(entries: mutable.Buffer[LogEntry]
            , squareWidth: Int
            , canvasWidth: Int
            , filter: Seq[Filter]): WritableImage = {
    val wi = SquareImageView.mkBareImage(entries.size, squareWidth, canvasWidth)
    val numberCols = canvasWidth / squareWidth
    val pw = wi.getPixelWriter
    // make sure that when a file gets deleted we don't blow up in the 'entries.zipWithIndex' for loop
    Try(entries.zipWithIndex) match {
      case Success(es) =>
        for ((e, i) <- es) {
          val x = (i % numberCols) * squareWidth
          val y = (i / numberCols) * squareWidth
          val color = e.calcColor(filter)
          paintSquare(pw
            , x
            , y
            , squareWidth
            , ColorUtil.mkPixelArray(squareWidth - 1, color)
          )
        }
      case Failure(exception) => logException(exception)
    }

    wi
  }

  /**
   * paints a square with given src int's
   *
   * copy arrays around is faster than painting every pixel individually
   * */
  def paintSquare(pw: PixelWriter, u: Int, v: Int, length: Int, src: Array[Int]): PixelWriter = {
    // length - 1 is just here to separate individual rectangles (gives nice effect)
    // double check length with Filter.pixelarray, they have to be of the same value
    pw.setPixels(u, v, length - 1, length - 1, PixelFormat.getIntArgbPreInstance, src, 0, 0)
    pw
  }

  /** paints a rectangle around a square */
  def paintRect(pw: PixelWriter, x: Int, y: Int, length: Int, c: Color): PixelWriter = {
    for (i <- x + 1 until x + length - 1) {
      pw.setColor(i, y, c)
      pw.setColor(i, y + length - 2, c)
    }
    for (i <- y + 1 until y + length - 1) {
      pw.setColor(x, i, c)
      pw.setColor(x + length - 2, i, c)
    }
    pw
  }

  def apply(totalSize: Int
            , canvasWidth: Int
            , squareWidth: Int): SquareImageView = {
    val i = mkBareImage(totalSize, canvasWidth, squareWidth)
    val siv = new SquareImageView
    siv.setImage(i)
    siv
  }
}

class SquareImageView extends ImageView