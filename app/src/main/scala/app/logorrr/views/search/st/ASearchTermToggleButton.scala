package app.logorrr.views.search.st

import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.model.*
import app.logorrr.util.{HashUtil, JfxUtils}
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.*
import app.logorrr.views.search.st.RemoveSearchTermButton.buttonCssStyle
import app.logorrr.views.util.CssBindingUtil
import javafx.beans.binding.{Bindings, BooleanBinding, StringBinding}
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.collections.transformation.FilteredList
import javafx.collections.{ObservableList, ObservableSet}
import javafx.scene.control.ToggleButton
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color

import java.lang
import java.util.function.Predicate


object ASearchTermToggleButton extends UiNodeSearchTermAware:

  override def uiNode(fileId: FileId, searchTerm: String): UiNode =
    Option(fileId) match
      case Some(value) => UiNode(classOf[ASearchTermToggleButton].getSimpleName + "-" + HashUtil.md5Sum(value.absolutePathAsString + ":" + searchTerm))
      case None => UiNode(classOf[ASearchTermToggleButton].getSimpleName + "-" + HashUtil.md5Sum(":" + searchTerm))


/**
 * Displays a search term and triggers displaying the results.
 */
abstract class ASearchTermToggleButton(entries: ObservableList[LogEntry]) extends ToggleButton
  with ValuePropertyHolder
  with ColorPropertyHolder
  with FileIdPropertyHolder:

  protected val updateChangeListener: ChangeListener[lang.Boolean]

  val predicateProperty = new SimpleObjectProperty[Predicate[LogEntry]]()

  def getPredicate(): Predicate[LogEntry] = predicateProperty.get()

  def setPredicate(p: Predicate[LogEntry]): Unit = predicateProperty.set(p)

  val origColorProperty = new SimpleObjectProperty[Color]()
  val hitsProperty = new SimpleIntegerProperty()
  val idBinding = Bindings.createStringBinding(() => ASearchTermToggleButton.uiNode(getFileId, getValue).value, fileIdProperty, valueProperty)

  val searchTermLabel: SearchTermLabel = new SearchTermLabel
  val hitsLabel: SearchTermHitsLabel = new SearchTermHitsLabel

  val removeSearchTermButton: RemoveSearchTermButton = new RemoveSearchTermButton

  // hack to circumvent warnings in iconli code
  val colorListener = JfxUtils.onNew[Color](c => removeSearchTermButton.icon.setStyle(buttonCssStyle(c)))

  lazy val contrastColorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()

  var toggle = false
  // fires if one of given properties change -- see update chhange Listener
  val globalPredicateUpdate = Bindings.createBooleanBinding(
    () =>
      toggle = !toggle
      toggle
      , valueProperty, selectedProperty, predicateProperty)

  private val hbox: HBox =
    val spacer: Region = new Region
    spacer.setMinWidth(30)
    HBox.setHgrow(spacer, Priority.ALWAYS)
    val hb = new HBox(searchTermLabel, spacer, removeSearchTermButton)
    hb.setMaxWidth(Double.MaxValue)
    hb

  setGraphic(new VBox(hbox, hitsLabel))

  def setOrigColor(color: Color): Unit = origColorProperty.set(color)

  def getOrigColor: Color = origColorProperty.get()

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , visibleBinding: BooleanBinding
           , mutSearchTerm: MutableSearchTerm
           , mutSearchTerms: ObservableList[MutableSearchTerm]): Unit = {
    globalPredicateUpdate.addListener(updateChangeListener)
    setOrigColor(mutSearchTerm.getColor)
    bindFileIdProperty(fileIdProperty)
    idProperty.bind(idBinding)
    styleProperty().bind(CssBindingUtil.mkGradientStyleBinding(colorProperty))
    valueProperty.bind(mutSearchTerm.valueProperty)
    searchTermLabel.init(contrastColorProperty, valueProperty)
    hitsLabel.init(contrastColorProperty, hitsProperty)
    hitsProperty.bind(Bindings.createIntegerBinding(() => new FilteredList[LogEntry](entries, getPredicate()).size, valueProperty, selectedProperty, predicateProperty))


    colorProperty.bind(Bindings.createObjectBinding[Color](() => {
      if isSelected then getOrigColor else Color.WHITESMOKE
    }, selectedProperty))
    selectedProperty().bindBidirectional(mutSearchTerm.activeProperty)
    removeSearchTermButton.init(
      fileIdProperty
      , visibleBinding
      , mutSearchTerm
      , mutSearchTerms)

    contrastColorProperty.bind(CssBindingUtil.mkContrastPropertyBinding(colorProperty))
    contrastColorProperty.addListener(colorListener)
  }

  def shutdown(activeProperty: BooleanProperty): Unit = {
    globalPredicateUpdate.removeListener(updateChangeListener)
    contrastColorProperty.unbind()
    contrastColorProperty.removeListener(colorListener)
    removeSearchTermButton.shutdown()
    selectedProperty().unbindBidirectional(activeProperty)
    colorProperty.unbind()
    hitsProperty.unbind()
    hitsLabel.shutdown()
    searchTermLabel.shutdown()
    valueProperty.unbind()
    styleProperty().unbind()
    idProperty().unbind()
  }

  def asSearchTerm: SearchTerm = SearchTerm(getValue, getOrigColor, isSelected)




