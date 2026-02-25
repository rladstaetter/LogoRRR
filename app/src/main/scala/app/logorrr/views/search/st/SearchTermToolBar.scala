package app.logorrr.views.search.st

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTerm}
import app.logorrr.model.{AddSearchTermButtonEvent, BoundId, LogEntry, UpdateLogFilePredicate}
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.stg.AddToFavoritesButton
import app.logorrr.views.search.{MutableSearchTerm, st}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectProperty
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.scene.control.ToolBar
import javafx.scene.layout.{HBox, Pane, Priority}
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog

import java.util.function.Predicate
import scala.jdk.CollectionConverters.*


object SearchTermToolBar extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchTermToolBar])


/**
 * Displays unclassfied and search term toggle buttons.
 *
 * Unclassified toggle button is calculated based on the other search terms.
 */
class SearchTermToolBar(mutLogFileSettings: MutLogFileSettings
                        , entries: ObservableList[LogEntry]
                        , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]])
  extends ToolBar with TinyLog with BoundId(SearchTermToolBar.uiNode(_).value):

  private val unclassifiedSearchTerm = MutableSearchTerm.mkUnclassified(Set())

  private val favoritesChoiceBox = new FavoritesComboBox
  private val spacer = new Pane()
  HBox.setHgrow(spacer, Priority.ALWAYS)
  private val addToFavoritesButton = new AddToFavoritesButton(activeSearchTerms)

  val unclassifiedButton =
    new UnclassifiedToggleButton(entries
      , mutLogFileSettings.mutSearchTerms
      , mutLogFileSettings.showPredicate.showUnclassifiedProperty
      , predicateProperty
      , mutLogFileSettings.showPredicate
    )

  private val immutableLeftNodes = Seq(favoritesChoiceBox, unclassifiedButton)
  private val immutableRightNodes = Seq(spacer, addToFavoritesButton)

  private val listChangeListener = JfxUtils.mkListChangeListener[MutableSearchTerm](processFiltersChange)

  setMaxHeight(Double.PositiveInfinity)

  def init(window: Window): Unit =
    bindIdProperty(mutLogFileSettings.fileIdProperty)

    getItems.addAll(immutableLeftNodes *)
    favoritesChoiceBox.init(LogoRRRGlobals.searchTermGroupEntries, mutLogFileSettings.mutSearchTerms)
    addToFavoritesButton.init(window, mutLogFileSettings.fileIdProperty)
    // add all search terms for this file
    mutLogFileSettings.mutSearchTerms.forEach(addSearchTermButton)
    getItems.addAll(immutableRightNodes *)
    mutLogFileSettings.mutSearchTerms.addListener(listChangeListener)
    unclassifiedButton.init(mutLogFileSettings.fileIdProperty, () => false, unclassifiedSearchTerm)


  def shutdown(): Unit =
    unbindIdProperty()
    favoritesChoiceBox.shutdown()
    addToFavoritesButton.shutdown()
    mutLogFileSettings.mutSearchTerms.removeListener(listChangeListener)
    unclassifiedButton.shutdown(unclassifiedSearchTerm.activeProperty)

  /** if filter list is changed in any way, react to this event and either add or remove filter from UI */
  private def processFiltersChange(change: ListChangeListener.Change[? <: MutableSearchTerm]): Unit =
    while change.next() do {
      if change.wasAdded() then {
        change.getAddedSubList.asScala.foreach(addSearchTermButton)
      } else if change.wasRemoved() then {
        change.getRemoved.asScala.foreach(removeSearchTermButton)
      }
    }

  private def addSearchTermButton(mutSearchTerm: MutableSearchTerm): Unit =

    def addButton() = {
      val button = new SearchTermToggleButton(entries)
      button.init(mutLogFileSettings.fileIdProperty, () => true, mutSearchTerm)
      getItems.add(button)
    }
    // handle favorite 'star' at the end
    if getItems.get(getItems.size - 1).isInstanceOf[AddToFavoritesButton] then
      val favButton = getItems.remove(getItems.size - 1)
      val spacer = getItems.remove(getItems.size - 1)
      addButton()
      getItems.addAll(spacer, favButton)
    else
      addButton()

    fireEvent(AddSearchTermButtonEvent(mutSearchTerm)) // update LogEntry

  private def removeSearchTermButton(mutSearchTerm: MutableSearchTerm): Unit =
    getSearchTermButtons.find(_.getValue == mutSearchTerm.getValue).foreach(b =>
      getItems.remove(b)
      b.shutdown(mutSearchTerm.activeProperty)
    )
    fireEvent(UpdateLogFilePredicate())

  def activeSearchTerms(): Seq[SearchTerm] = getSearchTermButtons.map(_.asSearchTerm)

  def getSearchTermButtons: Seq[SearchTermToggleButton] =
    getItems.asScala.flatMap {
      case sb: SearchTermToggleButton => Option(sb)
      case _ => None
    }.toSeq







