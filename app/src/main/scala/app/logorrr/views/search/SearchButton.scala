package app.logorrr.views.search

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.*
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.paint.Color
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

object SearchButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchButton])


class SearchButton extends Button with BoundFileId(SearchButton.uiNode(_).value):

  setGraphic(new FontIcon(FontAwesomeSolid.SEARCH))
  setTooltip(new Tooltip("search"))
  setMaxWidth(40)

  private val searchTextProperty = new SimpleStringProperty()
  private val colorProperty = new SimpleObjectProperty[Color]()
  private val searchTerms: SimpleListProperty[MutableSearchTerm] = new SimpleListProperty[MutableSearchTerm]()

  setOnAction:
    _ =>
      val searchText = searchTextProperty.get()
      if searchText.nonEmpty then
        searchTerms.add(MutableSearchTerm(searchText, colorProperty.get()))
        colorProperty.setValue(JfxUtils.randColor)
        // searchTextField.clear()
        searchTextProperty.setValue("")


  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , searchTextProperty: Property[String]
           , colorProperty: Property[Color]
           , searchTerms: SimpleListProperty[MutableSearchTerm]): Unit = {
    bindIdProperty(fileIdProperty)
    this.searchTextProperty.bindBidirectional(searchTextProperty)
    this.colorProperty.bindBidirectional(colorProperty)
    this.searchTerms.bindBidirectional(searchTerms)
  }

  def shutdown(searchTextProperty: Property[String]
               , colorProperty: Property[Color]
               , searchTerms: SimpleListProperty[MutableSearchTerm]): Unit = {
    unbindIdProperty()
    this.searchTextProperty.unbindBidirectional(searchTextProperty)
    this.colorProperty.unbindBidirectional(colorProperty)
    this.searchTerms.unbindBidirectional(searchTerms)
  }
