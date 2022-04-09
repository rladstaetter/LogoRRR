package app.logorrr.views.visual.sivr

import app.logorrr.util.JfxUtils
import javafx.beans.binding.StringBinding
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage

import scala.util.Random

object SIVR {

  val cols2 = Seq(Color.RED)

  val cols = Seq(Color.RED
    , Color.BLUE
    , Color.ORANGE
    , Color.GREEN
    , Color.BROWN
    , Color.AZURE
    , Color.MAGENTA)


  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[SIVR], args: _*)
  }
}

class SIVR extends javafx.application.Application {
  override def start(primaryStage: Stage): Unit = {

    val bp = new BorderPane
    val i = 1000 * 1000
    val button = new Button(s"set entries ${i} entries")
    val blockSizeLabel = new Label

    val squareImageViz = new SquareImageViz
    button.setOnAction(_ => {
      squareImageViz.setEntries(for (_ <- 1 to i) yield SQView.E(SIVR.cols(Random.nextInt(SIVR.cols.length))))
    })
    val slider = new Slider(5, 50, 5)
    slider.setOrientation(Orientation.HORIZONTAL)
    slider.setPrefWidth(400)

    slider.valueProperty().addListener(JfxUtils.onNew[Number](n => {
      val blockSize = n.intValue()
      blockSizeLabel.setText(blockSize.toString)
      squareImageViz.setBlockSize(blockSize)
    }))



    /*
        slider.valueProperty().bind(squareImageViz.blockWidthProperty)
        slider.valueProperty().bind(squareImageViz.blockHeightProperty)
    */
    bp.setTop(new ToolBar(button, slider, blockSizeLabel))
    bp.setCenter(squareImageViz)

    val s = new Scene(bp, 800, 600)
    primaryStage.setScene(s)
    primaryStage.show()
  }
}