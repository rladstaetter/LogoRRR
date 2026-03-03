package app.logorrr.views.search.st

import javafx.util.Duration
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTerm}
import app.logorrr.model.*
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.stg.AddToFavoritesButton
import app.logorrr.views.search.{MutableSearchTerm, st}
import javafx.animation.FadeTransition
import javafx.beans.binding.BooleanBinding
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.scene.control.ToolBar
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.layout.{HBox, Pane, Priority}
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog

import scala.jdk.CollectionConverters.*


object SearchTermToolBar extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchTermToolBar])

/**
 * Displays unclassfied and search term toggle buttons.
 *
 * Unclassified toggle button is calculated based on the other search terms.
 */
class SearchTermToolBar(mutLogFileSettings: MutLogFileSettings, entries: ObservableList[LogEntry])
  extends ToolBar with TinyLog with BoundId(SearchTermToolBar.uiNode(_).value):

  private val unclassifiedSearchTerm = MutableSearchTerm.mkUnclassified(Set())

  private val favoritesChoiceBox = new FavoritesComboBox
  private val spacer = new Pane()
  HBox.setHgrow(spacer, Priority.ALWAYS)
  private val addToFavoritesButton = new AddToFavoritesButton(activeSearchTerms)

  val unclassifiedButton = new UnclassifiedToggleButton()

  private val immutableLeftNodes = Seq(favoritesChoiceBox, unclassifiedButton)
  private val immutableRightNodes = Seq(spacer, addToFavoritesButton)

  private val listChangeListener = JfxUtils.mkListChangeListener[MutableSearchTerm](searchTermsChange)

  setMaxHeight(Double.PositiveInfinity)

  def init(window: Window): Unit =
    bindIdProperty(mutLogFileSettings.fileIdProperty)
    getItems.addAll(immutableLeftNodes *)
    favoritesChoiceBox.init(LogoRRRGlobals.searchTermGroupEntries, mutLogFileSettings.mutSearchTerms)
    addToFavoritesButton.init(window, mutLogFileSettings.fileIdProperty)
    // add all search terms for this file
    mutLogFileSettings.mutSearchTerms.asScala.zipWithIndex.foreach((m, i) => addSearchTermButton(immutableLeftNodes.size + i, m))
    getItems.addAll(immutableRightNodes *)
    mutLogFileSettings.mutSearchTerms.addListener(listChangeListener)
    unclassifiedButton.init(mutLogFileSettings.fileIdProperty, () => false, unclassifiedSearchTerm.valueProperty, unclassifiedSearchTerm.colorProperty, mutLogFileSettings.showUnclassifiedProperty)
    initDnD()

  def shutdown(): Unit =
    unbindIdProperty()
    favoritesChoiceBox.shutdown()
    addToFavoritesButton.shutdown()
    mutLogFileSettings.mutSearchTerms.removeListener(listChangeListener)
    unclassifiedButton.shutdown(mutLogFileSettings.showUnclassifiedProperty)


  def initDnD(): Unit = {
    setOnDragOver((event: DragEvent) => {
      if (event.getGestureSource.isInstanceOf[SearchTermToggleButton]) {
        event.acceptTransferModes(TransferMode.MOVE)
      }
      event.consume()
    })

    setOnDragDropped((event: DragEvent) => {
      val source = event.getGestureSource
      if (source.isInstanceOf[SearchTermToggleButton]) {
        event.setDropCompleted(true)
      }
      event.consume()
    })
  }

  /** if filter list is changed in any way, react to this event and either add or remove filter from UI */
  private def searchTermsChange(change: ListChangeListener.Change[? <: MutableSearchTerm]): Unit =
    while change.next() do {
      if change.wasAdded() then {
        change.getAddedSubList.asScala.zipWithIndex.foreach((m, i) => addSearchTermButton(immutableLeftNodes.size + change.getFrom + i, m))
      } else if change.wasRemoved() then {
        change.getRemoved.forEach(m => removeSearchTermButton(m))
      }
    }


  private def add(index: Int, mutableSearchTerm: MutableSearchTerm): Unit = {
    val button = new SearchTermToggleButton(entries)
    button.init(mutLogFileSettings.fileIdProperty, () => true, mutableSearchTerm)
    val ft = new FadeTransition(Duration.millis(200), button)
    ft.setFromValue(0.2)
    ft.setToValue(1.0)
    ft.play()
    getItems.add(index, button)
  }

  private def addSearchTermButton(index: Int, mutSearchTerm: MutableSearchTerm): Unit =
    // handle favorite 'star' at the end
    if getItems.get(getItems.size - 1).isInstanceOf[AddToFavoritesButton] then
      // val favButton = getItems.remove(getItems.size - 1)
      // val spacer = getItems.remove(getItems.size - 1)
      add(index, mutSearchTerm)
    // getItems.addAll(spacer, favButton)
    else
      add(index, mutSearchTerm)

    fireEvent(AddSearchTermButtonEvent(mutSearchTerm)) // update LogEntry

  private def removeSearchTermButton(mutSearchTerm: MutableSearchTerm): Unit =
    getSearchTermButtons.find(_.getValue == mutSearchTerm.getValue).foreach(b =>
      getItems.remove(b)
      b.shutdown(mutSearchTerm.activeProperty, mutSearchTerm.colorProperty)
    )
    fireEvent(UpdateLogFilePredicate())

  def activeSearchTerms(): Seq[SearchTerm] = getSearchTermButtons.map(_.asSearchTerm)

  private def getSearchTermButtons: Seq[SearchTermToggleButton] =
    getItems.asScala.flatMap {
      case sb: SearchTermToggleButton => Option(sb)
      case _ => None
    }.toSeq








