package app.logorrr.views.text

import app.logorrr.model.LogEntry
import javafx.collections.transformation.FilteredList
import jfx.incubator.scene.control.richtext.TextPos
import jfx.incubator.scene.control.richtext.model.BasicTextModel.Content
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap

class LogEntryContent(filteredList: FilteredList[LogEntry]) extends Content:

  override def size(): Int = filteredList.size()

  override def getText(index: Int): String =
    if index >= 0 && index < filteredList.size() then
      filteredList.get(index).value
    else ""

  override def insertTextSegment(index: Int, offset: Int, text: String, styleAttributeMap: StyleAttributeMap): Int = 0

  override def insertLineBreak(index: Int, offset: Int): Unit = ()

  override def removeRange(start: TextPos, end: TextPos): Unit = ()

  override def isWritable: Boolean = false
