package app.logorrr.views.search

import javafx.scene.control.TextField
import javafx.scene.input.{KeyCode, KeyEvent}

/**
 * Enter a name for the new search term group
 *
 * @param fireEvent a function which fires an event to trigger an onAction callBack
 */
class SearchTermGroupNameTextField(fireEvent: () => Unit) extends TextField {
  setPrefWidth(265)
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
