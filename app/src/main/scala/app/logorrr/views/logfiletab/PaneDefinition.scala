package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

case class PaneDefinition(calcId: SimpleObjectProperty[FileId] => String
                          , graphic: Node
                          , step: Int
                          , boundary: Int)
