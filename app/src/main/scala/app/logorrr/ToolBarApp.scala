package app.logorrr

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.{VBox, StackPane, Priority}
import javafx.scene.input.{TransferMode, ClipboardContent, DragEvent, MouseEvent}
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.geometry.Insets
import javafx.animation.FadeTransition
import javafx.util.Duration
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.{Button, ToolBar, Separator}
import javafx.scene.layout.VBox
import javafx.scene.input.{TransferMode, ClipboardContent, DragEvent, MouseEvent}
import javafx.scene.transform.Scale
import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.animation.ScaleTransition
import javafx.util.Duration

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.{Button, ToolBar}
import javafx.scene.layout.VBox
import javafx.scene.input.{TransferMode, ClipboardContent, DragEvent, MouseEvent}
import javafx.scene.transform.Scale
import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.animation.FadeTransition
import javafx.util.Duration

class ToolBarApp extends Application {

  override def start(primaryStage: Stage): Unit = {
    val toolbar = new ToolBar()
    val tools = List("Select", "Draw", "Erase", "Zoom", "Crop")

    tools.foreach { name =>
      toolbar.getItems.add(createDraggableButton(name, toolbar))
    }

    val root = new VBox(toolbar)
    val scene = new Scene(root, 500, 100)

    primaryStage.setTitle("Smooth Reorder ToolBar")
    primaryStage.setScene(scene)
    primaryStage.show()
  }

  def createDraggableButton(text: String, parentBar: ToolBar): Button = {
    val btn = new Button(text)
    btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15;")

    btn.setOnDragDetected((event: MouseEvent) => {
      val db = btn.startDragAndDrop(TransferMode.MOVE)
      val content = new ClipboardContent()
      content.putString(text)
      db.setContent(content)

      val params = new SnapshotParameters()
      params.setFill(Color.TRANSPARENT)
      params.setTransform(new Scale(0.7, 0.7))
      db.setDragView(btn.snapshot(params, null))

      // Make the original invisible immediately so only the ghost is seen
      btn.setOpacity(0.0)
      event.consume()
    })

    btn.setOnDragOver((event: DragEvent) => {
      if (event.getGestureSource != btn && event.getDragboard.hasString) {
        event.acceptTransferModes(TransferMode.MOVE)
      }
      event.consume()
    })

    btn.setOnDragEntered((event: DragEvent) => {
      val source = event.getGestureSource.asInstanceOf[Button]
      if (source != btn) {
        val items = parentBar.getItems
        val targetIdx = items.indexOf(btn)

        // Dynamic reordering while hovering
        if (items.contains(source)) {
          items.remove(source)
          items.add(targetIdx, source)
        }
      }
      event.consume()
    })

    // --- KEY ADDITION: The Drop Handler ---
    btn.setOnDragDropped((event: DragEvent) => {
      // This tells the OS the drop was successful.
      // It prevents the "snap-back" ghost animation in most OS environments.
      event.setDropCompleted(true)
      event.consume()
    })

    btn.setOnDragDone((event: DragEvent) => {
      // Clean up: Fade the button back in at its new position
      btn.setOpacity(0.0)
      val ft = new FadeTransition(Duration.millis(200), btn)
      ft.setFromValue(0.0)
      ft.setToValue(1.0)
      ft.play()

      event.consume()
    })

    btn
  }
}

object ToolBarApp {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ToolBarApp], args: _*)
  }
}