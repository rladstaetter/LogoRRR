package app.logorrr.views

import app.logorrr.util.CanLog
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.control.{TextField, ToggleButton}
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}

/**
 * LogoRRR supports some keyboard shortcuts to make life easier for the user.
 *
 * <ul>
 *   <li>CTRL-F/CMD-F : set focus to search</li>
 * </ul>
 *
 * Object is accessed directly/globally since it makes life much easier ...
 */
object LogoRRRAccelerators extends CanLog {

  val activeSearchTextField = new SimpleObjectProperty[TextField]()
  val activeRegexToggleButton = new SimpleObjectProperty[ToggleButton]()

  def getActiveSearchTextField: TextField = activeSearchTextField.get()
  def getActiveRegexToggleButton: ToggleButton = activeRegexToggleButton.get()

  def setActiveSearchTextField(textField: TextField): Unit = activeSearchTextField.set(textField)

  def setActiveRegexToggleButton(toggleButton: ToggleButton): Unit = activeRegexToggleButton.set(toggleButton)


  /**
   * CTRL-F (windows) or META/COMMAND-F (mac)
   *
   * sets focus to search textfield
   * */
  val shortCutF = new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN)

  val shortCutR = new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN)

  /**
   * Installs accelerators to the scene.
   *
   * The application is responsible to set the correct 'active' ui elements such that keybindings work as expected.
   *
   * For example, if the user switch between different tabs it is expected to set focus on the correct search field.
   * */
  def initAccelerators(scene: Scene): Unit = {

    scene.getAccelerators.put(shortCutF, () => {
      Option(getActiveSearchTextField) match {
        case Some(tf) => tf.requestFocus()
        case None => // logTrace("no textfield active")
      }
    })
    scene.getAccelerators.put(shortCutR, () => {
      Option(getActiveRegexToggleButton) match {
        case Some(regexToggleButton) =>
          if (regexToggleButton.isSelected) {
            regexToggleButton.setSelected(false)
          } else {
            regexToggleButton.setSelected(true)
          }
        case None => // logTrace("no regex togglebutton active")
      }
    })

  }

}
