package deeplearning

import chisel3._
import chisel3.util.log2Ceil

class OutputLayer(
    dtype: SInt,
    inNo: Int,
) extends Module {

  val io = IO(new Bundle() {
    val input = Input(Vec(inNo, dtype))
    val output = Output(UInt(log2Ceil(inNo).W))
  })

  val result_bits: Int = log2Ceil(inNo)
  val latency: Int = result_bits
  var result: List[UInt] = (0 until inNo)
    .map(i => {
      i.asUInt(result_bits.W)
    })
    .toList

  def comparator(a: UInt, b: UInt): UInt = {
    val bigger = Mux(io.input(a) > io.input(b), a, b)
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

  io.output := result.head
}
