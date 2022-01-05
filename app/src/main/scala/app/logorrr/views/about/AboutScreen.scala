package app.logorrr.views.about

import app.logorrr.LogoRRRFonts
import app.logorrr.conf.Settings
import app.logorrr.util.{HLink, ImageCp}
import javafx.application.HostServices
import javafx.collections.FXCollections
import javafx.scene.control._
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane

import scala.jdk.CollectionConverters._


object AboutScreen {

  val logo = ImageCp("/app/logorr/icon/logorrr-icon-512.png", 512, 512)

  val links = Seq(
    HLink("https://www.logorrr.app", "LogoRRR Homepage")
    , HLink("https://github.com/rladstaetter/LogoRRR/", "Github Page (Source code)")
    , HLink("https://www.buymeacoffee.com/rladstaetter/", "Donate a coffee to speed up development."))

}

class HLinkView(hostServices: HostServices, links: Seq[HLink]) extends ListView[HLink] {
  getItems.addAll(FXCollections.observableArrayList(links.asJava))

  setCellFactory(_ => {
    new ListCell[HLink] {
      setContentDisplay(ContentDisplay.GRAPHIC_ONLY)
      val hyperlink = new Hyperlink()
      hyperlink.setOnAction(_ => {
        Option(getItem()).foreach(i => hostServices.showDocument(i.url.toString))
      })

      override def updateItem(item: HLink, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (Option(item).isDefined && !empty) {
          hyperlink.setText(item.toString)
          setGraphic(hyperlink)
        } else setGraphic(null)
      }
    }
  })

}

case class MonoLabel(text: String, size: Int) extends Label(text) {
  setStyle(LogoRRRFonts.jetBrainsMono(size))
}

class AboutScreen(hostServices: HostServices) extends BorderPane() {

  private def mkLogo(): ImageView = AboutScreen.logo.imageView()

  private def mkHeader(): Label = MonoLabel(s"${Settings.meta.appName} ${Settings.meta.appVersion}", 130)

  setTop(mkHeader())
  setLeft(mkLogo())
  setRight(new HLinkView(hostServices, AboutScreen.links))

}
