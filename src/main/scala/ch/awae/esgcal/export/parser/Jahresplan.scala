package ch.awae.esgcal.export.parser

import ch.awae.esgcal.export.EventParser
import ch.awae.esgcal.export.Calendar
import ch.awae.esgcal.export.Calendar._
import ch.awae.esgcal.export.Directive
import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.export.Entry
import java.time.LocalDate
import ch.awae.esgcal.xssf.Workbook
import java.time.chrono.IsoChronology
import ch.awae.esgcal.Month

object Jahresplan extends EventParser {

  type In = (Calendar, String)
  type Out = (Symbol, String)

  def calendars = List(Bern, Zürich, Gastvesper, AbwesenheitenML, FerienBern, FerienML, FerienZürich)

  val extractor: Extractor = {
    case (calendar, event) => calendar -> event.getSummary
  }

  val parser: Parser = {
    case (Bern, "Probe und Vesper")                       => 'Termin -> "na"
    case (Bern, "Probe und Kantatenvesper")               => 'Termin -> "na"
    case (Bern, "Abendprobe")                             => 'Termin -> "ab"
    case (Bern, "Offenes Singen")                         => 'Termin -> "off. Singen BE"
    case (_, "Ensemblesingen")                            => null
    case (_, x) if x endsWith "Registerprobe"             => null
    case (AbwesenheitenML, title)                         => 'Termin -> title
    case (Gastvesper, "Gastvesper")                       => 'Termin -> "GastV"
    case (Zürich, "Vesper")                               => 'Termin -> "zk V"
    case (Zürich, "Probe")                                => 'Termin -> "zk"
    case (Zürich, "Probe mit Vertretung")                 => 'Termin -> "zk Vertr."
    case (Zürich, "Jahresversammlung Zürich")             => 'Termin -> "JV ZH"
    case (Bern, "Jahresversammlung Bern")                 => 'Termin -> "JV BE"
    case (_, "Jahresversammlung ESG")                     => 'Termin -> "JV ESG"
    case (Zürich, "Gottesdienst")                         => 'Termin -> "GD ZH"
    case (Bern, "Probe und Vesper Chor 50+")              => 'Termin -> "50+"
    case (Bern, "Probe und Vesper Vertretung")            => 'Termin -> "na Vertr."
    case (_, "Probentag in Bern mit Vesper")              => 'Termin -> "PT BE + na"
    case (_, "Probentag in Bern")                         => 'Termin -> "PT BE"
    case (_, "Probentag in Zürich")                       => 'Termin -> "PT ZH"
    case (_, "Probentag in Zürich mit Gottesdienst")      => 'Termin -> "PT ZH + GD"
    case (_, "Generalprobe Zürich")                       => 'Termin -> "GP ZH"
    case (_, "Generalprobe Zürich mit Vesper")            => 'Termin -> "zk V + GP ZH"
    case (_, "Generalprobe Bern")                         => 'Termin -> "GP BE"
    case (_, "Passionsmusik Zürich")                      => 'Termin -> "PM ZH"
    case (_, "Passionsmusik Bern")                        => 'Termin -> "PM BE"
    case (_, "Abendmusik Bern")                           => 'Termin -> "AM BE"
    case (_, "Abendmusik Zürich")                         => 'Termin -> "AM ZH"
    case (_, "Weihnachtsmusik Bern")                      => 'Termin -> "WM BE"
    case (_, "Weihnachtsmusik Zürich")                    => 'Termin -> "WM ZH"
    case (_, "Herbstmusik Bern")                          => 'Termin -> "HM BE"
    case (_, "Herbstmusik Zürich")                        => 'Termin -> "HM ZH"
    case (Bern, "Gottesdienst")                           => 'Termin -> "GD BE"
    case (_, "Singreise")                                 => 'Termin -> "Singreise"
    case (_, "Singwochenende")                            => 'Termin -> "Sing WoE"
    case (Bern, "Probe und Vesper Junge Kantorei ad hoc") => 'Termin -> "na Junge"
    case (FerienBern, _)                                  => 'FerienBE -> "x"
    case (FerienZürich, _)                                => 'FerienZH -> "x"
    case (FerienML, _)                                    => 'FerienML -> "x"
    case (_, title)                                       => 'Termin -> title
  }

  val merger: Merger = {
    case (x, y) if x == y                                 => x
    case ((a, x), (b, y)) if (a == b) && (x startsWith y) => a -> x
    case ((a, x), (b, y)) if (a == b) && (x endsWith y)   => a -> x
    case ((a, x), (b, y)) if (a == b) && (y startsWith x) => a -> y
    case ((a, x), (b, y)) if (a == b) && (y endsWith x)   => a -> y
    case ((a, "ab"), (b, "na")) if a == b                 => a -> "na + ab"
    case ((a, "na"), (b, y)) if a == b                    => a -> s"na + $y"
    case ((a, "zk"), (b, "zk V")) if a == b               => a -> "zk V"
    case ((a, x), (b, "JV ZH")) if a == b                 => a -> s"$x + JV ZH"
    case ((a, x), (b, y)) if a == b                       => a -> s"$x / $y"
  }

  val director: Director = {
    case ('FerienZH, x) => new Directive(0, 0, x) :: Nil
    case ('FerienBE, x) => new Directive(0, 1, x) :: Nil
    case ('FerienML, x) => new Directive(0, 2, x) :: Nil
    case ('Termin, x)   => new Directive(0, 3, x) :: Nil
  }

  def getEntries(range: DateRange, directives: Directives): Entries = {
    val baseRow = 5 - range._1.toEpochDay
    val baseCol = 3
    // process each day
    val body = for {
      (date, dirs) <- directives
      if !date.isBefore(range._1)
      if !date.isAfter(range._2)
      row = date.toEpochDay + baseRow
      directive <- dirs
    } yield {
      directive.toEntry(row.toInt, baseCol)
    }
    val days = (if (range._1.isLeapYear) { 0 until 366 } else { 0 until 365 }).toList flatMap { i =>
      val date = LocalDate.of(range._1.getYear, 1, 1).plusDays(i)
      Entry(i + 5, 1, date.getDayOfWeek.getValue match {
        case 1 => "Mo"
        case 2 => "Di"
        case 3 => "Mi"
        case 4 => "Do"
        case 5 => "Fr"
        case 6 => "Sa"
        case 7 => "So"
      }) :: {
        if (date.getDayOfMonth == 1) {
          Entry(i + 5, 0, date.getMonthValue match {
            case 1  => "Januar"
            case 2  => "Februar"
            case 3  => "März"
            case 4  => "April"
            case 5  => "Mai"
            case 6  => "Juni"
            case 7  => "Juli"
            case 8  => "August"
            case 9  => "September"
            case 10 => "Oktober"
            case 11 => "November"
            case 12 => "Dezember"
          }) :: Nil
        } else
          Nil
      }
    }

    Entry(1, 19, range._1) :: body ::: days
  }

  def fillExcel(book: Workbook, entries: Entries, range: DateRange) = {
    // fill 'BasisTabelle'
    entries foreach { e => book("Basistabelle").process(e) }
    // fill 'YearTable'
    val sheet = book("Jahresübersicht")

    val year = range._1.getYear

    for {
      month <- 1 to 12
      day <- 1 to Month.get(month).dayCount(year)
      dow = LocalDate.of(year, month, 1).getDayOfWeek.getValue
      row_offset = if (dow < 3) dow + 8 else dow + 1
      row = row_offset + day - 1
      col = 1 + (month - 1) * 8
    } {
      sheet(row, col) = day
    }

    // update formulas
    book.evaluateAll
  }
}