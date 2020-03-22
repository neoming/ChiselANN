
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
  })


  //fifo buffer
  val conv_buffer = Module(new ConvBufferLine(dtype,filterHeight,dataWidth))
  // 每行有 filterBatch * dataWidth 个卷积神经元
  val conv_line = Module(new ConvLine(dtype,weights,bias,frac_bits = frac_bits))

  conv_buffer.io.dataIn <> io.dataIn
  conv_line.io.dataIn <> conv_buffer.io.dataOut

  // Mem保存结果
  val (result_addr,is_full) = Counter(conv_line.io.dataOut.valid,outputHeight)
  val result_buffer = Mem(outputHeight,Vec(filterBatch,Vec(outputWidth,dtype)))

  //valid 写入结果
  when(conv_line.io.dataOut.valid){
    result_buffer(result_addr) := conv_line.io.dataOut.bits(result_addr)
  }

  //结果接入到输出
  for(h <- 0 until outputHeight)
    for(w <- 0 until outputWidth)
      for(f <- 0 until filterBatch){
        val outputIndex: Int = f * outputWidth * outputHeight + w + outputWidth * h
        io.dataOut.bits(outputIndex) := result_buffer(h)(f)(w)
      }

  io.dataOut.valid := RegNext(is_full)
}
