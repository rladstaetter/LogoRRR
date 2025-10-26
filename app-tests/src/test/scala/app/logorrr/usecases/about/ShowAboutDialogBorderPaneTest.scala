package app.logorrr.usecases.about

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.EmptyFileIdService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.{AppActions, VisibleItemActions}
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.uinodes.{AboutDialog, LogoRRRMenu}
import app.logorrr.views.about.AboutDialogBorderPane
import org.junit.jupiter.api.Test


class ShowAboutDialogBorderPaneTest extends TestFxBaseApplicationTest
  with AppActions
  with VisibleItemActions {

  // to get a handle to clicked urls
  val mockHostServices = new MockHostServices

  final def services: LogoRRRServices = {
    LogoRRRServices(Settings.Default
      , mockHostServices
      , new EmptyFileIdService
      , isUnderTest = true)
  }


  @Test def showAboutDialog(): Unit = {
    waitAndClickVisibleItem(LogoRRRMenu.Self)
    waitAndClickVisibleItem(LogoRRRMenu.About)

    waitAndClickVisibleItem(AboutDialog.AboutDialogOpenLogorrrMainSite)
    waitAndClickVisibleItem(AboutDialog.AboutDialogOpenDevelopmentBlog)
    waitAndClickVisibleItem(AboutDialog.AboutDialogOpenIssuePage)
    waitAndClickVisibleItem(AboutDialog.AboutDialogCloseButton)

    assert(AboutDialogBorderPane.links.map(_.url.toString).forall(u => mockHostServices.visitedUrls.contains(u)))
  }

}