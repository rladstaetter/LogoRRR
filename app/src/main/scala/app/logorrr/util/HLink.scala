package app.logorrr.util

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.views.a11y.UiNode
import javafx.scene.control.Hyperlink

import java.net.{URI, URL}

object HLink {

  def apply(uiNode: UiNode
            , url: String
            , description: String): HLink = {
    apply(uiNode, URI.create(url).toURL, description)
  }

}

case class HLink(uiNode: UiNode
                 , url: URL
                 , description: String) {

  def mkHyperLink(): Hyperlink = {
    val hyperlink = new Hyperlink(description)
    hyperlink.setId(uiNode.value)
    hyperlink.setOnAction(_ => LogoRRRGlobals.getHostServices.showDocument(uiNode, url.toString))
    hyperlink
  }
}