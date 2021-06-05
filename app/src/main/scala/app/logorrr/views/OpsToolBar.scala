package app.logorrr.views

import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control._
import app.logorrr.ExactMatchFilter


class OpsToolBar(logView: LogView) extends ToolBar {

  val initialText = "<enter search string>"
  val searchTextField = new TextField()
  searchTextField.setPrefWidth(500)
  searchTextField.setPromptText(initialText)

  val cp = new ColorPicker()
  val add = new Button("add")
  add.setOnAction(new EventHandler[ActionEvent]() {
    override def handle(t: ActionEvent): Unit = {
      logView.addFilter(ExactMatchFilter(searchTextField.getText, cp.getValue))
      searchTextField.setText("")
    }
  })
  getItems.addAll(searchTextField, cp, add)
}
