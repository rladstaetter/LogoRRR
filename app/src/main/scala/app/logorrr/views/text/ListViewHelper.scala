package app.logorrr.views.text

import app.logorrr.model.LogEntry
import javafx.scene.control.skin.{ListViewSkin, VirtualFlow}
import javafx.scene.control.{IndexedCell, ListView, ScrollBar}

object ListViewHelper {
  /**
   * helper function to deduce the position of the textview
   *
   * @param listView listview to analyze
   * @return
   */
  def getVisibleRange(listView: ListView[LogEntry]): (Int,Int) = {
    val skin = listView.getSkin.asInstanceOf[ListViewSkin[LogEntry]]
    val flow = skin.getChildren.get(0).asInstanceOf[VirtualFlow[_ <: IndexedCell[LogEntry]]]
    val firstVisibleIndex = Option(flow.getFirstVisibleCell.getGraphic).map(_.asInstanceOf[LogTextViewLabel].e.lineNumber).getOrElse(-1)
    val lastVisibleIndex = Option(flow.getLastVisibleCell.getGraphic).map(_.asInstanceOf[LogTextViewLabel].e.lineNumber).getOrElse(-1)
    (firstVisibleIndex,lastVisibleIndex)
  }

  def findScrollBar[T](listView: ListView[T]): Option[ScrollBar] = Option(listView.lookup(".scroll-bar:vertical").asInstanceOf[ScrollBar])

}
