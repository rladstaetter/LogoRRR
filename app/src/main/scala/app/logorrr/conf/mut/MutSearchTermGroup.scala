package app.logorrr.conf.mut

import app.logorrr.conf.{SearchTerm, SearchTermGroup}
import javafx.beans.property.{SimpleBooleanProperty, SimpleListProperty}
import javafx.collections.{FXCollections, ObservableList}

import scala.jdk.CollectionConverters.*

object MutSearchTermGroup:
  /** Constructor that acts as a deserializer from the case class */
  def apply(stg: SearchTermGroup): MutSearchTermGroup =
    val mut = new MutSearchTermGroup()
    mut.termsProperty.setAll(stg.terms.asJava)
    mut.selectedProperty.set(stg.selected)
    mut

class MutSearchTermGroup:

  val termsProperty = FXCollections.observableArrayList[SearchTerm]()

  val selectedProperty = new SimpleBooleanProperty(false)

  def isSelected: Boolean = selectedProperty.get()

  def setSelected(value: Boolean): Unit = selectedProperty.set(value)

  /** Converts the mutable property state back into an immutable case class */
  def mkImmutable(): SearchTermGroup =
    SearchTermGroup(termsProperty.asScala.toSeq, selectedProperty.get())