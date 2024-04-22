package app.logorrr

object LogoRRRAppLauncher {

  /** launcher for macos installer to circumvent module loading mechanism by javafx (dirty hack) */
  def main(args: Array[String]): Unit = {
    LogoRRRApp.main(args)
  }

}
