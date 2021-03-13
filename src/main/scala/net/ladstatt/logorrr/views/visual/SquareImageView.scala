package net.ladstatt.logorrr.views.visual

import javafx.geometry.Bounds
import javafx.scene.image.{ImageView, PixelWriter, WritableImage}
import javafx.scene.paint.Color
import net.ladstatt.logorrr.LogEntry

import scala.collection.mutable

object SquareImageView {

  def mkBareImage(entries: mutable.Buffer[LogEntry], squareWidth: Int, canvasWidth: Int): WritableImage = {
    val numberCols = canvasWidth / squareWidth
    val numRows = entries.size / numberCols
    val height = squareWidth * numRows
    new WritableImage(canvasWidth + squareWidth, height + squareWidth)
  }

  def paint(entries: mutable.Buffer[LogEntry]
            , squareWidth: Int
            , canvasWidth: Int
            , bounds: Bounds): WritableImage = {
    val wi = SquareImageView.mkBareImage(entries, squareWidth, canvasWidth)

    val numberCols = canvasWidth / squareWidth
    val pw = wi.getPixelWriter
    for ((e, i) <- entries.zipWithIndex) {
      val u = (i % numberCols) * squareWidth
      val v = (i / numberCols) * squareWidth
      //if ((bounds.getMinY + v) >= 0 && (bounds.getMinY + v + squareWidth - 1) < bounds.getMaxY) {
        paintSquare(pw
          , u
          , v
          , squareWidth
          , e.severity.color)
      //}
    }
    wi
  }

  def paintSquare(pw: PixelWriter, u: Int, v: Int, length: Int, c: Color): PixelWriter = {
    for {x <- u until (u + length - 1)
         y <- v until (v + length - 1)} {
      pw.setColor(x, y, c)
    }
    pw
  }


  def apply(entries: mutable.Buffer[LogEntry]
            , canvasWidth: Int
            , squareWidth: Int): SquareImageView = {
    val i = mkBareImage(entries, canvasWidth, squareWidth)
    val siv = new SquareImageView
    siv.setImage(i)
    siv
  }
}

class SquareImageView extends ImageView {


}