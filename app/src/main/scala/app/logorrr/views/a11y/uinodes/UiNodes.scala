package app.logorrr.views.a11y.uinodes

import app.logorrr.views.a11y.UiNode


/** uinodes which don't have their own namespace */
object UiNodes {

  /**
   * ID of main tab pane where all log files are placed
   */
  val MainTabPane: UiNode = UiNode("main_tab_pane")

  /** yields all tab 'cards' which are available */
  val LogFileHeaderTabs: String = s"${UiNodes.MainTabPane.ref} > .tab-header-area > .headers-region > .tab"

  /** css rule to target the close button of a LogFileHeaderTab */
  val LogFileHeaderTabCloseButton = ".tab-container > .tab-close-button"

  /** contains information about log formatter */
  val OpenDateFormatterSite: UiNode = UiNode("date_time_formatter_url")


}

