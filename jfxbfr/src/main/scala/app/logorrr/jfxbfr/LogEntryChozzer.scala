package app.logorrr.jfxbfr

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList
import javafx.scene.paint.Color

import scala.jdk.CollectionConverters.CollectionHasAsScala

case class LogEntryChozzer(colorMatcherProperty: ObservableList[_ <: ColorMatcher]) extends ColorChozzer[LogEntry] {

  def colorMatcher: Seq[ColorMatcher] = Option(colorMatcherProperty).map(_.asScala.toSeq).getOrElse(Seq())

  override def calc(e: LogEntry): Color = ColorUtil.calcColor(e.value, colorMatcher)
}
