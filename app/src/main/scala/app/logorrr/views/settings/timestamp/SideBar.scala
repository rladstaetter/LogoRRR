package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.model.{BoundId, IntStringBinding}
import javafx.beans.property.{ObjectProperty, ObjectPropertyBase}
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.VBox

class SideBar extends VBox(15):
  private val startColLabel = new FromLabel
  private val startColTf = new FromTextField

  private val endColLabel = new ToLabel
  private val endColTf = new ToTextField

  private val boundIdControls: Seq[? <: BoundId] =
    Seq(startColLabel, startColTf, endColLabel, endColTf)

  private val rangeLabel =
    new Label("1. Define Column Range"):
      setStyle("-fx-font-weight: bold;")

  private val fromBox = new VBox(5, startColLabel, startColTf)
  private val toBox = new VBox(5, endColLabel, endColTf)

  setPadding(new Insets(15))
  setPrefWidth(200)
  setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-border-width: 0 1 0 0;")

  val description = new Label("Click on the preview text\nto select columns.")
  getChildren.addAll(rangeLabel, fromBox, toBox, description)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , startColProperty: ObjectProperty[java.lang.Integer]
           , endColProperty: ObjectProperty[java.lang.Integer]
          ): Unit =
    boundIdControls.foreach(_.bindIdProperty(fileIdProperty))
    startColTf.textProperty().bind(new IntStringBinding(startColProperty))
    endColTf.textProperty().bind(new IntStringBinding(endColProperty))


  def shutdown(): Unit =
    startColTf.textProperty.unbind()
    endColTf.textProperty.unbind()
    boundIdControls.foreach(_.unbindIdProperty())

