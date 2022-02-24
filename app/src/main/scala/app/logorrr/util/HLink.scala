package app.logorrr.util

import javafx.application.HostServices
import javafx.scene.control.Hyperlink

import java.net.URL

object HLink {

  def apply(url: String, description: String): HLink = {
    apply(new URL(url), description)
  }

}

case class HLink(url: URL
                 , description: String) {

  def mkHyperLink(hostServices: HostServices): Hyperlink = {
    val hyperlink = new Hyperlink(description)
    hyperlink.setOnAction(_ => hostServices.showDocument(url.toString))
    hyperlink
  }
}