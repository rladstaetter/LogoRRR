package app.logorrr.conf

import upickle.default._

object BlockSettings {

  implicit lazy val rw: ReadWriter[BlockSettings] = macroRW

}

/**
 * Represents all settings regarding to blocks
 *
 * @param size size of blocks
 */
case class BlockSettings(size: Int)