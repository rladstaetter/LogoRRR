package net.ladstatt.logboard

import javafx.scene.image.{Image, PixelWriter, WritableImage}
import javafx.scene.paint.Color

import java.util
import scala.jdk.CollectionConverters._


object Painter {


  def paintSquare(pw: PixelWriter, u: Int, v: Int, length: Int, c: Color): PixelWriter = {
    for {x <- u until (u + length - 1)
         y <- v until (v + length - 1)} {
      pw.setColor(x, y, c)
    }
    pw
  }

  def paint(entries: util.List[LogEntry], squareWidth: Int, canvasWidth: Int): WritableImage = {
    val numberCols = canvasWidth / squareWidth
    val numRows = entries.size() / numberCols
    val height = squareWidth * numRows
    val wi = new WritableImage((canvasWidth + squareWidth).toInt, (height + squareWidth).toInt)
    val pw = wi.getPixelWriter

    for ((e, i) <- entries.asScala.zipWithIndex) {
      paintSquare(pw, ((i % numberCols) * squareWidth).toInt, ((i / numberCols) * squareWidth).toInt, squareWidth.toInt, e.severity.color)
    }
    wi
  }

}
