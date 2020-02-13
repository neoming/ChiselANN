
package deeplearning

import chisel3._
import chisel3.experimental.FixedPoint

class FixedPointCalculation extends Module {
  val io = IO(new Bundle{
    val value1 = Input(FixedPoint(16.W,8.BP))
    val value2 = Input(FixedPoint(16.W,8.BP))
    val output = Output(FixedPoint(16.W,8.BP))
  })
  val reg = RegInit(SInt(16.W),2.S)
  val reg2 = RegInit(SInt(16.W),3.S)
  val result = Wire(SInt(32.W))
  result := reg + reg2
  io.output := io.value1 + io.value2
}
