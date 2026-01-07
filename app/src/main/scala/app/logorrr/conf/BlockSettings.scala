package app.logorrr.conf

import upickle.default.*

object BlockSettings {

}

/**
 * Represents all settings regarding to blocks
 *
 * @param size size of blocks
 */
case class BlockSettings(size: Int) derives ReadWriter