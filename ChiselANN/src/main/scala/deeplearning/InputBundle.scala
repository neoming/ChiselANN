
package deeplearning

import chisel3._
import chisel3.util.log2Ceil

//for img input
class InputBundle(dtype:SInt,dataWidth:Int,dataHeight:Int)extends Bundle{
  val write_data = Input(Vec(dataWidth,dtype))
  val write = Input(Bool())
  val write_addr = Input(UInt(log2Ceil(dataHeight).W))
  val dataReady = Input(Bool())
}