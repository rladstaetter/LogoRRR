package net.ladstatt.logorrr.views

import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{ToggleButton, ToolBar}
import net.ladstatt.logorrr.{LogEntry, LogSeverity}

import java.text.DecimalFormat

/** A toolbar with buttons which filter log events */
object FilterButtonsToolBar {

  val percentFormatter = new DecimalFormat("#.##")

  def percentAsString(ls: LogSeverity
                      , occurences: Map[LogSeverity, Int]
                      , size: Int): String = {
    percentFormatter.format((100 * occurences(ls).toDouble) / size.toDouble) + "%"
  }


}

/**
 * Depending on buttons pressed, filteredList will be mutated to show only selected items.
 *
 * @param filteredList
 * @param occurences
 * @param size
 */
class FilterButtonsToolBar(filteredList: FilteredList[LogEntry]
                           , occurences: Map[LogSeverity, Int]
                           , size: Int) extends ToolBar {

  val filterButtons: Map[LogSeverity, ToggleButton] = {
    LogSeverity.seq.map((ls: LogSeverity) => {
      val button = new ToggleButton(ls.name + ": " + occurences(ls) + " " + FilterButtonsToolBar.percentAsString(ls, occurences, size))
      button.setSelected(true)
      button.selectedProperty().addListener(new InvalidationListener {
        // if any of the buttons changes its selected value, reevaluate predicate
        // and thus change contents of all views which display filtered List
        override def invalidated(observable: Observable): Unit = {
          // it is important that this is a method and thus the predicate gets recreated
          // otherwise filteredlist won't react to the new predicate and stays the same
          filteredList.setPredicate((t: LogEntry) => {
            filterButtons(t.severity).isSelected
          })
        }
      })
      getItems.add(button)
      ls -> button
    }).toMap
  }

}
