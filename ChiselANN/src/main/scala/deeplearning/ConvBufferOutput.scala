
package deeplearning

import chisel3._
import chisel3.util._

class ConvBufferOutput(
                      dtype : SInt,
                      height : Int,
                      width : Int,
                      batch : Int
                      ) extends  Module {


  val addr_width: Int = log2Ceil(height) + 1
  val mem = Mem(height,Vec(batch,Vec(width,dtype)))
  val addr = RegInit(0.U(addr_width.W))

  val io = IO(new Bundle() {
    val dataIn = Flipped(Decoupled(Vec(batch,Vec(width,dtype))))
    val dataOut = Decoupled(Vec(height * height * batch,dtype))
    val addr = Output(UInt(addr_width.W))
  })

  val addr_inc = RegNext(io.dataIn.valid)

  when(io.dataIn.valid){
    mem.write(addr,io.dataIn.bits)
    addr := addr + 1.U
  }

  when(addr === height.asUInt(addr_width.W)){
    addr := 0.U(addr_width.W)
    io.dataOut.valid := true.B
  }.otherwise{
    io.dataOut.valid := false.B
  }

  for(b <- 0 until batch)
    for(w <- 0 until width)
      for(h <- 0 until height)
        io.dataOut.bits(b*width*height + h*width + w) := mem(h)(b)(w)

  io.addr := addr
  io.dataIn.ready := true.B
}
