package app.logorrr.views.about

import app.logorrr.meta.AppInfo
import app.logorrr.util.{HLink, ImageCp, LogoRRRFonts}
import app.logorrr.views.a11y.UiNodes
import javafx.geometry.{Insets, Pos}
import javafx.scene.control._
import javafx.scene.layout.{BorderPane, HBox, VBox}
import javafx.stage.Stage


object AboutDialogBorderPane {

  val logo: ImageCp = ImageCp("/app/logorrr/icon/logorrr-icon-128.png", 128, 128)

  lazy val links: Seq[HLink] = Seq(
    HLink(UiNodes.AboutDialogOpenLogorrrMainSite, "https://www.logorrr.app/", "https://www.logorrr.app/")
    , HLink(UiNodes.AboutDialogOpenDevelopmentBlog, "https://www.logorrr.app/posts/index.html", "Development blog")
    , HLink(UiNodes.AboutDialogOpenIssuePage, "https://github.com/rladstaetter/LogoRRR/issues/", "Request a feature or report a bug"))

  case class MonoLabel(text: String, size: Int) extends Label(text) {
    setStyle(LogoRRRFonts.jetBrainsMono(size))
  }

  class HLinkView(links: Seq[HLink]) extends VBox {
    setAlignment(Pos.CENTER_LEFT)
    setSpacing(10)
    setPrefWidth(400)
    setPadding(new Insets(30, 20, 20, 20))

    links.foreach(l => getChildren.add(l.mkHyperLink()))

  }

}


class AboutDialogBorderPane(stage: Stage) extends BorderPane {

  private val closeButton: Button = {
    val b = new Button("", AboutDialogBorderPane.logo.imageView())
    b.setId(UiNodes.AboutDialogCloseButton.value)
    b.setOnAction(_ => stage.close())
    b
  }

  setPadding(new Insets(10, 10, 10, 10))
  setTop(AboutDialogBorderPane.MonoLabel(AppInfo.fullAppNameWithVersion, 50))
  setLeft(closeButton)
  setRight(new AboutDialogBorderPane.HLinkView(AboutDialogBorderPane.links))

  val hBox = new HBox()
  hBox.setAlignment(Pos.CENTER_LEFT)
  hBox.getChildren.add(new Label(BuildProps.Instance.timestamp + " " + BuildProps.Instance.githash))

  setBottom(hBox)

}
