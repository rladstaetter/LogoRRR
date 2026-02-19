package app.logorrr.views.search.st

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.model.{BoundId, LogEntry}
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.stg.AddToFavoritesButton
import app.logorrr.views.search.{MutableSearchTerm, st}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{ObjectProperty, SimpleIntegerProperty}
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.scene.control.ToolBar
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
 *
 * @param filteredList list of entries which are displayed (can be filtered via buttons)
 */
class SearchTermToolBar(mutLogFileSettings: MutLogFileSettings
                        , entries: ObservableList[LogEntry]
                        , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]])
  extends ToolBar with TinyLog with BoundId(SearchTermToolBar.uiNode(_).value):

  val unclassifiedSearchTerm = MutableSearchTerm.mkUnclassified(Set())

  val addToFavoritesButton = new AddToFavoritesButton(activeSearchTerms)

  val unclassifiedButton =
    new UnclassifiedToggleButton(entries
      , mutLogFileSettings.mutSearchTerms
      , mutLogFileSettings.showPredicate.showUnclassifiedProperty
      , predicateProperty
      , mutLogFileSettings.showPredicate
    )

  val listChangeListener = JfxUtils.mkListChangeListener[MutableSearchTerm](processFiltersChange)
  setMaxHeight(Double.PositiveInfinity)

  def init(window: Window): Unit =
    bindIdProperty(mutLogFileSettings.fileIdProperty)
    getItems.addAll(addToFavoritesButton, unclassifiedButton)

    addToFavoritesButton.init(window, mutLogFileSettings.fileIdProperty)
    // add all search terms for this file
    mutLogFileSettings.mutSearchTerms.forEach(addSearchTermButton)
    mutLogFileSettings.mutSearchTerms.addListener(listChangeListener)
    unclassifiedButton.init(mutLogFileSettings.fileIdProperty
      , () => false
      , unclassifiedSearchTerm
      , mutLogFileSettings.mutSearchTerms)


  def shutdown(): Unit =
    unbindIdProperty()
    addToFavoritesButton.shutdown()
    mutLogFileSettings.mutSearchTerms.removeListener(listChangeListener)
    unclassifiedButton.shutdown(unclassifiedSearchTerm.activeProperty)

  /** if filter list is changed in any way, react to this event and either add or remove filter from UI */
  private def processFiltersChange(change: ListChangeListener.Change[? <: MutableSearchTerm]): Unit =
    while change.next() do {
      if change.wasAdded() then {
        change.getAddedSubList.asScala.foreach(addSearchTermButton)
        unclassifiedButton.setPredicate(new Predicate[LogEntry] {
          override def test(t: LogEntry): Boolean =
            unclassifiedButton.isSelected && !mutLogFileSettings.mutSearchTerms.asScala.map(_.getValue).exists(t.value.contains)
        })
      } else if change.wasRemoved() then {
        change.getRemoved.asScala.foreach(removeSearchTermButton)
        unclassifiedButton.setPredicate(new Predicate[LogEntry] {
          override def test(t: LogEntry): Boolean =
            unclassifiedButton.isSelected && !mutLogFileSettings.mutSearchTerms.asScala.map(_.getValue).exists(t.value.contains)
        })
      }
    }

  private def addSearchTermButton(mutSearchTerm: MutableSearchTerm): Unit =
    val button = new SearchTermToggleButton(entries, predicateProperty, mutLogFileSettings.showPredicate)
    button.init(mutLogFileSettings.fileIdProperty
      , () => true
      , mutSearchTerm
      , mutLogFileSettings.mutSearchTerms)
    getItems.add(button)

  private def removeSearchTermButton(mutSearchTerm: MutableSearchTerm): Unit =
    getSearchTermButtons.find(_.getValue == mutSearchTerm.getValue).foreach(b =>
      getItems.remove(b)
      b.shutdown(mutSearchTerm.activeProperty)
    )

  def activeSearchTerms(): Seq[SearchTerm] = getSearchTermButtons.map(_.asSearchTerm)

  def getSearchTermButtons: Seq[SearchTermToggleButton] =
    getItems.asScala.drop(2).map {
      case sb: SearchTermToggleButton => sb
    }.toSeq







