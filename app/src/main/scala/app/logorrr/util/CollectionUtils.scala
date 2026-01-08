package app.logorrr.util

import javafx.collections.{FXCollections, ObservableList}

import java.util

object CollectionUtils:
  def mkEmptyObservableList[T](): ObservableList[T] =
    val a = new util.ArrayList[T]()
    FXCollections.observableList(a)
