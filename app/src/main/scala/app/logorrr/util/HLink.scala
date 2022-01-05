package app.logorrr.util

import java.net.URL

object HLink {

  def apply(url: String, description: String): HLink = {
    apply(url, new URL(url), description)
  }

}

case class HLink(name: String
                 , url: URL
                 , description: String) {

  val text = s"$description"

}