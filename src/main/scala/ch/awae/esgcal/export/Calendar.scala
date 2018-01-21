package ch.awae.esgcal.export

class Calendar(val sources: List[String]) {
  def this(src: String*) = this(src.toList)
}

object Calendar {
  case object Bern extends Calendar(
    "Berner Kantorei",
    "Berner Kantorei - Planung")
  case object Zürich extends Calendar(
    "Zürcher Kantorei",
    "Zürcher Kantorei - Planung")
  case object Gastvesper extends Calendar("Gastvespern")
  case object FerienBern extends Calendar("Ferien Bern")
  case object FerienZürich extends Calendar("Ferien Zürich")
  case object FerienML extends Calendar("Ferien Musikalischer Leiter")
  case object AbwesenheitenML extends Calendar("Abwesenheiten Musikalischer Leiter")
  case object Konzerte extends Calendar("Konzerte", "Konzerte - Planung")
}