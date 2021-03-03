package net.ladstatt.logboard.views

import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{ToggleButton, ToolBar}
import net.ladstatt.logboard.{LogEntry, LogSeverity}

import java.text.DecimalFormat
import java.util
import java.util.function.Predicate

/** A toolbar with buttons which filter log events */
object FilterButtonsToolBar {

  val percentFormatter = new DecimalFormat("#.##")

  def percentAsString(ls: LogSeverity
                      , occurences: util.HashMap[LogSeverity, Long]
                      , size: Int): String = {
    percentFormatter.format((100 * occurences.get(ls).toDouble) / size.toDouble) + "%"
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
                           , occurences: util.HashMap[LogSeverity, Long]
                           , size: Int) extends ToolBar {

  val filterButtons: util.HashMap[LogSeverity, ToggleButton] = {
    val m = new util.HashMap[LogSeverity, ToggleButton]()
    LogSeverity.seq.foreach((ls: LogSeverity) => {
      val button = new ToggleButton(ls.name + ": " + occurences.get(ls) + " " + FilterButtonsToolBar.percentAsString(ls, occurences, size))
      button.setSelected(true)
      button.selectedProperty().addListener(new InvalidationListener {
        // if any of the buttons changes its selected value, reevaluate predicate
        // and thus change contents of all views which display filtered List
        override def invalidated(observable: Observable): Unit = {
          // it is important that this is a method and thus the predicate gets recreated
          // otherwise filteredlist won't react to the new predicate and stays the same
          filteredList.setPredicate(new Predicate[LogEntry] {
            override def test(t: LogEntry): Boolean = {
              filterButtons.get(t.severity).isSelected
            }
          })
        }
      })
      m.put(ls, button)
    })
    m
  }

  //  add buttons to self (ToolBar)
  getItems.addAll(filterButtons.values)

}
