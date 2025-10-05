package app.logorrr.views.about

import app.logorrr.meta.AppInfo
import app.logorrr.util.{HLink, ImageCp, LogoRRRFonts, PropsCp}
import app.logorrr.views.a11y.UiNodes
import javafx.geometry.{Insets, Pos}
import javafx.scene.control._
import javafx.scene.layout.{BorderPane, HBox, VBox}
import javafx.stage.{Stage, WindowEvent}

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}
import java.util.Properties


object AboutDialog {

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


object BuildProps {

  lazy val Instance = new BuildProps
}

class BuildProps {

  lazy val buildProps: Properties = PropsCp("/build.properties").asProperties(getClass)

  lazy val githash: String = buildProps.getProperty("revision")

  lazy val timestamp: String = {
    val PATTERN_FORMAT = "dd.MM.yyyy"
    val formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
      .withZone(ZoneId.systemDefault())
    val i = Instant.ofEpochMilli(buildProps.getProperty("timestamp").toLong)
    formatter.format(i)
  }

}

class AboutDialog extends BorderPane {

  private val closeButton: Button = {
    val b = new Button("", AboutDialog.logo.imageView())
    b.setId(UiNodes.AboutDialogCloseButton.value)
    b.setOnAction(_ => {
      val modalStage = getScene.getWindow.asInstanceOf[Stage]
      modalStage.fireEvent(new WindowEvent(modalStage, WindowEvent.WINDOW_CLOSE_REQUEST))
    })
    b
  }

  setPadding(new Insets(10, 10, 10, 10))
  setTop(AboutDialog.MonoLabel(AppInfo.fullAppNameWithVersion, 50))
  setLeft(closeButton)
  setRight(new AboutDialog.HLinkView(AboutDialog.links))

  val hBox = new HBox()
  hBox.setAlignment(Pos.CENTER_LEFT)
  hBox.getChildren.add(new Label(BuildProps.Instance.timestamp + " " + BuildProps.Instance.githash))

  setBottom(hBox)

}
