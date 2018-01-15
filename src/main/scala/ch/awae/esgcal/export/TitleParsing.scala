package ch.awae.esgcal.export

object TitleParsing {
  type Parser = PartialFunction[(Calendar, String), (Symbol, String)]
  type Merger = PartialFunction[((Symbol, String), (Symbol, String)), (Symbol, String)]
}

trait TitleParser {
  val parser: TitleParsing.Parser
  val merger: TitleParsing.Merger
}
