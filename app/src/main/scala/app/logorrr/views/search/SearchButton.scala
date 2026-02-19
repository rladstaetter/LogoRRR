package app.logorrr.views.search

import app.logorrr.conf.FileId
import app.logorrr.model.BoundId
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.util.GfxElements
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.paint.Color
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

object SearchButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchButton])


class SearchButton extends Button with BoundId(SearchButton.uiNode(_).value):

  setGraphic(GfxElements.Icons.search)
  setTooltip(new Tooltip("search"))
  setMaxWidth(40)

  private val searchTextProperty = new SimpleStringProperty()
  private val colorProperty = new SimpleObjectProperty[Color]()
  private val mutSearchTerms: ObservableList[MutableSearchTerm] = FXCollections.observableArrayList[MutableSearchTerm]()

  setOnAction:
    _ =>
      val searchText = searchTextProperty.get()
      if searchText.nonEmpty then
        mutSearchTerms.add(MutableSearchTerm(searchText, colorProperty.get()))
        colorProperty.setValue(JfxUtils.randColor)
        searchTextProperty.setValue("")


  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , searchTextProperty: Property[String]
           , colorProperty: Property[Color]
           , mutSearchTerms: ObservableList[MutableSearchTerm]): Unit = {
    bindIdProperty(fileIdProperty)
    this.searchTextProperty.bindBidirectional(searchTextProperty)
    this.colorProperty.bindBidirectional(colorProperty)
    Bindings.bindContentBidirectional(this.mutSearchTerms, mutSearchTerms)
  }

  def shutdown(searchTextProperty: Property[String]
               , colorProperty: Property[Color]
               , mutSearchTerms: ObservableList[MutableSearchTerm]): Unit = {
    unbindIdProperty()
    this.searchTextProperty.unbindBidirectional(searchTextProperty)
    this.colorProperty.unbindBidirectional(colorProperty)
    Bindings.unbindContentBidirectional(this.mutSearchTerms, mutSearchTerms)
  }
