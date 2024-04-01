package app.logorrr.views

case class LogoRRRNode(value: String) {

  lazy val ref: String = "#" + value

}

object LogoRRRNodes {

  /** id for the file menu */
  val FileMenu: LogoRRRNode = LogoRRRNode("file_menu")

  /** ID of menu item which opens a file */
  val FileMenuOpenFile: LogoRRRNode = LogoRRRNode("file_menu_open_file")

  /**
   * close all files
   */
  val FileMenuCloseAll: LogoRRRNode = LogoRRRNode("file_menu_close_all")

  /**
   * ID of main tab pane where all log files are placed
   */
  val MainTabPane: LogoRRRNode = LogoRRRNode("main_tab_pane")

}
