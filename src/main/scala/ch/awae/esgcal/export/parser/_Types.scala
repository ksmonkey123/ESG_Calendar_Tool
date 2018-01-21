package ch.awae.esgcal.export.parser

sealed class _Type(val title: String)

object _Types {
  case object Ganztag extends _Type("Ganztätige Termine")
  case object Jahresplan extends _Type("Jahresplan")
  case object JahresplanBE extends _Type("Jahresplan Bern")
  case object JahresplanZH extends _Type("Jahresplan Zürich")
}