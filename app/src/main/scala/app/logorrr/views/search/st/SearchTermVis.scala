package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.{InvalidationListener, Observable}
import javafx.event.ActionEvent

object SearchTermVis:

  def apply(fileId: FileId
            , hits: Integer
            , searchTerm: MutableSearchTerm
            , isUnclassified: Boolean
            , removeSearchTerm: MutableSearchTerm => Unit): SearchTermVis =
    val vis = new SearchTermVis
    if isUnclassified then
      vis.removeFilterButton.setVisible(false)
      vis.removeFilterButton.setManaged(false)
      vis.removeFnProperty.set(_ => ())
    else
      vis.removeFnProperty.set(removeSearchTerm)
    vis.searchTermProperty.set(searchTerm)
    vis.fileIdProperty.set(fileId)
    vis.hitsProperty.set(hits)
    vis.textProperty.set(searchTerm.getPredicate.description)
    vis




class SearchTermVis extends SimpleSearchTermVis:

  val removeFilterButton = new RemoveSearchTermButton
  val removeFnProperty = new SimpleObjectProperty[MutableSearchTerm => Unit]()
  val searchTermProperty = new SimpleObjectProperty[MutableSearchTerm]()
  val fileIdProperty = new SimpleObjectProperty[FileId]()

  removeFilterButton.idProperty.bind(Bindings.createStringBinding(
    () =>
      (for fileId <- Option(fileIdProperty.get())
            searchTerm <- Option(searchTermProperty.get())
      yield RemoveSearchTermButton.uiNode(fileId, searchTerm).value).getOrElse(""),
    fileIdProperty, searchTermProperty))

  removeFnProperty.addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      removeFilterButton.onActionProperty().set(
        (_: ActionEvent) => removeFnProperty.get()(searchTermProperty.get())
      )
    }
  })

  hitsLabel.textProperty().bind(Bindings.createStringBinding(() => {
    "Hits: " + hitsProperty.get()
  }, hitsProperty))

  hbox.getChildren.add(removeFilterButton)

