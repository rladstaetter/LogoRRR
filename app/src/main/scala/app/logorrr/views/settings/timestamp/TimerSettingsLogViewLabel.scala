package app.logorrr.views.settings.timestamp

import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.LabelUtil
import app.logorrr.views.text.LineNumberLabel
import javafx.beans.property.ObjectProperty
import javafx.scene.control.ListView
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

object TimerSettingsLogViewLabel {

}


// TODO use RichTextArea from jfx.incubator
class TimerSettingsLogViewLabel(listView: ListView[LogEntry]
                                , settings: MutLogFileSettings
                                , e: LogEntry
                                , maxLength: Int
                                , startColProperty: ObjectProperty[java.lang.Integer]
                                , endColProperty: ObjectProperty[java.lang.Integer])
  extends HBox:

  val lineNumberLabel: LineNumberLabel = LineNumberLabel(e.lineNumber, maxLength)
  lineNumberLabel.styleProperty().bind(settings.fontStyleBinding)

  val chars: IndexedSeq[LogViewLabel] =
    for (c, i) <- e.value.zipWithIndex yield new LogViewLabel(listView, settings.getFileId, e.lineNumber, i, c.toString, applyStyleAtPos)

  (Option(startColProperty.get()), Option(endColProperty.get())) match
    case (Some(startCol), Some(endCol)) => paint(startCol, endCol)
    case _ =>

  private def applyStyleAtPos(pos: Int): Unit =
    (Option(startColProperty.get), Option(endColProperty.get)) match
      // if none is set, start with startcol
      case (None, None) =>
        startColProperty.set(pos)
        chars.foreach(LabelUtil.resetStyle)
      // if both are set, start with startcol again
      case (Some(_), Some(_)) =>
        startColProperty.set(pos)
        endColProperty.set(null)
        chars.foreach(LabelUtil.resetStyle)
      // startcol is set already, set endcol
      case (Some(startCol), None) =>
        // if startCol is greater or equal to pos, intpret it as new startcol
        if startCol >= pos then
          startColProperty.set(pos)
          endColProperty.set(null)
          chars.foreach(LabelUtil.resetStyle)
        else
          endColProperty.set(pos)
          paint(startCol, pos)
      case _ =>
        chars.foreach(LabelUtil.resetStyle)
        startColProperty.set(null)
        endColProperty.set(null)

  private def paint(startCol: Integer, endCol: Int): Unit =
    for l <- chars do
      val labelIndex = l.getUserData.asInstanceOf[Int]
      if startCol <= labelIndex && labelIndex < endCol then
        l.setBackground(ColorUtil.mkBg(Color.YELLOWGREEN))

  getChildren.add(lineNumberLabel)
  getChildren.addAll(chars *)

