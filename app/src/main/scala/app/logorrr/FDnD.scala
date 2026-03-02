package app.logorrr

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
import javafx.scene.Cursor

class FDnD extends Application {

  override def start(primaryStage: Stage): Unit = {
    val toolbar = new ToolBar()
    val tools = List("Select", "Draw", "Erase", "Zoom", "Crop")

    tools.foreach { name =>
      toolbar.getItems.add(createDraggableButton(name, toolbar))
    }

    // --- TOOLBAR LEVEL HANDLERS (Crucial for firing onDragDropped) ---
    toolbar.setOnDragOver((event: DragEvent) => {
      if (event.getGestureSource.isInstanceOf[Button]) {
        event.acceptTransferModes(TransferMode.MOVE)
      }
      event.consume()
    })

    toolbar.setOnDragDropped((event: DragEvent) => {
      println("Dropped! Ghost should vanish now.")
      // This is the signal to the OS to stop the drag animation immediately
      event.setDropCompleted(true)
      event.consume()
    })

    val root = new VBox(toolbar)
    val scene = new Scene(root, 500, 100)
    primaryStage.setTitle("Animated Reorder - Ghost Version")
    primaryStage.setScene(scene)
    primaryStage.show()
  }

  def createDraggableButton(text: String, parentBar: ToolBar): Button = {
    val btn = new Button(text)
    btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;")

    btn.setOnDragDetected((event: MouseEvent) => {
      val db = btn.startDragAndDrop(TransferMode.MOVE)
      val content = new ClipboardContent()
      content.putString(text)
      db.setContent(content)

      // --- CREATE THE GHOST ---
      val params = new SnapshotParameters()
      params.setFill(Color.TRANSPARENT)
      // Scale it down slightly for a nicer "picked up" look
      params.setTransform(new Scale(0.8, 0.8))
      db.setDragView(btn.snapshot(params, null))

      // Hide the real button so it looks like you are carrying it
      btn.setOpacity(0.0)
      btn.getScene.setCursor(Cursor.CLOSED_HAND)
      event.consume()
    })

    btn.setOnDragEntered((event: DragEvent) => {
      val source = event.getGestureSource
      if (source != btn && source.isInstanceOf[Button]) {
        val items = parentBar.getItems
        val sourceBtn = source.asInstanceOf[Button]
        val targetIdx = items.indexOf(btn)

        if (items.contains(sourceBtn)) {
          // Reorder the list dynamically
          items.remove(sourceBtn)
          items.add(targetIdx, sourceBtn)
        }
      }
      event.consume()
    })

    btn.setOnDragDone((event: DragEvent) => {
      println("Drag Session Finished")
      if (btn.getScene != null) btn.getScene.setCursor(Cursor.DEFAULT)

      // Fade the button back in at its new permanent home
      val ft = new FadeTransition(Duration.millis(250), btn)
      ft.setFromValue(0.0)
      ft.setToValue(1.0)
      ft.play()

      event.consume()
    })

    btn
  }
}

object FDnD {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[FDnD], args: _*)
  }
}