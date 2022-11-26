package app.logorrr.views.settings.timer

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.LabelUtil
import app.logorrr.views.text.LineNumberLabel
import javafx.beans.property.ObjectProperty
import javafx.scene.control.{Label, Tooltip}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{Background, BackgroundFill, HBox}
import javafx.scene.paint.Color

object TimerSettingsLogViewLabel {

  def calcStyle(
                 startColProperty: ObjectProperty[java.lang.Integer]
                 , endColProperty: ObjectProperty[java.lang.Integer]
               ): Option[String] = {
    (Option(startColProperty.get()), Option(endColProperty.get)) match {
      case (Some(start), Some(end)) =>
        Some("")
      case _ => None
    }
  }
}

case class TimerSettingsLogViewLabel(settings: MutLogFileSettings
                                     , e: LogEntry
                                     , maxLength: Int
                                     , startColProperty: ObjectProperty[java.lang.Integer]
                                     , endColProperty: ObjectProperty[java.lang.Integer]) extends HBox {

  val lineNumberLabel: LineNumberLabel = LineNumberLabel(e.lineNumber, maxLength)
  lineNumberLabel.styleProperty().bind(settings.fontStyleBinding)
  val lbls =
    for ((c, i) <- e.value.zipWithIndex) yield {
      val l = new Label(c.toString)
      l.setUserData(i) // save position of label for later
      l.setOnMouseDragOver(e => println("mouse drag over"))
      l.setOnMouseDragEntered(e => println("mouse drag entered"))
      l.setOnMouseDragExited(e => println("mouse drag exited"))
      l.setOnMouseDragReleased(e => println("mouse drag released"))
      l.setOnMouseDragged(e => println("mouse dragged"))
      l.setOnMouseClicked(reactToMouseClick(i) _)
      l.setTooltip(new Tooltip("column: " + i.toString))
      l.setOnMouseEntered(e => {
        l.setStyle(
          """-fx-border-color: RED;
            |-fx-border-width: 0 0 0 1px;
            |""".stripMargin)
      })
      l.setOnMouseExited(e => {
        l.setStyle("")
      })
      l
    }


  private def reactToMouseClick(i: Int)(e: MouseEvent): Unit = {
    (Option(startColProperty.get), Option(endColProperty.get)) match {
      case (None, None) =>
        startColProperty.set(i)
        lbls.foreach(l => LabelUtil.resetStyle(l))
      case (Some(startCol), None) =>
        endColProperty.set(i)
        for (l <- lbls) {
          val labelIndex = l.getUserData.asInstanceOf[Int]
          if (startCol <= labelIndex && labelIndex < i) {
            l.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)))
          }
        }
      case _ =>
        lbls.foreach(l => LabelUtil.resetStyle(l))
        startColProperty.set(null)
        endColProperty.set(null)
    }
  }

  getChildren.add(lineNumberLabel)
  getChildren.addAll(lbls: _*)
  //  getChildren.addAll(labels: _*)

}
