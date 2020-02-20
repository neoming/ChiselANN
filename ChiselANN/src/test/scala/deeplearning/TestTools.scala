
package deeplearning

import java.io.File

import chisel3._
import com.github.tototoshi.csv.CSVWriter

object TestTools {
  val fracBits = 4
  val src_path = "src/main/resources/"

  def getTwoDimArryAsSInt(fname: String,dtype:SInt):Seq[Seq[SInt]] = {
    val buffer = scala.io.Source.fromFile(src_path + fname)
    val buffer_raw = buffer.getLines().toList
    val result = buffer_raw.map(_.split(",").toList.map(x => {
      BigInt(math.round( x.toFloat * ( 1 << fracBits ) ).toInt)
    }).map( x => x.asSInt(dtype.getWidth.W))).transpose
    result
  }

  def getOneDimArryAsSInt(fname: String,dtyp:SInt):Seq[SInt] = {
    val buffer = scala.io.Source.fromFile(src_path + fname)
    val buffer_raw = buffer.getLines().toList
    val result = buffer_raw(0).split(",").toList.map(x => {
      BigInt(math.round( x.toFloat * ( 1 << fracBits)).toInt)
    }).map( x => x.asSInt(dtyp.getWidth.W))
    result
  }

  def writeRowToCsv(row:Seq[AnyRef],fname:String):Unit = {
    val f = new File(src_path + fname)
    val writer = CSVWriter.open(f)
    writer.writeRow(row)
  }
}
