
package deeplearning

import chisel3._
import chisel3.util.log2Ceil

class ImgMem(
            dtype : SInt,
            height : Int,
            width : Int,
            ) extends Module{
  val io = IO(new Bundle {
    val write = Input(Bool())
    val addr = Input(UInt(log2Ceil(height).W))
    val dataIn = Input(Vec(width,dtype))
    val dataOut = Output(Vec(width,dtype))
  })

  val mem = Reg(Vec(height, Vec(width,dtype)))
  io.dataOut := DontCare

  when (io.write) {
    mem(io.addr) := io.dataIn
  }

  io.dataOut := mem(io.addr)
}
