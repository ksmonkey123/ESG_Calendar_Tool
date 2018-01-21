package ch.awae.esgcal.scene

import com.google.api.client.auth.oauth2.Credential

import ch.awae.esgcal.export.parser._Types.Ganztag
import ch.awae.esgcal.export.parser._Types.Jahresplan
import ch.awae.esgcal.export.parser._Types.JahresplanBE
import ch.awae.esgcal.ui.Button
import ch.awae.esgcal.ui.Navigation
import ch.awae.esgcal.ui.Navigation.LEFT
import ch.awae.esgcal.ui.Navigation.RIGHT
import ch.awae.esgcal.ui.Scene
import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.export.parser._Types.JahresplanZH

case class ExportScene(credential: Credential) extends Scene {

  val navigation = Navigation("Abbrechen", null) {
    case (LEFT, b)  => pop
    case (RIGHT, b) => ???
  }

  val panel =
    vertical(
      label("Export"),
      glue,
      vcenter(
        center(button("Jahresplan ESG", doJahresplan('ESG))), gap(30),
        center(button("Jahresplan Bern", doJahresplan('BE))), gap(30),
        center(button("Jahresplan Zürich", doJahresplan('ZH))), gap(30),
        center(button("Ganztägige Termine", doFullDay))),
      glue,
      navigation.panel)

  def doJahresplan(s: Symbol)(b: Button) = push(
    ExportYearSelection(credential, s match {
      case 'ESG => Jahresplan
      case 'BE  => JahresplanBE
      case 'ZH  => JahresplanZH
    }))

  def doFullDay(b: Button) = push(
    ExportDateSelection(credential, Ganztag))

}