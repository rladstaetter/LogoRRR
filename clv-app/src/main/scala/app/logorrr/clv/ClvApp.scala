package app.logorrr.clv

import app.logorrr.clv.color.ColorPicker
import javafx.application.Application
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import net.ladstatt.app.{AppId, AppMeta}
import net.ladstatt.util.log.CanLog

import java.util
import scala.jdk.CollectionConverters._


/**
 * App to test block list view
 */
object ClvApp {

  def mkCLTElems(nr: Int): java.util.List[ClvElem] = List.fill(nr)(new ClvElem).asJava

  def main(args: Array[String]): Unit = {
    val appMeta = net.ladstatt.app.AppMeta(AppId("ChunkListTestApp", "chunklisttestapp", "chunklisttest.app"), AppMeta.LogFormat)
    net.ladstatt.app.AppMeta.initApp(appMeta)

    System.setProperty("user.language", "en")
    javafx.application.Application.launch(classOf[ClvApp], args: _*)
  }

}

class ClvElem

class ClvApp extends Application with CanLog {

  private val elems = FXCollections.observableArrayList(0, 1, 10, 100, 1000 * 1000, 1000 * 1000 * 10)

  private val DefaultElemCount = elems.asScala.head
  // private val elements = FXCollections.observableArrayList(ClvApp.mkCLTElems(DefaultElemCount))
  private val elements = FXCollections.observableArrayList(new util.ArrayList[ClvElem]())

  def start(stage: Stage): Unit = {

    val width = 1000
    val height = 1000
    val blockSize = 10
    val selectedLineNumber = 0

    val bp = new BorderPane()

    val elemSelector = new ElementSelector[ClvElem] {
      override def select(a: ClvElem): Unit = ()
    }
    val vizor = new Vizor[ClvElem] {
      /** returns true if entry is active (= selected) - typically this entry is highlighted in some form */
      override def isSelected(a: ClvElem): Boolean = false

      /** element is the first visible element in the text view (the start of the visible elements) */
      override def isFirstVisible(a: ClvElem): Boolean = false

      /** element is the last visible element in the text view (the end of the visible elements) */
      override def isLastVisible(a: ClvElem): Boolean = false

      /** element is visible in the text view */
      override def isVisibleInTextView(a: ClvElem): Boolean = true
    }
    val colorPicker = new ColorPicker[ClvElem] {
      override def calc(a: ClvElem): Color = Color.GREY
    }
    val clv = new ChunkListView[ClvElem](elements
      , new SimpleIntegerProperty(selectedLineNumber)
      , new SimpleIntegerProperty(blockSize)
      , new SimpleIntegerProperty()
      , new SimpleIntegerProperty()
      , _ => ()
      , vizor
      , colorPicker
      , elemSelector)
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
    nrElemsChoiceBox.setItems(elems)
    nrElemsChoiceBox.getSelectionModel.selectedIndexProperty().addListener(JfxUtils.onNew[Number](n => {
      val nrElems = elems.get(n.intValue())
      for (_ <- 1 to 500) {
        clv.elements.clear()
        clv.elements.setAll(ClvApp.mkCLTElems(nrElems))
      }
    }))
    nrElemsChoiceBox.setValue(DefaultElemCount)

    bp.setTop(new ToolBar(nrBlocksLabel, nrElemsChoiceBox, slider))
    bp.setCenter(sp)

    val refreshListener = JfxUtils.onNew[Number](n => {
      if (n.doubleValue() > 0.1) {
        clv.recalculateAndUpdateItems()
      }
    })

    sp.getDividers.get(0).positionProperty().addListener(refreshListener)

    val scene = new Scene(bp, width, height)
    stage.setScene(scene)

    stage.showingProperty().addListener((_, _, isNowShowing) => {
      if (isNowShowing) {
        clv.recalculateAndUpdateItems()
        logTrace("Scene is loaded and displayed!")
      }
    })

    stage.show()

  }

}
