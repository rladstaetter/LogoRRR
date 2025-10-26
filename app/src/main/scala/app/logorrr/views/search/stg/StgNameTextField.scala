package app.logorrr.views.search.stg

import app.logorrr.io.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.TextField
import javafx.scene.input.{KeyCode, KeyEvent}

object StgNameTextField extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[StgNameTextField])

}

/**
 * Enter a name for the new search term group
 *
 * @param fireEvent a function which fires an event to trigger an onAction callBack
 */
class StgNameTextField(fileId: FileId, fireEvent: () => Unit) extends TextField {
  setId(StgNameTextField.uiNode(fileId).value)
  setPrefWidth(150)
  val maxLength = 200

  setPromptText("Enter a name...")

  setOnKeyPressed((event: KeyEvent) => {
    if (event.getCode == KeyCode.ENTER) {
      fireEvent()
    }
  })

  textProperty().addListener((_, oldValue, newValue) => {
    if (newValue.length > maxLength) {
      setText(oldValue)
    }
  })
}
