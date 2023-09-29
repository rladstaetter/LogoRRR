package app.logorrr.util

import app.logorrr.conf.LogoRRRGlobals
import javafx.scene.control.Hyperlink

import java.net.{URI, URL}

object HLink {

  def apply(url: String, description: String): HLink = {
    apply(URI.create(url).toURL, description)
  }

}

case class HLink(url: URL
                 , description: String) {

  def mkHyperLink(): Hyperlink = {
    val hyperlink = new Hyperlink(description)
    hyperlink.setOnAction(_ => LogoRRRGlobals.getHostServices.showDocument(url.toString))
    hyperlink
  }
}