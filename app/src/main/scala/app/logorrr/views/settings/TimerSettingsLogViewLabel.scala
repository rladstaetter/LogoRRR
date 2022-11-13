package app.logorrr.views.settings

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.text.LineNumberLabel
import javafx.scene.control.{Label, Tooltip}
import javafx.scene.layout.HBox

case class TimerSettingsLogViewLabel(mutLogFileSettings: MutLogFileSettings
                                     , e: LogEntry
                                     , maxLength: Int) extends HBox {

  val lineNumberLabel: LineNumberLabel = LineNumberLabel(e.lineNumber, maxLength)
  lineNumberLabel.styleProperty().bind(mutLogFileSettings.fontStyle)
  val lbls =
    for ((c, i) <- e.value.zipWithIndex) yield {
      val l = new Label(c.toString)
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


  getChildren.add(lineNumberLabel)
  getChildren.addAll(lbls: _*)
  //  getChildren.addAll(labels: _*)

}
