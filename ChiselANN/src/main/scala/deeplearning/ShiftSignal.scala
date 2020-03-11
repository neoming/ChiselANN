
package deeplearning

import chisel3._
import chisel3.util.{Decoupled, ShiftRegister}

class ShiftSignal extends Module {
  val io = IO(new Bundle() {
    val dataIn = Flipped(Decoupled(SInt(16.W)))
    val dataOut = Decoupled(SInt(16.W))
  })
  io.dataOut.bits := io.dataIn.bits
  io.dataIn.ready := true.B
  io.dataOut.valid := ShiftRegister(io.dataIn.valid,2,false.B,true.B)
}
