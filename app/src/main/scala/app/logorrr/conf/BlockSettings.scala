package app.logorrr.conf

import pureconfig.{ConfigReader, ConfigWriter}
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object BlockSettings {

  implicit lazy val reader: ConfigReader[BlockSettings] = deriveReader[BlockSettings]
  implicit lazy val writer: ConfigWriter[BlockSettings] = deriveWriter[BlockSettings]

}
/**
 * @param size size of blocks
 */
case class BlockSettings(size: Int)
