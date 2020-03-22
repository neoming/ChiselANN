
package deeplearning

import chisel3._
import chisel3.util._

// 每行有 filterBatch * dataWidth 个卷积神经元
class ConvLine(
              dtype : SInt,
              weights : Seq[Seq[Seq[SInt]]],
              bias : Seq[SInt],
              dataWidth : Int = 28,
              filterWidth : Int = 5,
              filterHeight : Int = 5,
              filterBatch : Int = 3,
              strideWidth : Int = 1,
              frac_bits : Int = 0,
              )extends Module{

  val outputWidth: Int = (dataWidth - filterWidth + 1)/strideWidth

  val io = IO(new Bundle{
    val dataIn = Flipped(Decoupled(Vec(dataWidth * filterHeight, dtype)))
    val dataOut = Decoupled(Vec(filterBatch,Vec(outputWidth,dtype)))
  })

  //权重存到ROM中
  val weight_rom: Vec[Vec[Vec[SInt]]] = Vec(filterBatch,Vec(filterWidth,Vec(filterBatch,dtype)))
  for(f <- 0 until filterBatch)
    for(w <- 0 until filterWidth)
      for(h <- 0 until filterHeight)
        weight_rom(f)(w)(h) := weights(f)(w)(h)
  val bias_rom = VecInit(bias)

  val conv_neurons : Seq[Seq[Neuron]]= (0 until filterBatch).map(f =>{
    (0 until outputWidth).map(j =>{
      val neuron = Module(new Neuron(dtype , filterHeight * filterWidth ,false,frac_bits))
      neuron.io.bias := bias_rom(f)
      val baseW : Int = j * strideWidth
      for(w <- 0 until filterWidth)
        for( h <- 0 until filterHeight){
          val ioIndex = baseW + w + outputWidth * h
          neuron.io.in(w + filterWidth * h) := io.dataIn.bits(ioIndex)
          neuron.io.weights(w + filterWidth * h) := weight_rom(f)(w)(h)
        }
      io.dataOut.bits(f)(j) := neuron.io.act
      neuron
    }).toList
  }).toList

  val latency: Int = conv_neurons.head.head.total_latency
  io.dataIn.ready := true.B
  io.dataOut.valid := ShiftRegister(io.dataIn.valid, latency, false.B, true.B)

}
