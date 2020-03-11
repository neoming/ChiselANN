
package deeplearning

import java.io.File

import chisel3._
import com.github.tototoshi.csv.CSVWriter

object TestTools {
  val fracBits = 4
  val src_path = "src/main/resources/"
  val ReLU: SInt => SInt = x => Mux(x >= 0.S, x, 0.S)
  val No: SInt => SInt = x => x

  def getTwoDimArryAsSInt(fname: String,dtype:SInt,frac:Int = fracBits):Seq[Seq[SInt]] = {
    val buffer = scala.io.Source.fromFile(src_path + fname)
    val buffer_raw = buffer.getLines().toList
    val result = buffer_raw.map(_.split(",").toList.map(x => {
      BigInt(math.round( x.toFloat * ( 1 << frac ) ).toInt)
    }).map( x => x.asSInt(dtype.getWidth.W))).transpose
    result
  }

  def getTwoDimArryAsSIntWithOutTrans(fname: String,dtype:SInt,frac:Int = fracBits):Seq[Seq[SInt]] = {
    val buffer = scala.io.Source.fromFile(src_path + fname)
    val buffer_raw = buffer.getLines().toList
    val result = buffer_raw.map(_.split(",").toList.map(x => {
      BigInt(math.round( x.toFloat * ( 1 << frac ) ).toInt)
    }).map( x => x.asSInt(dtype.getWidth.W)))
    result
  }
  def getOneDimArryAsSInt(fname: String,dtyp:SInt,frac:Int = fracBits):Seq[SInt] = {
    val buffer = scala.io.Source.fromFile(src_path + fname)
    val buffer_raw = buffer.getLines().toList
    val result = buffer_raw.head.split(",").toList.map(x => {
      BigInt(math.round( x.toFloat * ( 1 << frac)).toInt)
    }).map( x => x.asSInt(dtyp.getWidth.W))
    result
  }

  def getOneDimArryAsUInt(fname:String,dtyp:UInt,frac:Int = fracBits):Seq[UInt] = {
    val buffer = scala.io.Source.fromFile(src_path + fname)
    val buffer_raw = buffer.getLines().toList
    val result = buffer_raw.head.split(",").toList.map(x => {
      BigInt(math.round( x.toFloat * ( 1 << frac)).toInt)
    }).map( x => x.asUInt(dtyp.getWidth.W))
    result
  }

  def writeRowToCsv(row:Seq[AnyRef],fname:String):Unit = {
    val f = new File(src_path + fname)
    val writer = CSVWriter.open(f)
    writer.writeRow(row)
  }
}
