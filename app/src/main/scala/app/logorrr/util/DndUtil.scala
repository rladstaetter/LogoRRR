package app.logorrr.util

import javafx.scene.input.{DragEvent, TransferMode}

object DndUtil:
  /** register any transfer mode for drag'n drop operations */
  def onDragAcceptAll(event: DragEvent): Unit =
    if event.getDragboard.hasFiles then {
      event.acceptTransferModes(TransferMode.ANY *)
    }
