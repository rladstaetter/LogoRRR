package app.logorrr.views.text

import app.logorrr.model.LogEntry
import javafx.collections.transformation.FilteredList
import jfx.incubator.scene.control.richtext.TextPos
import jfx.incubator.scene.control.richtext.model.BasicTextModel.Content
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap

class LogEntryContent(filteredList: FilteredList[LogEntry]) extends Content:

  override def size(): Int = filteredList.size()

  override def getText(index: Int): String =
    if (index >= 0 && index < filteredList.size()) {
      filteredList.get(index).value
    } else ""

  override def insertTextSegment(i: Int, i1: Int, s: String, styleAttributeMap: StyleAttributeMap): Int = 0

  override def insertLineBreak(i: Int, i1: Int): Unit = ()

  override def removeRange(textPos: TextPos, textPos1: TextPos): Unit = ()

  override def isWritable: Boolean = false
