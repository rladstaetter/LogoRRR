package app.logorrr.views.search.searchterm

import app.logorrr.clv.color.ColorUtil
import app.logorrr.io.FileId
import app.logorrr.views.MutableSearchTerm
import javafx.beans.binding.Bindings
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.event.ActionEvent
import javafx.scene.control.Label
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color

object SearchTermVis {

  def apply(fileId: FileId
            , hits: Integer
            , searchTerm: MutableSearchTerm
            , isUnclassified: Boolean
            , removeSearchTerm: MutableSearchTerm => Unit): SearchTermVis = {
    val vis = new SearchTermVis
    if (isUnclassified) {
      vis.removeFilterButton.setVisible(false)
      vis.removeFilterButton.setManaged(false)
      vis.removeFnProperty.set(_ => ())
    } else {
      vis.removeFnProperty.set(removeSearchTerm)
    }
    vis.searchTermProperty.set(searchTerm)
    vis.fileIdProperty.set(fileId)
    vis.hitsProperty.set(hits)
    vis.textProperty.set(searchTerm.getPredicate.description)
    vis
  }
}


class SearchTermVis extends VBox {

  val removeFilterButton = new RemoveSearchTermButton
  val removeFnProperty = new SimpleObjectProperty[MutableSearchTerm => Unit]()
  val searchTermProperty = new SimpleObjectProperty[MutableSearchTerm]()
  val fileIdProperty = new SimpleObjectProperty[FileId]()
  val hitsProperty = new SimpleIntegerProperty()
  val textProperty = new SimpleStringProperty()
  val colorProperty = new SimpleObjectProperty[Color]()

  removeFilterButton.idProperty.bind(Bindings.createStringBinding(
    () =>
      (for {fileId <- Option(fileIdProperty.get())
            searchTerm <- Option(searchTermProperty.get())}
      yield RemoveSearchTermButton.uiNode(fileId, searchTerm).value).getOrElse(""),
    fileIdProperty, searchTermProperty))

  removeFnProperty.addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      removeFilterButton.onActionProperty().set(
        (_: ActionEvent) => removeFnProperty.get()(searchTermProperty.get())
      )
    }
  })

  colorProperty.addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      setStyle(ColorUtil.mkCssBackgroundString(colorProperty.get()))
    }
  })

  private val label = new Label()
  label.setStyle("-fx-font-weight: bold;")
  label.textProperty().bind(textProperty)


  val spacer: Region = new Region
  spacer.setMinWidth(30)
  HBox.setHgrow(spacer, Priority.ALWAYS)

  private val hbox = new HBox(label, spacer, removeFilterButton)
  hbox.setMaxWidth(Double.MaxValue)
  private val hitsLabel = new SearchTermHitsLabel
  hitsLabel.textProperty().bind(Bindings.createStringBinding(() => {
    "Hits: " + hitsProperty.get()
  }, hitsProperty))

  getChildren.addAll(hbox, hitsLabel)

}
