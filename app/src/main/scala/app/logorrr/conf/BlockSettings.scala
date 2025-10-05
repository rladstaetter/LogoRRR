package app.logorrr.conf

import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

object BlockSettings {

  implicit lazy val reader: ConfigReader[BlockSettings] = deriveReader[BlockSettings]
  implicit lazy val writer: ConfigWriter[BlockSettings] = deriveWriter[BlockSettings]

}

/**
 * Represents all settings regarding to blocks
 *
 * @param size size of blocks
 */
case class BlockSettings(size: Int)
