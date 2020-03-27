
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
               debug : Boolean = true,
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
    val conv_buffer = if(debug)Some(Output(Vec(filterHeight*dataWidth,dtype))) else None
    val conv_buffer_valid = if(debug)Some(Output(Bool())) else None
    val conv_line = if(debug)Some(Output(Vec(filterBatch,Vec(outputWidth,dtype)))) else None
    val conv_line_valid = if(debug)Some( Output(Bool())) else None
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

  if(debug){
    //debug
    io.conv_buffer_valid.get := conv_buffer.io.dataOut.valid
    io.conv_buffer.get := conv_buffer.io.dataOut.bits
    io.conv_line_valid.get := conv_line.io.dataOut.valid
    io.conv_line.get := conv_line.io.dataOut.bits
  }
}
