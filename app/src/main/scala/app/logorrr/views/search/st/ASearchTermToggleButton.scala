package app.logorrr.views.search.st

import app.logorrr.conf.mut.LogFilePredicate
import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.model.*
import app.logorrr.util.{HashUtil, JfxUtils}
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.*
import app.logorrr.views.search.st.RemoveSearchTermButton.buttonCssStyle
import app.logorrr.views.util.CssBindingUtil
import javafx.beans.binding.{Bindings, BooleanBinding}
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
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
abstract class ASearchTermToggleButton(entries: ObservableList[LogEntry]
                                       , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]]
                                       , logFilePredicate: LogFilePredicate) extends ToggleButton
  with ValuePropertyHolder
  with ColorPropertyHolder
  with FileIdPropertyHolder:

  private val updateLogFilePredicate: ChangeListener[lang.Boolean] = JfxUtils.onNew[java.lang.Boolean](e => {
    LogFilePredicate.update(predicateProperty, logFilePredicate)
  })

  private val origColorProperty = new SimpleObjectProperty[Color]()
  val hitsProperty = new SimpleLongProperty()
  private val searchTermLabel: SearchTermLabel = new SearchTermLabel
  private val hitsLabel: SearchTermHitsLabel = new SearchTermHitsLabel

  private val removeSearchTermButton: RemoveSearchTermButton = new RemoveSearchTermButton

  // hack to circumvent warnings in iconli code
  private val colorListener = JfxUtils.onNew[Color](c => removeSearchTermButton.icon.setStyle(buttonCssStyle(c)))

  lazy val contrastColorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()

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
    selectedProperty().addListener(updateLogFilePredicate)
    setOrigColor(mutSearchTerm.getColor)
    bindFileIdProperty(fileIdProperty)
    idProperty.bind(Bindings.createStringBinding(() => ASearchTermToggleButton.uiNode(getFileId, getValue).value, fileIdProperty, valueProperty))
    styleProperty().bind(CssBindingUtil.mkGradientStyleBinding(selectedProperty, colorProperty))
    hitsLabel.init(contrastColorProperty, hitsProperty)


    valueProperty.bind(mutSearchTerm.valueProperty)
    searchTermLabel.init(contrastColorProperty, valueProperty)

    colorProperty.bind(Bindings.createObjectBinding[Color](() => {
      if isSelected then getOrigColor else getOrigColor
    }, selectedProperty))

    selectedProperty().bindBidirectional(mutSearchTerm.activeProperty)
    removeSearchTermButton.init(
      fileIdProperty
      , visibleBinding
      , mutSearchTerm
      , mutSearchTerms)

    contrastColorProperty.bind(CssBindingUtil.mkContrastPropertyBinding(selectedProperty(), colorProperty))
    contrastColorProperty.addListener(colorListener)
  }

  def shutdown(activeProperty: BooleanProperty): Unit = {
    selectedProperty().removeListener(updateLogFilePredicate)
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




