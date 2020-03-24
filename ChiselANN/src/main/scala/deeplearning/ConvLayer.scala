
package deeplearning

import chisel3._
import chisel3.util._

class ConvLayer(
               dtype : SInt,
               weights : Seq[Seq[Seq[SInt]]],
               bias : Seq[SInt],
               dataWidth : Int = 28,
               dataHeight : Int = 28,
               filterWidth : Int = 5,
               filterHeight : Int = 5,
               filterBatch : Int = 3,
               strideWidth : Int = 1,
               strideHeight : Int = 1,
               frac_bits : Int = 0,
               ) extends  Module{

  val inputNo: Int = dataWidth
  val outputWidth: Int = (dataWidth - filterWidth + 1 )/strideWidth
  val outputHeight: Int = (dataHeight - filterHeight + 1 )/strideHeight
  val outputNo: Int = outputWidth * outputHeight * filterBatch
  val neuronInputNo: Int = filterHeight * filterWidth

  val io = IO(new Bundle{
    val dataIn = Flipped(Decoupled(Vec(inputNo, dtype)))
    val dataOut = Decoupled(Vec(outputNo,dtype))
    //for debug
    val conv_buffer = Output(Vec(filterHeight*dataWidth,dtype))
    val conv_buffer_valid = Output(Bool())
    val conv_line = Output(Vec(filterBatch,Vec(outputWidth,dtype)))
    val conv_line_valid = Output(Bool())
  })


  //fifo buffer
  val conv_buffer = Module(new ConvBufferLine(
    dtype = dtype,
    height = filterHeight,
    width = dataWidth
  ))

  // 每行有 filterBatch * dataWidth 个卷积神经元
  val conv_line = Module(new ConvLine(
    dtype = dtype,
    weights = weights,
    bias = bias,
    frac_bits = frac_bits
  ))

  val conv_output = Module(new ConvBufferOutput(
    dtype = dtype,
    width = outputWidth,
    height = outputHeight,
    batch = filterBatch
  ))

  conv_buffer.io.dataIn <> io.dataIn
  conv_line.io.dataIn <> conv_buffer.io.dataOut
  conv_output.io.dataIn <> conv_line.io.dataOut
  io.dataOut <> conv_output.io.dataOut

  val latency : Int = outputHeight * conv_buffer.latency + conv_line.latency
  io.dataIn.ready := true.B

  //debug
  io.conv_buffer_valid := conv_buffer.io.dataOut.valid
  io.conv_buffer := conv_buffer.io.dataOut.bits
  io.conv_line_valid := conv_line.io.dataOut.valid
  io.conv_line := conv_line.io.dataOut.bits
}
