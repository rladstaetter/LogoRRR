package app.logorrr.views.search.st

import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.model.*
import app.logorrr.util.HashUtil
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.*
import app.logorrr.views.util.CssUtil
import javafx.beans.binding.{Bindings, BooleanBinding, ObjectBinding}
import javafx.beans.property.*
import javafx.scene.control.ToggleButton
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color
import javafx.util.Subscription

import scala.compiletime.uninitialized


object ASearchTermToggleButton extends UiNodeSearchTermAware:

  override def uiNode(fileId: FileId, searchTerm: String): UiNode =
    Option(fileId) match
      case Some(value) => UiNode(classOf[ASearchTermToggleButton].getSimpleName + "-" + HashUtil.md5Sum(value.absolutePathAsString + ":" + searchTerm))
      case None => UiNode(classOf[ASearchTermToggleButton].getSimpleName + "-" + HashUtil.md5Sum(":" + searchTerm))


/**
 * Displays a search term and triggers displaying the results.
 */
abstract class ASearchTermToggleButton extends ToggleButton
  with ValuePropertyHolder
  with ColorPropertyHolder
  with FileIdPropertyHolder:

  var selectedSubscription: Subscription = uninitialized

  protected val searchTermLabel: SearchTermLabel = new SearchTermLabel

  protected val contrastColorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()

  val searchTermBinding: ObjectBinding[SearchTerm] = new ObjectBinding[SearchTerm] {
    bind(valueProperty, selectedProperty, colorProperty)

    override def computeValue(): SearchTerm = SearchTerm(valueProperty.get(), getColor, isSelected)
  }


  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , visibleBinding: BooleanBinding
           , valProperty: StringProperty
           , colorProperty: ObjectPropertyBase[Color]
           , activeProperty: BooleanProperty): Unit = {
    idProperty.bind(Bindings.createStringBinding(() => ASearchTermToggleButton.uiNode(getFileId, getValue).value, fileIdProperty, valueProperty))
    bindFileIdProperty(fileIdProperty)
    selectedProperty().bindBidirectional(activeProperty)
    this.selectedSubscription = selectedProperty().subscribe(e => fireEvent(UpdateLogFilePredicate()))
    this.colorProperty.bind(colorProperty)
    this.contrastColorProperty.bind(CssUtil.mkContrastPropertyBinding(selectedProperty(), colorProperty))
    styleProperty().bind(CssUtil.mkGradientStyleBinding(selectedProperty, colorProperty))

    this.valueProperty.bind(valProperty)
    searchTermLabel.init(contrastColorProperty, this.valueProperty)

  }

  def shutdown(activeProperty: BooleanProperty): Unit = {
    idProperty().unbind()
    fileIdProperty.unbind()
    selectedProperty().unbindBidirectional(activeProperty)
    selectedSubscription.unsubscribe()
    colorProperty.unbind()
    contrastColorProperty.unbind()
    styleProperty().unbind()
    valueProperty.unbind()
    searchTermLabel.shutdown()
  }

  def asSearchTerm: SearchTerm = searchTermBinding.get()




