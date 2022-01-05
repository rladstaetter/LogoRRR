package app.logorrr.views.about

import app.logorrr.LogoRRRFonts
import app.logorrr.conf.Settings
import app.logorrr.util.{HLink, ImageCp}
import javafx.application.HostServices
import javafx.geometry.Insets
import javafx.scene.control._
import javafx.scene.image.ImageView
import javafx.scene.layout.{BorderPane, VBox}


object AboutScreen {

  val logo = ImageCp("/app/logorrr/icon/logorrr-icon-128.png", 128, 128)

  val links = Seq(
    HLink("https://www.logorrr.app", "LogoRRR Homepage")
    , HLink("https://github.com/rladstaetter/LogoRRR/", "Github Page (Source code)")
    , HLink("https://www.buymeacoffee.com/rladstaetter/", "Donate a coffee to speed up development️"))

  case class MonoLabel(text: String, size: Int) extends Label(text) {
    setStyle(LogoRRRFonts.jetBrainsMono(size))
  }

  class HLinkView(hostServices: HostServices, links: Seq[HLink]) extends VBox {
    setPrefWidth(400)
    setPadding(new Insets(30, 20, 20, 20))

    def mkHyperLink(hlink: HLink): Hyperlink = {
      val hyperlink = new Hyperlink(hlink.text)
      hyperlink.setOnAction(e => hostServices.showDocument(hlink.url.toString))
      hyperlink
    }

    links.foreach(l => getChildren.add(mkHyperLink(l)))

  }
}


class AboutScreen(hostServices: HostServices) extends BorderPane {

  private def mkLogo(): ImageView = AboutScreen.logo.imageView()

  private def mkHeader(): Label = AboutScreen.MonoLabel(Settings.fullAppName, 50)

  setPadding(new Insets(10, 10, 10, 10))
  setTop(mkHeader())
  setLeft(mkLogo())
  setRight(new AboutScreen.HLinkView(hostServices, AboutScreen.links))

}
