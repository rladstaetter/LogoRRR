package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.views.logfiletab.actions.*
import javafx.scene.control.{ContextMenu, MenuItem, TabPane}
import net.ladstatt.util.os.OsUtil

class LogFileTabContextMenu(fileId: FileId
                            , getTabPane: => TabPane
                            , logFileTab: => LogFileTab) extends ContextMenu:
  val closeMenuItem = new CloseTabMenuItem(fileId, logFileTab)
  val openInFinderMenuItem = new OpenInFinderMenuItem(fileId)

  val closeOtherFilesMenuItem = new CloseOtherFilesMenuItem(fileId, logFileTab)
  val closeAllFilesMenuItem = new CloseAllFilesMenuItem(fileId, logFileTab)

  val leftRightCloser =
    if getTabPane.getTabs.size() == 1 then
      Seq()
    // current tab is the first one, show only 'right'
    else if getTabPane.getTabs.indexOf(logFileTab) == 0 then
      Seq(new CloseRightFilesMenuItem(fileId, logFileTab))
    // we are at the end of the list
    else if getTabPane.getTabs.indexOf(logFileTab) == getTabPane.getTabs.size - 1 then
      Seq(new CloseLeftFilesMenuItem(fileId, logFileTab))
    // we are somewhere in between, show both options
    else
      Seq(new CloseLeftFilesMenuItem(fileId, logFileTab), new CloseRightFilesMenuItem(fileId, logFileTab))

  val items: Seq[MenuItem] =
    // special handling if there is only one tab
    if getTabPane.getTabs.size() == 1 then
      if OsUtil.isMac then
        Seq(closeMenuItem)
      else
        Seq(closeMenuItem, openInFinderMenuItem)
    else
      Seq(closeMenuItem
        , closeOtherFilesMenuItem
        , closeAllFilesMenuItem) ++ leftRightCloser ++ {
        if OsUtil.isMac then
          Seq()
        else
          Seq(openInFinderMenuItem)
      }
  getItems.addAll(items *)
