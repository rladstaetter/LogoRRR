package net.ladstatt.logorrr.views

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{Label, TextField, ToolBar}
import net.ladstatt.logorrr.LogEntry


class OpsToolBar(filteredList: FilteredList[LogEntry]) extends ToolBar {

  val initialText = "<enter search string>"
  val searchTextField = new TextField()
  searchTextField.setPrefWidth(500)
  searchTextField.setPromptText(initialText)
  searchTextField.textProperty().addListener(new ChangeListener[String] {
    override def changed(observableValue: ObservableValue[_ <: String], t: String, t1: String): Unit = {
      Option(t1) match {
        case Some(value) => filteredList.setPredicate((t: LogEntry) => t.value.contains(value))
        case None =>
      }
    }
  })

  private val label = new Label("Search")
  label.setPrefWidth(100)
  getItems.addAll(label, searchTextField)
}
