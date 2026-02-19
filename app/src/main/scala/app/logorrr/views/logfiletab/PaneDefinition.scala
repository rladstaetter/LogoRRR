package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.scene.Node

case class PaneDefinition(calcId: ObjectPropertyBase[FileId] => String
                          , graphic: Node
                          , step: Int
                          , boundary: Int)
