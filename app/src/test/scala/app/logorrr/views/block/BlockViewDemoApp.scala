package app.logorrr.views.block

import app.logorrr.util.JfxUtils
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage

import scala.util.Random

/**
 * Demo application for BlockViewPane component
 */
object BlockViewDemoApp {

  case class Block(index: Int, color: Color)

  val cols: Seq[Color] = Seq(Color.RED
    , Color.GREEN
    , Color.BLUE
    , Color.ORANGE)

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[BlockViewDemoApp], args: _*)
  }
}

class BlockViewDemoApp extends javafx.application.Application {
  import scala.jdk.CollectionConverters._
  override def start(primaryStage: Stage): Unit = {

    val bp = new BorderPane
    val nrBlocksLabel = new Label(s"# blocks")
    val currentBlockSizeLabel = new Label

    val nrElemsChoiceBox = new ChoiceBox[Int]()
    val elems = FXCollections.observableArrayList(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000)
    val blockViewPane = new BlockViewPane("string")
    nrElemsChoiceBox.setItems(elems)
    nrElemsChoiceBox.getSelectionModel().selectedIndexProperty().addListener(JfxUtils.onNew[Number](n => {
      val nrElems = elems.get(n.intValue())
      val es = for (i <- 1 to nrElems) yield BlockViewDemoApp.Block(i, BlockViewDemoApp.cols(Random.nextInt(BlockViewDemoApp.cols.length)))
    //  blockViewPane.setEntries(FXCollections.observableList(es.asJava))
    }))

    val slider = new Slider(5, 50, 5)
    slider.setOrientation(Orientation.HORIZONTAL)
    slider.setPrefWidth(90)

    slider.valueProperty().addListener(JfxUtils.onNew[Number](n => {
      val blockSize = n.intValue()
      currentBlockSizeLabel.setText(blockSize.toString)
      blockViewPane.setBlockSize(blockSize)
    }))

    bp.setTop(new ToolBar(nrBlocksLabel, nrElemsChoiceBox, slider))
    bp.setCenter(blockViewPane)

    val s = new Scene(bp, 300, 200)
    primaryStage.setScene(s)
    primaryStage.show()
  }
}