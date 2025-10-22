package app.logorrr.views.a11y

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import javafx.geometry.Pos

object UiNode {

  def apply(fileId: FileId, pos : Pos, clazz : Class[_]) : UiNode = {
    UiNode(s"${clazz.getSimpleName}-${pos.toString}-${HashUtil.md5Sum(fileId)}")
  }

  def apply(fileId: FileId, clazz: Class[_]): UiNode = {
    UiNode(clazz.getSimpleName + "-" + HashUtil.md5Sum(fileId))
  }

}

case class UiNode(value: String) {

  lazy val ref: String = "#" + value

}

object UiNodes {

  object LogoRRRMenu {

    val Settings: UiNode = UiNode("logorrr_menu_settings")

  }

  object FileMenu {

    /** id for the file menu */
    val Self: UiNode = UiNode("file_menu")

    /** ID of menu item which opens a file */
    val OpenFile: UiNode = UiNode("file_menu_open_file")

    /**
     * close all files
     */
    val CloseAll: UiNode = UiNode("file_menu_close_all")

    /** quit application */
    val CloseApplication: UiNode = UiNode("file_menu_close_application")

  }

  object HelpMenu {

    /** help menu */
    val Self: UiNode = UiNode("help_menu")

    /** help menu - about */
    val About: UiNode = UiNode("help_menu_about")

    /** help menu - open log */
    val OpenLogorrLog: UiNode = UiNode("help_menu_open_log")

  }


  /**
   * ID of main tab pane where all log files are placed
   */
  val MainTabPane: UiNode = UiNode("main_tab_pane")

  /** yields all tab 'cards' which are available */
  val LogFileHeaderTabs: String = s"${UiNodes.MainTabPane.ref} > .tab-header-area > .headers-region > .tab"

  /** css rule to target the close button of a LogFileHeaderTab */
  val LogFileHeaderTabCloseButton = ".tab-container > .tab-close-button"

  /** open logorrrs main site */
  val AboutDialogOpenLogorrrMainSite: UiNode = UiNode("about_stage_logorrr_app")

  /** have a look at the development blog */
  val AboutDialogOpenDevelopmentBlog: UiNode = UiNode("about_stage_devel_blog")

  /** report a bug or a feature request */
  val AboutDialogOpenIssuePage: UiNode = UiNode("about_stage_report_issues")

  /** logo embedded in a close button */
  val AboutDialogCloseButton: UiNode = UiNode("about_stage_close_button")

  /** contains information about log formatter */
  val OpenDateFormatterSite: UiNode = UiNode("date_time_formatter_url")
}
