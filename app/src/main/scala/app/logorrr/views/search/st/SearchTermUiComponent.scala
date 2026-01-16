package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.util.CssBindingUtil
import javafx.beans.binding.{Bindings, StringBinding}
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.event.ActionEvent
import javafx.scene.control.Label
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color

import java.util.concurrent.Callable

object SearchTermUiComponent:

  def apply(fileId: FileId
            , hits: Integer
            , searchTerm: MutableSearchTerm
            , removeSearchTerm: MutableSearchTerm => Unit): SearchTermUiComponent =
    val vis = new SearchTermUiComponent
    vis.searchTermProperty.set(searchTerm)
    vis.fileIdProperty.set(fileId)
    vis.colorProperty.bind(searchTerm.colorProperty)
    vis.valueProperty.bind(searchTerm.valueProperty)
    vis


class SearchTermUiComponent extends VBox:

  val valueProperty = new SimpleStringProperty()
  val colorProperty = new SimpleObjectProperty[Color]()

  val searchTermProperty = new SimpleObjectProperty[MutableSearchTerm]()
  val fileIdProperty = new SimpleObjectProperty[FileId]()







