
package deeplearning

import chisel3._
import chisel3.util.Decoupled

class FlattenLayer(dtype : SInt, val inumber : Int) extends Module {
  val io = IO(new Bundle() {
    val dataIn = Flipped( Decoupled( Vec( inumber, dtype ) ) )
    val dataOut = Decoupled( Vec( inumber, dtype ) )
  })

  val registers = Wire(Vec(inumber,dtype))
  for (i <- 0 until inumber){
    registers(i) := 1.S + io.dataIn.bits(i)
  }

  when(io.dataIn.valid){
    io.dataOut.valid := true.B
  }.otherwise{
    io.dataOut.valid := false.B
  }

  io.dataIn.ready := true.B
  io.dataOut.bits := registers
}
