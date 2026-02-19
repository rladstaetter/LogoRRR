package app.logorrr.conf

import app.logorrr.conf.mut.MutSearchTermGroup
import javafx.collections.{FXCollections, ObservableList}

import scala.jdk.CollectionConverters.*

class MutSearchTermGroupSettings:

  val searchTermGroupEntries: ObservableList[MutSearchTermGroup] = FXCollections.observableArrayList[MutSearchTermGroup]()

  def setSelected(target: MutSearchTermGroup): Unit =
    searchTermGroupEntries.forEach(g => g.setSelected(g == target))

  def mkImmutable(): Seq[SearchTermGroup] = searchTermGroupEntries.asScala.map(_.mkImmutable()).toSeq

  def add(s: MutSearchTermGroup): Unit = searchTermGroupEntries.add(s)

  def remove(s: MutSearchTermGroup) = searchTermGroupEntries.remove(s)

  def clear(): Unit = searchTermGroupEntries.clear()
