package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.views.logfiletab.actions.*
import javafx.scene.control.{ContextMenu, MenuItem, TabPane}
import net.ladstatt.util.os.OsUtil

class LogFileTabContextMenu(fileId: FileId
                            , tabPane: TabPane) extends ContextMenu:

  private val closeMenuItem = new CloseTabMenuItem(fileId, tabPane)
  private val openInFinderMenuItem = new OpenInFinderMenuItem(fileId, tabPane)
  private val closeOtherFilesMenuItem = new CloseOtherFilesMenuItem(fileId, tabPane)
  private val closeAllFilesMenuItem = new CloseAllFilesMenuItem(fileId, tabPane)
  private val logFileTab = tabPane.getSelectionModel.getSelectedItem
  private val leftRightCloser =
    if tabPane.getTabs.size() == 1 then
      Seq()
    // current tab is the first one, show only 'right'
    else if tabPane.getTabs.indexOf(logFileTab) == 0 then
      Seq(new CloseRightFilesMenuItem(fileId, tabPane))
    // we are at the end of the list
    else if tabPane.getTabs.indexOf(logFileTab) == tabPane.getTabs.size - 1 then
      Seq(new CloseLeftFilesMenuItem(fileId, tabPane))
    // we are somewhere in between, show both options
    else
      Seq(new CloseLeftFilesMenuItem(fileId, tabPane), new CloseRightFilesMenuItem(fileId, tabPane))

  private val items: Seq[MenuItem] =
    // special handling if there is only one tab
    if tabPane.getTabs.size() == 1 then
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
