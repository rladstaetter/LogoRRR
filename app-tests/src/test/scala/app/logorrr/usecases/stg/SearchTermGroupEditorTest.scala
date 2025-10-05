package app.logorrr.usecases.stg

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.EmptyFileIdService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.{CanStartApplication, VisibleItemActions}
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.UiNodes
import app.logorrr.views.about.AboutDialog
import org.junit.jupiter.api.Test

class SearchTermGroupEditorTest extends TestFxBaseApplicationTest
  with CanStartApplication
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
    waitAndClickVisibleItem(UiNodes.HelpMenu.Self)
    waitAndClickVisibleItem(UiNodes.HelpMenu.About)

    waitAndClickVisibleItem(UiNodes.AboutDialogOpenLogorrrMainSite)
    waitAndClickVisibleItem(UiNodes.AboutDialogOpenDevelopmentBlog)
    waitAndClickVisibleItem(UiNodes.AboutDialogOpenIssuePage)
    waitAndClickVisibleItem(UiNodes.AboutDialogCloseButton)

    assert(AboutDialog.links.map(_.url.toString).forall(u => mockHostServices.visitedUrls.contains(u)))
  }

}