package app.logorrr.views.block

import app.logorrr.LogoRRRApp
import app.logorrr.model.{LogEntry, LogFileReader, LogFileSettings}
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.Filter
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
object ChunkListTestApp {

  def main(args: Array[String]): Unit = {
    System.setProperty("user.language", "en")
    System.setProperty("java.util.logging.SimpleFormatter.format", LogoRRRApp.logFormat)
    javafx.application.Application.launch(classOf[ChunkListApp], args: _*)
  }

}

class ChunkListApp extends Application {

  private def mkEntries(path: Path): java.util.List[LogEntry] = {
    util.Arrays.asList((for ((l, i) <- LogFileReader.readFromFile(path).zipWithIndex) yield LogEntry(i, l, None)): _*)
  }

  def start(stage: Stage): Unit = {

    val width = 1000
    val height = 1000
    val blockSize = 10
    val selectedLineNumber = 0

    // val entries: java.util.List[LogEntry] = ChunkSpec.mkTestLogEntries(1000  )
    val entries: java.util.List[LogEntry] = mkEntries(Paths.get("/Users/lad/logfiles/bmw-mexiko/logic.0.log"))
    val filtersProperty = new SimpleListProperty[Filter](FXCollections.observableArrayList(LogFileSettings.DefaultFilter: _*))
    val entriesProperty = new SimpleListProperty[LogEntry](FXCollections.observableArrayList(entries))
    val clv = new ChunkListView(entriesProperty
      , new SimpleIntegerProperty(selectedLineNumber)
      , new SimpleIntegerProperty(blockSize)
      , filtersProperty)
    val scene = new Scene(clv, width, height)
    clv.updateItems()

    scene.widthProperty().addListener(JfxUtils.onNew[Number](n => {
      println("recalculate")
      clv.updateItems()
      clv.refresh()
    }))

    stage.setScene(scene)

    stage.show()

  }

}
