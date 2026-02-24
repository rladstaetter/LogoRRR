package app.logorrr.conf

import app.logorrr.conf.mut.MutSearchTermGroup
import javafx.beans.Observable
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.util.Callback

import scala.jdk.CollectionConverters.*

class MutSearchTermGroupSettings:

  val searchTermGroupEntries: SimpleListProperty[MutSearchTermGroup] =
    new SimpleListProperty[MutSearchTermGroup](FXCollections.observableArrayList[MutSearchTermGroup]())

  def setSelected(target: MutSearchTermGroup): Unit = searchTermGroupEntries.forEach(g => g.setSelected(g == target))

  def mkImmutable(): Seq[SearchTermGroup] = searchTermGroupEntries.asScala.map(_.mkImmutable()).toSeq

  def add(s: MutSearchTermGroup): Unit = searchTermGroupEntries.add(s)

  def remove(s: MutSearchTermGroup): Boolean = searchTermGroupEntries.remove(s)

  def clear(): Unit = searchTermGroupEntries.clear()
