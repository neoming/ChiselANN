
package deeplearning

import chisel3._
import chisel3.util._

class MaxPoolingLayer(
    dtype : SInt,
    dataWidth : Int = 24,
    dataHeight : Int = 24,
    filterWidth : Int = 2,
    filterHeight : Int = 2,
    filterBatch : Int = 3,
) extends Module {
  val inputNo : Int = dataWidth * dataHeight * filterBatch
  val outputWidth : Int = dataWidth/filterWidth
  val outputHeight : Int = dataHeight/filterHeight
  val outputNo : Int = outputWidth * outputHeight * filterBatch

  val io = IO(new Bundle() {
    val dataIn = Flipped(Decoupled(Vec(inputNo,dtype)))
    val dataOut = Decoupled(Vec(outputNo,dtype))
  })

}
