package app.logorrr.conf

import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object BlockSettings {

  implicit lazy val reader = deriveReader[BlockSettings]
  implicit lazy val writer = deriveWriter[BlockSettings]

}
/**
 * @param width size of blocks
 */
case class BlockSettings(width: Int)
