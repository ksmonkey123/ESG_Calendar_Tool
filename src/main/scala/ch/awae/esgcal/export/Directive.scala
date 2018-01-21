package ch.awae.esgcal.export

import ch.awae.esgcal.xssf.CellWriteMagnet
import ch.awae.esgcal.xssf.Sheet

case class Directive[T: CellWriteMagnet](row: Int, col: Int, value: T) {

  def process(baseRow: Int, baseCol: Int, sheet: Sheet) = {
    sheet(baseRow + row, col + baseCol) = value
  }

  override def toString(): String = {
    s"Directive(${implicitly[CellWriteMagnet[T]].getClass.getSimpleName}, $row-$col, $value"
  }

  def toEntry(baseRow: Int, baseCol: Int) = new Entry(baseRow + row, baseCol + col, value)

}

case class Entry[T: CellWriteMagnet](row: Int, col: Int, value: T) {
  def process(sheet: Sheet) = sheet(row, col) = value
  override def toString(): String = {
    s"($row, $col) <- $value"
  }
}