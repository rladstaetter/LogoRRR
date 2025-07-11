package app.logorrr.jfxbfr

import javafx.application.Application
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleListProperty}
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import net.ladstatt.app.{AppId, AppMeta}
import net.ladstatt.util.log.CanLog


/**
 * App to test block list view
 */
object ChunkListTestApp {

  def mkCLTElems(nr: Int): List[CLTElem] = List.fill(nr)(new CLTElem)

  def main(args: Array[String]): Unit = {
    val appMeta = net.ladstatt.app.AppMeta(AppId("ChunkListTestApp", "chunklisttestapp", "chunklisttest.app"), AppMeta.LogFormat)
    net.ladstatt.app.AppMeta.initApp(appMeta)

    System.setProperty("user.language", "en")
    javafx.application.Application.launch(classOf[ChunkListTestApp], args: _*)
  }

}

class CLTElem

class ChunkListTestApp extends Application with CanLog {
  /*
    private def mkEntries(path: Path): java.util.List[LogEntry] = {
      util.Arrays.asList((for ((l, i) <- IoManager.fromPathUsingSecurityBookmarks(path).zipWithIndex) yield LogEntry(i, l, None)): _*)
    }
  */
  def start(stage: Stage): Unit = {

    val width = 1000
    val height = 1000
    val blockSize = 10
    val dividerPosition = 0.5
    val selectedLineNumber = 0

    val entries = ChunkListTestApp.mkCLTElems(1000)
    val filtersProperty = new SimpleListProperty[ColorMatcher](FXCollections.observableArrayList())
    val entriesProperty = new SimpleListProperty[CLTElem](FXCollections.observableArrayList(entries: _*))

    val bp = new BorderPane()

    val elemSelector = new ElementSelector[CLTElem] {
      override def select(a: CLTElem): Unit = ()
    }
    val vizor = new Vizor[CLTElem] {
      /** returns true if entry is active (= selected) - typically this entry is highlighted in some form */
      override def isSelected(a: CLTElem): Boolean = false

      /** element is the first visible element in the text view (the start of the visible elements) */
      override def isFirstVisible(a: CLTElem): Boolean = false

      /** element is the last visible element in the text view (the end of the visible elements) */
      override def isLastVisible(a: CLTElem): Boolean = false

      /** element is visible in the text view */
      override def isVisibleInTextView(a: CLTElem): Boolean = true
    }
    val colorChozzer = new ColorChozzer[CLTElem] {
      override def calc(a: CLTElem): Color = Color.GREY
    }
    val clv = new ChunkListView[CLTElem](entriesProperty
      , new SimpleIntegerProperty(selectedLineNumber)
      , new SimpleIntegerProperty(blockSize)
      , filtersProperty
      , new SimpleDoubleProperty(dividerPosition)
      , new SimpleIntegerProperty()
      , new SimpleIntegerProperty()
      , _ => ()
      , vizor, colorChozzer, elemSelector)
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
      clv.logEntries.setAll(ChunkListTestApp.mkCLTElems(nrElems) : _*)
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
