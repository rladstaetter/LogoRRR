package app.logorrr.views.block

import app.logorrr.LogoRRRApp
import app.logorrr.model.{LogEntry, LogFileReader}
import app.logorrr.util.JfxUtils
import javafx.application.Application
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty}
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage

import java.nio.file.{Path, Paths}
import java.{lang, util}
import java.util.Collections
import scala.jdk.CollectionConverters.IterableHasAsJava

/**
 * App to test block list view
 */
object BlockListViewTestApp {

  def main(args: Array[String]): Unit = {
    System.setProperty("user.language", "en")
    System.setProperty("java.util.logging.SimpleFormatter.format", LogoRRRApp.logFormat)
    javafx.application.Application.launch(classOf[BlockListApp], args: _*)
  }

}

class BlockListApp extends Application {

  // return test log entries
  private def mkTestEntries(): java.util.List[LogEntry] = {
    val entries: util.List[LogEntry] = Chunk.mkTestLogEntries(1000 * 1000 * 100)
    entries
  }

  private def mkEntries(path: Path): java.util.List[LogEntry] = {
    util.Arrays.asList((for ((l, i) <- LogFileReader.readFromFile(path).zipWithIndex) yield LogEntry(i, l, None)) : _*)
  }

  def start(stage: Stage): Unit = {

    val width = 1000
    val height = 100
    val blockSize = 10

    // val entries: java.util.List[LogEntry] = mkTestEntries()
    val entries: java.util.List[LogEntry] = mkEntries(Paths.get("/Users/lad/logfiles/bmw-mexiko/logic.0.log"))

    val entriesProperty = new SimpleListProperty[LogEntry](FXCollections.observableArrayList(entries))
    val bv = new ChunkListView(entriesProperty, new SimpleIntegerProperty(blockSize))
    val scene = new Scene(bv, width, height)
    bv.recalculateListViewElements()

    scene.widthProperty().addListener(JfxUtils.onNew[Number](n => {
      bv.recalculateListViewElements()
      bv.refresh()
    }))

    stage.setScene(scene)

    stage.show()

  }

}
