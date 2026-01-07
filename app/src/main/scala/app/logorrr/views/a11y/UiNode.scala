package app.logorrr.views.a11y

import app.logorrr.conf.FileId
import app.logorrr.util.HashUtil
import javafx.geometry.Pos

/**
 * Used for UI tests - every element which should be reachable for an UI test has to create its own unique instance
 * of this class.
 */
object UiNode {

  def apply(fileId: FileId, pos: Pos, clazz: Class[?]): UiNode = {
    UiNode(s"${clazz.getSimpleName}-${pos.toString}-${HashUtil.md5Sum(fileId)}")
  }

  def apply(fileId: FileId, clazz: Class[?]): UiNode = {
    UiNode(clazz.getSimpleName + "-" + HashUtil.md5Sum(fileId))
  }

}

case class UiNode(value: String) {

  lazy val ref: String = "#" + value

}
