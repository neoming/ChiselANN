package deeplearning

import chisel3._
import chisel3.util._

class OutputLayer(
    dtype: SInt,
    inNo: Int,
) extends Module {

  val io = IO(new Bundle() {
    val input = Flipped(Decoupled(Vec(inNo, dtype)))
    val output = Decoupled(UInt(log2Ceil(inNo).W))
  })

  val result_bits: Int = log2Ceil(inNo)
  val latency: Int = result_bits
  var result: List[UInt] = (0 until inNo)
    .map(i => {
      i.asUInt(result_bits.W)
    })
    .toList

  def comparator(a: UInt, b: UInt): UInt = {
    val bigger = Mux(io.input.bits(a) > io.input.bits(b), a, b)
    bigger
  }

  while (result.size > 1) {
    result = result
      .grouped(2)
      .map(grp => {
        val big = grp.reduce(comparator)
        RegNext(big)
      })
      .toList
  }

  io.input.ready := true.B
  io.output.valid := ShiftRegister(io.input.valid,latency,false.B,true.B)
  io.output.bits := result.head
}
