package app.logorrr.views.block

import app.logorrr.LogoRRRApp
import app.logorrr.io.FileManager
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.application.Application
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleListProperty}
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

import java.nio.file.Path
import java.util

/**
 * App to test block list view
 */
object ChunkListTestApp {

  def main(args: Array[String]): Unit = {
    System.setProperty("user.language", "en")
    System.setProperty("java.util.logging.SimpleFormatter.format", CanLog.LogFormat)
    javafx.application.Application.launch(classOf[ChunkListTestApp], args: _*)
  }

}

class ChunkListTestApp extends Application with CanLog {

  private def mkEntries(path: Path): java.util.List[LogEntry] = {
    util.Arrays.asList((for ((l, i) <- FileManager.fromPathUsingSecurityBookmarks(path).zipWithIndex) yield LogEntry(i, l, None)): _*)
  }

  def start(stage: Stage): Unit = {

    val width = 1000
    val height = 1000
    val blockSize = 10
    val dividerPosition = 0.5
    val selectedLineNumber = 0

    val entries: java.util.List[LogEntry] = ChunkSpec.mkTestLogEntries(1000)
    //val entries: java.util.List[LogEntry] = mkEntries(Paths.get("/Users/lad/logfiles/biglog.txt"))
    val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList(LogFileSettings.DefaultFilter: _*))
    val entriesProperty = new SimpleListProperty[LogEntry](FXCollections.observableArrayList(entries))

    val bp = new BorderPane()

    val clv = new ChunkListView(entriesProperty
      , new SimpleIntegerProperty(selectedLineNumber)
      , new SimpleIntegerProperty(blockSize)
      , filtersProperty
      , new SimpleDoubleProperty(dividerPosition)
      , _ => ())
    clv.init()
    val sp = new SplitPane(clv, new BorderPane(new Label("Test")))

    val slider = new Slider(2, 100, 10)
    slider.setOrientation(Orientation.HORIZONTAL)
    slider.setPrefWidth(300)

    slider.valueProperty().addListener(JfxUtils.onNew[Number](n => {
      val blockSize = n.intValue()
      clv.blockSizeProperty.set(blockSize)
    }))

    val nrBlocksLabel = new Label(s"# blocks")

    val nrElemsChoiceBox = new ChoiceBox[Int]()
    val elems = FXCollections.observableArrayList(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000)
    nrElemsChoiceBox.setItems(elems)
    nrElemsChoiceBox.getSelectionModel.selectedIndexProperty().addListener(JfxUtils.onNew[Number](n => {
      val nrElems = elems.get(n.intValue())
      clv.logEntries.setAll(ChunkSpec.mkTestLogEntries(nrElems))
    }))

    bp.setTop(new ToolBar(nrBlocksLabel, nrElemsChoiceBox, slider))
    bp.setCenter(sp)

    val refreshListener = JfxUtils.onNew[Number](n => {
      if (n.doubleValue() > 0.1) {
        clv.recalculateAndUpdateItems("testapp")
      }
    })

    sp.getDividers.get(0).positionProperty().addListener(refreshListener)

    val scene = new Scene(bp, width, height)
    stage.setScene(scene)

    stage.showingProperty().addListener((_, _, isNowShowing) => {
      if (isNowShowing) {
        clv.recalculateAndUpdateItems("testapp")
        logTrace("Scene is loaded and displayed!")
      }
    })

    stage.show()

  }

}
