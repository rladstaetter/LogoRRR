package app.logorrr.views.settings.timestamp

import javafx.scene.layout.{HBox, Region}

class AlwaysGrowHorizontalRegion extends Region:
  HBox.setHgrow(this, javafx.scene.layout.Priority.ALWAYS)
