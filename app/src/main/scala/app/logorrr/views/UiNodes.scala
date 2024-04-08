package app.logorrr.views

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil

object UiNode {

  def apply(fileId: FileId, clazz: Class[_]): UiNode = {
    UiNode(clazz.getSimpleName + "-" + HashUtil.md5Sum(fileId))
  }

}

case class UiNode(value: String) {

  lazy val ref: String = "#" + value

}

object UiNodes {

  /** id for the file menu */
  val FileMenu: UiNode = UiNode("file_menu")

  /** ID of menu item which opens a file */
  val FileMenuOpenFile: UiNode = UiNode("file_menu_open_file")

  /**
   * close all files
   */
  val FileMenuCloseAll: UiNode = UiNode("file_menu_close_all")

  /**
   * ID of main tab pane where all log files are placed
   */
  val MainTabPane: UiNode = UiNode("main_tab_pane")

  /** yields all tab 'cards' which are available */
  val LogFileHeaderTabs = s"${UiNodes.MainTabPane.ref} > .tab-header-area > .headers-region > .tab"

  /** css rule to target the close button of a LogFileHeaderTab */
  val LogFileHeaderTabCloseButton = ".tab-container > .tab-close-button"


}
