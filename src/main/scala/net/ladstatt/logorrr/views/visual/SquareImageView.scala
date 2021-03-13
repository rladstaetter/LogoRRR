package net.ladstatt.logorrr.views.visual

import javafx.scene.image._
import javafx.scene.paint.Color
import net.ladstatt.logorrr.{ColorUtil, LogEntry, LogSeverity, LogoRRRApp}

import scala.collection.mutable


object SquareImageView {

  val pixelBuffers: Map[Color, Array[Int]] =
    LogSeverity.seq.map {
      s => s.color -> ColorUtil.mkPixelArray(LogoRRRApp.InitialSquareWidth - 1, s.color)
    }.toMap

  def mkBareImage(entries: mutable.Buffer[LogEntry], squareWidth: Int, canvasWidth: Int): WritableImage = {
    val numberCols = canvasWidth / squareWidth
    val numRows = entries.size / numberCols
    val height = squareWidth * numRows
    new WritableImage(canvasWidth + squareWidth, height + squareWidth)
  }

  def paint(entries: mutable.Buffer[LogEntry]
            , squareWidth: Int
            , canvasWidth: Int): WritableImage = {
    val wi = SquareImageView.mkBareImage(entries, squareWidth, canvasWidth)

    val numberCols = canvasWidth / squareWidth
    val pw = wi.getPixelWriter
    for ((e, i) <- entries.zipWithIndex) {
      val u = (i % numberCols) * squareWidth
      val v = (i / numberCols) * squareWidth
      paintSquare(pw
        , u
        , v
        , squareWidth
        , pixelBuffers(e.severity.color))
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
    pw.setPixels(u, v, length - 1, length - 1, PixelFormat.getIntArgbPreInstance, src, 0, 0)
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

class SquareImageView extends ImageView