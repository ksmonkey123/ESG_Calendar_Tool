package ch.awae.esgcal

import javax.swing.UIManager

object Launcher extends App {

  UIManager setLookAndFeel UIManager.getSystemLookAndFeelClassName
  new GUI().init

}