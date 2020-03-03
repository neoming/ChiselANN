
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
  val maxMuxInNo : Int = filterWidth * filterHeight

  val io = IO(new Bundle() {
    val dataIn = Flipped(Decoupled(Vec(inputNo,dtype)))
    val dataOut = Decoupled(Vec(outputNo,dtype))
  })

  val filters:Seq[Seq[Seq[MaxMux]]] = (0 until filterBatch).map( f =>{
    val maxMuxs = {
      (0 until outputWidth).map(i =>{
        (0 until outputHeight).map(j => {
          val maxMux = Module(new MaxMux(dtype,maxMuxInNo))
          val inputBaseW: Int = i * filterWidth
          val inputBaseH: Int = j * filterHeight
          val outputIndex: Int = f * outputWidth * outputHeight + i + outputWidth * j
          for(w <- 0 until filterWidth){
            for(h <- 0 until filterHeight){
              val inputW: Int = inputBaseW + w
              val inputH: Int = inputBaseH + h
              val inputIndex: Int = f * dataWidth * dataHeight + inputW + inputH * dataWidth
              val maxMuxInputIndex: Int = w + filterWidth * h
              maxMux.io.dataIn(maxMuxInputIndex) := io.dataIn.bits(inputIndex)
            }
          }
          io.dataOut.bits(outputIndex) := maxMux.io.dataOut
          maxMux
        }).toList
      }).toList
    }
    maxMuxs
  }).toList

  val latency: Int = filters.head.head.head.latency
  io.dataIn.ready := true.B
  io.dataOut.valid := ShiftRegister(io.dataIn.valid,latency,false.B,true.B)
}
