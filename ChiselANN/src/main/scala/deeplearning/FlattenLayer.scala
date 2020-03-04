
package deeplearning

import chisel3._
import chisel3.util._

class FlattenLayer(
    dtype : SInt,
    dataWidth : Int = 12,
    dataHeight : Int = 12,
    dataBatch : Int = 3,
) extends Module{

    val inNo: Int= dataWidth * dataHeight * dataBatch
    val io = IO(new Bundle{
        val dataIn = Flipped(Decoupled(Vec(inNo, dtype)))
        val dataOut = Decoupled(Vec(inNo,dtype))
    })

    for(b <- 0 until dataBatch)
        for(w <- 0 until dataWidth)
            for( h <- 0 until dataHeight){
                val inputIndex = w + h * dataWidth + b * dataWidth * dataHeight
                val outputIndex = b + w * dataBatch + h * dataWidth * dataBatch
                io.dataOut.bits(outputIndex) := RegNext(io.dataIn.bits(inputIndex))
            }
    val latency: Int = 1

    io.dataIn.ready := true.B
    io.dataOut.valid := ShiftRegister(io.dataIn.valid, latency, false.B, true.B)
}
