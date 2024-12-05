package app.logorrr.views

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

class HorizontalVerticalStackedTreemapApp extends Application {
  override def start(primaryStage: Stage): Unit = {
    // Numbers to visualize
    val numbers = List(1, 3, 5, 7, 11, 17, 50, 100, 400, 1000, 5000, 10000)

    // Calculate the total sum
    val total = numbers.sum.toDouble

    // Define the dimensions of the rectangle (painting area)
    val width = 1000.0
    val height = 100.0

    // Create a root pane to hold rectangles
    val root = new Pane()

    // Recursive function to partition and draw rectangles
    def drawStackedRectangles(data: List[Double], startX: Double, startY: Double, w: Double, h: Double): Unit = {
      if (data.isEmpty) return

      if (data.size == 1) {
        // Base case: Draw a single rectangle
        val rect = new Rectangle(startX, startY, w, h)
        rect.setFill(Color.hsb((data.head / total) * 360, 0.7, 0.9))
        rect.setStroke(Color.BLACK)
        root.getChildren.add(rect)
        return
      }

      // Determine whether to split horizontally or vertically based on aspect ratio
      //val isHorizontalSplit = Math.random() > 0.5
      val isHorizontalSplit = w > h

      // Calculate total for proportions
      val subsetTotal = data.sum
      var accumulated = 0.0
      val splitIndex = data.indexWhere { value =>
        accumulated += value
        accumulated >= subsetTotal / 2
      }
      // Partition data into two groups
      val (firstHalf, secondHalf) = data.splitAt(splitIndex)

      if (isHorizontalSplit) {
        // Split horizontally
        val splitWidth = w * firstHalf.sum / subsetTotal
        drawStackedRectangles(firstHalf, startX, startY, splitWidth, h)
        drawStackedRectangles(secondHalf, startX + splitWidth, startY, w - splitWidth, h)
      } else {
        // Split vertically
        val splitHeight = h * firstHalf.sum / subsetTotal
        drawStackedRectangles(firstHalf, startX, startY, w, splitHeight)
        drawStackedRectangles(secondHalf, startX, startY + splitHeight, w, h - splitHeight)
      }
    }

    // Start drawing the treemap
    drawStackedRectangles(numbers.map(_.toDouble), 0.0, 0.0, width, height)

    // Create the scene and show the stage
    val scene = new Scene(root, width, height)
    primaryStage.setTitle("Horizontal and Vertical Stacked Treemap")
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}

object HorizontalVerticalStackedTreemapApp {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[HorizontalVerticalStackedTreemapApp], args: _*)
  }
}