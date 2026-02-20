package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.scene.control.ListView
import javafx.scene.paint.Color

import scala.jdk.CollectionConverters._

class MutableSearchTermListener(mutableSearchTerms: FilteredList[MutableSearchTerm]
                                , targetPairs: ObservableList[(String, Color)]
                                , listview: ListView[LogEntry]
                               ) extends ListChangeListener[MutableSearchTerm]:
  override def onChanged(c: ListChangeListener.Change[? <: MutableSearchTerm]): Unit = {
    // println"fired")
    while (c.next()) {
      val list = FXCollections.observableArrayList[(String, Color)]()
      if (c.wasPermutated()) {
        // println"permutated")
      } else if (c.wasUpdated()) {
        // // println"updated")
        //update item
        c.getList.forEach(st => {
          // printlnst.getValue + " " + st.getColor + " " + st.isActive)
          if (st.isActive)
            list.add((st.getValue, st.getColor))
        })

        targetPairs.setAll(list)
      } else if (c.wasRemoved()) {
        c.getRemoved.forEach(st => list.add((st.getValue, st.getColor)))
        targetPairs.removeAll(list)
      } else if (c.wasAdded()) {
        // println"added")
        c.getAddedSubList.forEach(st => list.add((st.getValue, st.getColor)))
        // printlnlist.asScala)
        targetPairs.addAll(list)
      }
    }
    listview.refresh()
  }


