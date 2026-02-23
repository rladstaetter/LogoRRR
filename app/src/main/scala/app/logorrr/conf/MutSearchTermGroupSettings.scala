package app.logorrr.conf

import app.logorrr.conf.mut.MutSearchTermGroup
import javafx.beans.property.{Property, SimpleBooleanProperty}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}

import scala.jdk.CollectionConverters.*

class MutSearchTermGroupSettings:

  val searchTermGroupEntries: ObservableList[MutSearchTermGroup] = FXCollections.observableArrayList[MutSearchTermGroup]()

  private val listDirtyPulse = new SimpleBooleanProperty(false)

  searchTermGroupEntries.addListener(new ListChangeListener[MutSearchTermGroup] {
    override def onChanged(c: ListChangeListener.Change[_ <: MutSearchTermGroup]): Unit = {
      // Flip the boolean to force an invalidation/change event
      listDirtyPulse.set(!listDirtyPulse.get())
    }
  })
  
  def setSelected(target: MutSearchTermGroup): Unit =
    searchTermGroupEntries.forEach(g => g.setSelected(g == target))

  def mkImmutable(): Seq[SearchTermGroup] = searchTermGroupEntries.asScala.map(_.mkImmutable()).toSeq

  def add(s: MutSearchTermGroup): Unit = searchTermGroupEntries.add(s)

  def remove(s: MutSearchTermGroup) = searchTermGroupEntries.remove(s)

  def clear(): Unit = searchTermGroupEntries.clear()

  val allProps: Set[Property[?]] = Set(listDirtyPulse)