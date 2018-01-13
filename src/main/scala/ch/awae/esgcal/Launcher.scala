package ch.awae.esgcal

import javax.swing.UIManager
import ch.awae.esgcal.ui.GUI

object Launcher extends App {

  UIManager setLookAndFeel UIManager.getSystemLookAndFeelClassName
  new GUI().init

}