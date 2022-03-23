package app.logorrr.views

import app.logorrr.conf.SettingsIO
import app.logorrr.model.LogFileSettings
import javafx.scene.control._
import javafx.scene.paint.Color

import scala.jdk.CollectionConverters._

object SearchOp {

  class SearchColorPicker extends ColorPicker {
    setValue(Color.MAGENTA)
  }

  class SearchTextField extends TextField {
    setPrefWidth(200)
    setPromptText("<enter search string>")
  }

  class SearchButton(searchTextField: SearchTextField
                     , colorPicker: ColorPicker
                     , addFilterFn: Filter => Unit) extends Button {
    setText("search")

    setOnAction(_ => {
      val filter = new Filter(searchTextField.getText, colorPicker.getValue.toString)
      searchTextField.clear()
      addFilterFn(filter)
    })

  }

}

case class SearchOp(addFilterFn: Filter => Unit) {
  val searchTextField = new SearchOp.SearchTextField
  val colorPicker = new SearchOp.SearchColorPicker()
  val searchButton = new SearchOp.SearchButton(searchTextField, colorPicker, addFilterFn)

  val items = Seq(searchTextField, colorPicker, searchButton)
}


class SearchToolBar(addFilterFn: Filter => Unit) extends ToolBar {

  val searchOp = SearchOp(addFilterFn)

  getItems.addAll(searchOp.items: _*)
}
