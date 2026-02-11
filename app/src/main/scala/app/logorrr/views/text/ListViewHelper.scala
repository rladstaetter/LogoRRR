package app.logorrr.views.text

import app.logorrr.model.LogEntry
import javafx.scene.control.skin.{ListViewSkin, VirtualFlow}
import javafx.scene.control.{IndexedCell, ListView, ScrollBar}

object ListViewHelper:
  /**
   * helper function to deduce the position of the textview
   *
   * @param listView listview to analyze
   * @return
   */
  def getVisibleRange(listView: ListView[LogEntry]): (Int, Int) =
    val skin = listView.getSkin.asInstanceOf[ListViewSkin[LogEntry]]
    val flow = skin.getChildren.get(0).asInstanceOf[VirtualFlow[? <: IndexedCell[LogEntry]]]
    val firstVisibleIndex =
      (for fvc <- Option(flow.getFirstVisibleCell)
            g <- Option(fvc.getGraphic) yield g.asInstanceOf[LogTextViewLabel].lineNumber).getOrElse(-1)

    val lastVisibleIndex =
      (for lvc <- Option(flow.getLastVisibleCell)
            g <- Option(lvc.getGraphic) yield g.asInstanceOf[LogTextViewLabel].lineNumber).getOrElse(-1)

    (firstVisibleIndex, lastVisibleIndex)

  def findScrollBar[T](listView: ListView[T]): Option[ScrollBar] = Option(listView.lookup(".scroll-bar:vertical").asInstanceOf[ScrollBar])

