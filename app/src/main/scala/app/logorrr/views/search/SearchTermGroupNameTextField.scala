package app.logorrr.views.search

import javafx.scene.control.TextField
import javafx.scene.input.{KeyCode, KeyEvent}

class SearchTermGroupNameTextField(fireEvent: () => Unit) extends TextField {

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
