package app.logorrr

import javafx.application.Application
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.{Button, ColorPicker, TextField, ToolBar}
import javafx.scene.input.{ClipboardContent, DragEvent, MouseEvent, TransferMode}
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.scene.paint.Color
import javafx.scene.transform.Scale
import javafx.scene.SnapshotParameters
import javafx.stage.Stage
import javafx.animation.FadeTransition
import javafx.event.EventHandler
import javafx.util.Duration
import javafx.scene.Cursor

class FDnD extends Application {

  override def start(primaryStage: Stage): Unit = {
    val toolbar = new ToolBar()
    val tools = List("Select", "Draw", "Erase", "Zoom", "Crop")

    tools.foreach { name =>
      toolbar.getItems.add(createToolItem(name, toolbar))
    }

    toolbar.setOnDragOver((event: DragEvent) => {
      if (event.getGestureSource.isInstanceOf[HBox]) {
        event.acceptTransferModes(TransferMode.MOVE)
      }
      event.consume()
    })

    toolbar.setOnDragDropped((event: DragEvent) => {
      event.setDropCompleted(true)
      event.consume()
    })

    val root = new VBox(toolbar)
    root.setPadding(new Insets(20))
    val scene = new Scene(root, 700, 150)
    primaryStage.setTitle("Fully Editable Animated Toolbox")
    primaryStage.setScene(scene)
    primaryStage.show()
  }

  def createToolItem(initialText: String, parentBar: ToolBar): HBox = {
    val container = new HBox(5)
    container.setAlignment(Pos.CENTER_LEFT)
    container.setPadding(new Insets(4, 8, 4, 8))
    container.setStyle("-fx-background-color: #3498db; -fx-background-radius: 5;")

    // 1. DRAG HANDLE (Users grab this to move the item)
    val dragHandle = new Button("☰")
    dragHandle.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: move; -fx-padding: 0 5 0 0;")

    // 2. EDITABLE TEXT FIELD
    val textField = new TextField(initialText)
    textField.setPrefWidth(100)
    textField.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-border-width: 0;")
    HBox.setHgrow(textField, Priority.ALWAYS)

    // 3. COLOR PICKER BUTTON (Simplified)
    val colorPicker = new ColorPicker(Color.web("#3498db"))
    colorPicker.setStyle("-fx-color-label-visible: false; -fx-background-color: transparent;")
    colorPicker.setPrefWidth(40)
    colorPicker.setOnAction { _ =>
      val c = colorPicker.getValue
      val hex = f"#${(c.getRed * 255).toInt}%02x${(c.getGreen * 255).toInt}%02x${(c.getBlue * 255).toInt}%02x"
      container.setStyle(s"-fx-background-color: $hex; -fx-background-radius: 5;")
    }

    // 4. DELETE BUTTON
    val delBtn = new Button("✕")
    delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;")
    delBtn.setOnAction(_ => parentBar.getItems.remove(container))

    container.getChildren.addAll(dragHandle, textField, colorPicker, delBtn)

    // --- DRAG AND DROP LOGIC (Bound to the Drag Handle) ---

    val handleDrag = new EventHandler[MouseEvent] {

      override def handle(event: MouseEvent): Unit = {
        val db = container.startDragAndDrop(TransferMode.MOVE)
        val content = new ClipboardContent()
        content.putString(textField.getText)
        db.setContent(content)

        val params = new SnapshotParameters()
        params.setFill(Color.TRANSPARENT)
        params.setTransform(new Scale(0.9, 0.9))
        db.setDragView(container.snapshot(params, null))

        container.setOpacity(0.0)
        container.getScene.setCursor(Cursor.CLOSED_HAND)
        event.consume()
      }
    }

    dragHandle.setOnDragDetected(handleDrag)

    container.setOnDragEntered((event: DragEvent) => {
      val source = event.getGestureSource
      if (source != container && source.isInstanceOf[HBox]) {
        val items = parentBar.getItems
        val sourceBox = source.asInstanceOf[HBox]
        val targetIdx = items.indexOf(container)

        if (items.contains(sourceBox)) {
          items.remove(sourceBox)
          items.add(targetIdx, sourceBox)
        }
      }
      event.consume()
    })

    container.setOnDragDone((event: DragEvent) => {
      if (container.getScene != null) container.getScene.setCursor(Cursor.DEFAULT)
      val ft = new FadeTransition(Duration.millis(200), container)
      ft.setFromValue(0.0)
      ft.setToValue(1.0)
      ft.play()
      event.consume()
    })

    container
  }
}

object FDnD {
  def main(args: Array[String]): Unit = Application.launch(classOf[FDnD], args: _*)
}