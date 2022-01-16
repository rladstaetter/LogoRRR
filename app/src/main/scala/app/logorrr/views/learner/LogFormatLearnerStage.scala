package app.logorrr.views.learner

import app.logorrr.model.LogEntry
import app.logorrr.util.{JfxEventHandler, LogoRRRFonts}
import app.logorrr.views.{LogColumnDef, SimpleRange}
import javafx.beans.property.{SimpleIntegerProperty, SimpleMapProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.stage.{Modality, Stage}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 * Modal dialog to define positions of date/time in log entries
 *
 * @param e
 */
case class LogFormatLearnerStage(e: LogEntry) extends Stage {

  initModality(Modality.APPLICATION_MODAL)
  setTitle("Learn Logformat")

  val width = 1500
  val height = 150

  val lowerBoundProperty = new SimpleIntegerProperty(0)
  val upperBoundProperty = new SimpleIntegerProperty(0)

  /** stores where LogoRRRR searches for certain attributes like date/time for a given log entry */
  val logColumnDefinitionProperty =
    new SimpleMapProperty[String, SimpleRange](FXCollections.observableMap(new mutable.HashMap[String, SimpleRange]().asJava))

  val logColumnDefProperty = new SimpleObjectProperty[LogColumnDef]()

  def setLogColumnDef(value: LogColumnDef): Unit = logColumnDefProperty.set(value)

  def getLogColumnDef(): LogColumnDef = logColumnDefProperty.get()

  logColumnDefinitionProperty.addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      val map = observable.asInstanceOf[SimpleMapProperty[String, SimpleRange]]
      val difference = LogColumnDef.entries.toSet.diff(map.keySet().asScala)
      if (difference.isEmpty) {
        println("Updating column definiton ...")
        setLogColumnDef(LogColumnDef(map.asScala))
      } else {
        println("Still missing: " + difference.toSeq.sorted.mkString(","))
      }
    }
  })

  def mk60TextField(): TextField = {
    val tf = new TextField()
    tf.setPrefWidth(40)
    tf.setMaxWidth(40)
    tf
  }

  def combiner(tb: ToolBar, name: String): ToolBar = {
    val l = new Button(name)
    val lB = mk60TextField()
    val uB = mk60TextField()

    l.setOnAction(_ => {
      lB.setText(lowerBoundProperty.get().toString)
      uB.setText(upperBoundProperty.get().toString)
      logColumnDefinitionProperty.put(name, SimpleRange(lowerBoundProperty.get(), upperBoundProperty.get()))
    })

    tb.getItems.addAll(l, lB, uB, new Separator(Orientation.HORIZONTAL))
    tb
  }

  val bp = new BorderPane()
  val ta = new TextField()
  ta.setText(e.value)
  ta.setEditable(false)
  val tb = new ToolBar()
  val ftb = LogColumnDef.entries.foldLeft(tb)((acc, e) => combiner(acc, e))
  bp.setBottom(ftb)
  ta.selectionProperty().addListener(new ChangeListener[IndexRange] {
    override def changed(observableValue: ObservableValue[_ <: IndexRange], oldVal: IndexRange, newVal: IndexRange): Unit = {
      val selectedText = ta.getText(newVal.getStart, newVal.getEnd)
      // only track selections, not 'deselections'
      if (newVal.getStart() != 0 && newVal.getEnd() != 0) {
        lowerBoundProperty.set(newVal.getStart)
        upperBoundProperty.set(newVal.getEnd)
      }
    }
  })
  ta.setStyle(LogoRRRFonts.jetBrainsMono(30))
  bp.setCenter(ta)


  val scene = new Scene(bp, width, height)
  setScene(scene)
  setOnCloseRequest(JfxEventHandler(event => {
    event.consume()
    println("closed")
    close()
  }))

}
