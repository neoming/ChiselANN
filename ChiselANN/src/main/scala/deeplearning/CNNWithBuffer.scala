
package deeplearning

import chisel3._
import chisel3.util._

class CNNWithBuffer(
  dtype : SInt,
  img : Vec[Vec[SInt]],
  dense_bias : Seq[SInt],
  dense_weights : Seq[Seq[SInt]],
  conv_bias : Seq[SInt],
  conv_weights :  Seq[Seq[Seq[SInt]]],
  frac_bits: Int = 0,
) extends Module{

  val dataWidth = 28
  val dataHeight = 28
  val filterWidth = 5
  val filterHeight = 5
  val filterBatch = 3
  val strideHeight = 1
  val strideWidth = 1
  val output = 10

  val io = IO(new Bundle{
    val inputBundle = new InputBundle(dtype = dtype,dataWidth = dataWidth,dataHeight = dataHeight)
    val dataOut = Decoupled(UInt(log2Ceil(output).W))
  })

  val getInputImg = Module(new GetInputImg(
    dtype = dtype,
    dataWidth = dataWidth,
    dataHeight = dataWidth,
    filterHeight = filterHeight,
    img = Some(img),
  ))

  getInputImg.io.inputBundle <> io.inputBundle

  val convLayer = Module(new ConvLayer(
    dtype = dtype,
    weights = conv_weights,
    bias = conv_bias,
    dataWidth = dataWidth,
    dataHeight = dataHeight,
    filterHeight = filterHeight,
    filterWidth = filterWidth,
    filterBatch = filterBatch,
    strideHeight = strideHeight,
    strideWidth = strideWidth,
    frac_bits = frac_bits
  ))

  convLayer.io.dataIn <> getInputImg.io.dataOut

}
