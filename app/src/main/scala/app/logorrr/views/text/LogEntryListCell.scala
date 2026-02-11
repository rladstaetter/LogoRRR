package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.util.JetbrainsMonoFontStyleBinding
import app.logorrr.views.text.contextactions.{CopyEntriesMenuItem, IgnoreAboveMenuItem, IgnoreBelowMenuItem}
import javafx.beans.property.{Property, SimpleIntegerProperty}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{ContextMenu, ListCell}
import javafx.scene.paint.Color

import scala.jdk.CollectionConverters.*

class LogEntryListCell(filteredList: FilteredList[LogEntry]
                       , searchTermsAndColors: ObservableList[(String, Color)]
                       , selectedLineNumberProperty: SimpleIntegerProperty
                       , scrollToActiveLogEntry: () => Unit
                       , fontSizeProperty: Property[Number]
                       , maxSizeProperty: SimpleIntegerProperty) extends ListCell[LogEntry]:
  setGraphic(null)

  override def updateItem(t: LogEntry, b: Boolean): Unit =
    super.updateItem(t, b)
    styleProperty().unbind()
    Option(t) match
      case Some(e) =>
        styleProperty().bind(new JetbrainsMonoFontStyleBinding(fontSizeProperty))
        val entry =
          new LogTextViewLabel(e.lineNumber
            , e.value
            , maxSizeProperty.get()
            , searchTermsAndColors.asScala.toSeq
            , fontSizeProperty)
        setGraphic(entry)

        val copySelectionMenuItem = new CopyEntriesMenuItem(getListView.getSelectionModel)
        val ignoreAboveMenuItem = new IgnoreAboveMenuItem(selectedLineNumberProperty
          , e
          , filteredList
          , scrollToActiveLogEntry)
        val ignoreBelowMenuItem = new IgnoreBelowMenuItem(e, filteredList)
        val menu = new ContextMenu(copySelectionMenuItem, ignoreAboveMenuItem, ignoreBelowMenuItem)
        setContextMenu(menu)
      case None =>
        styleProperty().unbind()
        Option(getGraphic) match {
          case Some(label: LogTextViewLabel) => LogTextViewLabel.unbind(label)
          case _ =>
        }
        setGraphic(null)
        setContextMenu(null)
