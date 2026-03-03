package app.logorrr.views.search.st

import app.logorrr.model.ReoderSearchTermButtonEvent
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.util.GfxElements
import javafx.animation.FadeTransition
import javafx.scene.control.Button
import javafx.scene.input.{ClipboardContent, DragEvent, MouseEvent, TransferMode}
import javafx.scene.paint.Color
import javafx.scene.transform.Scale
import javafx.scene.{Cursor, SnapshotParameters}
import javafx.util.Duration

class DragToReorderButton extends AnIkonliButton(GfxElements.Icons.listAlt, GfxElements.ToolTips.mkDragToReorder):
  setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: move; -fx-padding: 0 5 0 0;")

  def init(button: SearchTermToggleButton
           , mutableSearchTerm: MutableSearchTerm): Unit =

    setOnDragDetected((event: MouseEvent) => {
      val db = button.startDragAndDrop(TransferMode.MOVE)
      val content = new ClipboardContent()
      content.putString(button.idProperty().get())
      db.setContent(content)

      val params = new SnapshotParameters()
      params.setFill(Color.TRANSPARENT)
      params.setTransform(new Scale(0.9, 0.9))
      db.setDragView(button.snapshot(params, null))
      button.setOpacity(0.5)
      button.getScene.setCursor(Cursor.CLOSED_HAND)
      event.consume()
    })


