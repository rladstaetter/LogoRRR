package app.logorrr.views.about

import app.logorrr.meta.AppMeta
import app.logorrr.util.{HLink, ImageCp, LogoRRRFonts}
import javafx.geometry.Insets
import javafx.scene.control._
import javafx.scene.image.ImageView
import javafx.scene.layout.{BorderPane, VBox}


object AboutScreen {

  val logo = ImageCp("/app/logorrr/icon/logorrr-icon-128.png", 128, 128)

  val links = Seq(
    HLink("https://www.logorrr.app/", "LogoRRR Homepage")
    , HLink("https://www.twitter.com/logorrr/", "LogoRRR on twitter")
    , HLink("https://www.github.com/rladstaetter/LogoRRR/", "LogoRRR on github (source code)")
    , HLink("https://www.buymeacoffee.com/rladstaetter/", "Support LogoRRR via buymeacoffee.com"))

  case class MonoLabel(text: String, size: Int) extends Label(text) {
    setStyle(LogoRRRFonts.jetBrainsMono(size))
  }

  class HLinkView(links: Seq[HLink]) extends VBox {
    setPrefWidth(400)
    setPadding(new Insets(30, 20, 20, 20))

    links.foreach(l => getChildren.add(l.mkHyperLink()))

  }

}


class AboutScreen extends BorderPane {

  private def mkLogo(): ImageView = AboutScreen.logo.imageView()

  private def mkHeader(): Label = AboutScreen.MonoLabel(AppMeta.fullAppNameWithVersion, 50)

  setPadding(new Insets(10, 10, 10, 10))
  setTop(mkHeader())
  setLeft(mkLogo())
  setRight(new AboutScreen.HLinkView(AboutScreen.links))

}
