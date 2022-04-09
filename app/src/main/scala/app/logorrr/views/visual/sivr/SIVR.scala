package app.logorrr.views.visual.sivr

import app.logorrr.util.JfxUtils
import javafx.beans.binding.StringBinding
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import sun.jvm.hotspot.utilities.Observable

import scala.util.Random

object SIVR {

  val cols = Seq(Color.RED
    , Color.GREEN
    , Color.BLUE
    , Color.ORANGE)

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[SIVR], args: _*)
  }
}

class SIVR extends javafx.application.Application {
  override def start(primaryStage: Stage): Unit = {

    val bp = new BorderPane
    val nrBlocksLabel = new Label(s"# blocks")
    val sizeBlocksLabel = new Label(s"size")
    val currentBlockSizeLabel = new Label

    val nrElemsChoiceBox = new ChoiceBox[Int]()
    val elems = FXCollections.observableArrayList(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000)
    val squareImageViz = new SquareImageViz
    nrElemsChoiceBox.setItems(elems)
    nrElemsChoiceBox.getSelectionModel().selectedIndexProperty().addListener(JfxUtils.onNew[Number](n => {
      val nrElems = elems.get(n.intValue())
      squareImageViz.setEntries(for (_ <- 1 to nrElems) yield BlockView.E(SIVR.cols(Random.nextInt(SIVR.cols.length))))
    }))
 //   nrElemsChoiceBox.setValue(elems.get(0))

    val slider = new Slider(5, 50, 5)
    slider.setOrientation(Orientation.HORIZONTAL)
    slider.setPrefWidth(90)

    slider.valueProperty().addListener(JfxUtils.onNew[Number](n => {
      val blockSize = n.intValue()
      currentBlockSizeLabel.setText(blockSize.toString)
      squareImageViz.setBlockSize(blockSize)
    }))

    bp.setTop(new ToolBar(nrBlocksLabel, nrElemsChoiceBox, slider))
    bp.setCenter(squareImageViz)

    val s = new Scene(bp, 300, 200)
    primaryStage.setScene(s)
    primaryStage.show()
  }
}