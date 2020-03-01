
package deeplearning

import chisel3._
import chisel3.util._

class MaxMux(
    dtype : SInt,
    inputNo : Int,
)extends Module{
  val io = IO(new Bundle() {
    val dataIn = Input(Vec(inputNo,dtype))
    val dataOut = Output(dtype)
  })

  def bigger(a: SInt, b: SInt): SInt = {
    val bigger = Mux(a > b, a, b)
    bigger
  }
  var max: Seq[SInt] = io.dataIn.toList

  while (max.size > 1){
    max = max
      .grouped(2)
      .map(grp => {
      val big = grp.reduce(bigger)
      RegNext(big)
    })
    .toList
  }

  val latency: Int = log2Ceil( inputNo ) + 1
  io.dataOut := max.head
}
