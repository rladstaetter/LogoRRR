package app.logorrr.views.block

import app.logorrr.LogoRRRApp
import app.logorrr.model.{LogEntry, LogFileReader}
import app.logorrr.util.JfxUtils
import javafx.application.Application
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty}
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.stage.Stage

import java.nio.file.{Path, Paths}
import java.util

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

  private def mkEntries(path: Path): java.util.List[LogEntry] = {
    util.Arrays.asList((for ((l, i) <- LogFileReader.readFromFile(path).zipWithIndex) yield LogEntry(i, l, None)): _*)
  }

  def start(stage: Stage): Unit = {

    val width = 1000
    val height = 1000
    val blockSize = 2

    val entries: java.util.List[LogEntry] = ChunkSpec.mkTestLogEntries(1000 * 1000 * 100)
    //val entries: java.util.List[LogEntry] = mkEntries(Paths.get("/Users/lad/logfiles/bmw-mexiko/logic.0.log"))

    val entriesProperty = new SimpleListProperty[LogEntry](FXCollections.observableArrayList(entries))
    val bv = new ChunkListView(entriesProperty
      , new SimpleIntegerProperty(10)
      , new SimpleIntegerProperty(blockSize))
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
