package app.logorrr.usecases.about

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.EmptyFileService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.{CanStartApplication, VisibleItemActions}
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.UiNodes
import app.logorrr.views.about.AboutScreen
import org.junit.jupiter.api.Test


class ShowAboutDialogTest extends TestFxBaseApplicationTest
  with CanStartApplication
  with VisibleItemActions {

  // to get a handle to clicked urls
  val mockHostServices = new MockHostServices

  final def services: LogoRRRServices = {
    LogoRRRServices(Settings.Default
      , mockHostServices
      , new EmptyFileService
      , isUnderTest = true)
  }


  @Test def showAboutDialog(): Unit = {
    waitAndClickVisibleItem(UiNodes.HelpMenu)
    waitAndClickVisibleItem(UiNodes.HelpMenuAbout)

    waitAndClickVisibleItem(UiNodes.AboutDialogOpenLogorrrMainSite)
    waitAndClickVisibleItem(UiNodes.AboutDialogOpenDevelopmentBlog)
    waitAndClickVisibleItem(UiNodes.AboutDialogOpenIssuePage)

    assert(AboutScreen.links.map(_.url.toString).forall(u => mockHostServices.visitedUrls.contains(u)))
  }

}