package net.ladstatt.logboard

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control._
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{Background, BackgroundFill, BorderPane, CornerRadii}


object ReportTab {

  def apply(logReport: LogReport): ReportTab = {
    val rt = new ReportTab
    rt.setUserData(logReport)
    rt.setText(logReport.name + " (" + logReport.entries.size() + " entries)")
    rt
  }

}

class ReportTab extends Tab {
  /**
   * initially, report tab will be painted when added. per default the newly added report will be painted once.
   * repaint property will be set to true if the report tab itself is not selected and the width of the application
   * changes. the tab will be repainted when selected.
   * */
  val repaintProperty = new SimpleBooleanProperty(false)

  def setRepaint(repaint: Boolean): Unit = repaintProperty.set(repaint)

  def getRepaint(): Boolean = repaintProperty.get()

  def paint(squareWidth: Int, canvasWidth: Int): Unit = {
    val logReport = getUserData.asInstanceOf[LogReport]
    val image = logReport.paint(squareWidth, canvasWidth)

    val bp = new BorderPane()
    val bar = new ToolBar()

    LogSeverity.seq.stream.forEach(ls => {
      bar.getItems.add(new Button(ls.name + ": " + logReport.occurences.get(ls) + " " + logReport.percentAsString(ls) + "%"))
    })
    val label = new Label("")
    bp.setTop(bar)
    val view = new ImageView(image)
    view.setOnMouseMoved(new EventHandler[MouseEvent]() {
      override def handle(me: MouseEvent): Unit = {
        val entry = logReport.getEntryAt(me.getX.toInt, me.getY.toInt, squareWidth, canvasWidth)
        label.setText(entry.value)
        label.setPrefWidth(canvasWidth)
        label.setBackground(new Background(new BackgroundFill(entry.severity.color, new CornerRadii(5.0), new Insets(-5.0))))
        label.setStyle(s"-fx-text-fill: white; -fx-font-size: 20px;")
      }
    })
    bp.setBottom(label)
    bp.setCenter(new ScrollPane(view))
    setContent(bp)
  }
}