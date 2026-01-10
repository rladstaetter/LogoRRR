package app.logorrr.views.about

import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.{AppInfo, LogoRRRGlobals}
import app.logorrr.cp.ImageCp
import app.logorrr.util.{HLink, LogoRRRFonts}
import app.logorrr.views.a11y.uinodes.AboutDialog
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.CycleMethod.NO_CYCLE
import javafx.scene.paint.{Color, LinearGradient, Stop}
import javafx.stage.Stage

object AboutDialogBorderPane:

  val logo: ImageCp = ImageCp("logorrr-icon-128.png", 128, 128)

  lazy val links: Seq[HLink] = Seq(
    HLink(AboutDialog.AboutDialogOpenLogorrrMainSite, "https://www.logorrr.app/", "https://www.logorrr.app/")
    , HLink(AboutDialog.AboutDialogOpenDevelopmentBlog, "https://www.logorrr.app/posts/index.html", "Development blog")
    , HLink(AboutDialog.AboutDialogOpenIssuePage, "https://github.com/rladstaetter/LogoRRR/issues/", "Request a feature or report a bug"))

  case class MonoLabel(text: String, size: Int) extends Label(text):
    setStyle(LogoRRRFonts.jetBrainsMono(size))

  class HLinkView(links: Seq[HLink]) extends VBox:
    setAlignment(Pos.CENTER_LEFT)
    setSpacing(10)
    setPrefWidth(400)
    setPadding(new Insets(30, 20, 20, 20))

    links.foreach(l => getChildren.add(l.mkHyperLink()))


class AboutDialogBorderPane(stage: Stage) extends BorderPane:

  private val showLogoButton: Button =
    val b = new Button("", AboutDialogBorderPane.logo.imageView())
    b.setOnAction:
      _ =>
        // open website
        LogoRRRGlobals.getHostServices.showDocument(AboutDialog.AboutDialogOpenLogorrrMainSite, AboutDialogBorderPane.links.head.url.toString)
    b

  private val closeButton: Button =
    val b = new Button("close")
    b.setId(AboutDialog.AboutDialogCloseButton.value)
    b.setOnAction:
      _ =>
        stage.close()
    b


  setPadding(new Insets(10, 10, 10, 10))
  setTop(AboutDialogBorderPane.MonoLabel(AppInfo.fullAppNameWithVersion, 50))
  setLeft(showLogoButton)
  setRight(new AboutDialogBorderPane.HLinkView(AboutDialogBorderPane.links))

  val hBox = new HBox()
  hBox.setAlignment(Pos.CENTER_LEFT)
  private val label = new Label(BuildProps.Instance.timestamp + " " + BuildProps.Instance.githash)
  val spacer: Region = new Region
  spacer.setMinWidth(10)
  hBox.getChildren.addAll(label, spacer, closeButton)

  setBottom(hBox)

