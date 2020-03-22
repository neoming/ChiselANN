
package deeplearning

import chisel3._
import chisel3.util._

class ConvBufferLine(
                    dtype : SInt,
                    height : Int = 5,
                    width : Int = 28,
                    )extends Module{

  val io = IO(new Bundle() {
    val dataIn = Flipped(Decoupled(Vec(width, dtype)))
    val dataOut = Decoupled(Vec(width*height,dtype))
  })

  val buffer = Mem(height,Vec(width,dtype))
  val (adder , full) = Counter(io.dataIn.valid,height)
  when(io.dataIn.valid){
    buffer(adder) := io.dataIn.bits
  }

  for(w <- 0 until width)
    for(h <- 0 until height){
      io.dataOut.bits(w + h * width) := buffer(h)(w)
    }

  io.dataIn.ready := true.B
  io.dataOut.valid := RegNext(full)
}
