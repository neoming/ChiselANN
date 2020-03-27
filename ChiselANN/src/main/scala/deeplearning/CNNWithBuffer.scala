
package deeplearning

import chisel3._
import chisel3.util._

class CNNWithBuffer(
  dtype : SInt,
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

  //img mem
  val getInputImg = Module(new GetInputImg(
    dtype = dtype,
    dataWidth = dataWidth,
    dataHeight = dataWidth,
    filterHeight = filterHeight,
  ))

  getInputImg.io.inputBundle <> io.inputBundle

  //conv
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
    frac_bits = frac_bits,
    debug = false,
  ))

  convLayer.io.dataIn <> getInputImg.io.dataOut

  //max pool
  val maxpool_data_width : Int = 24
  val maxpool_data_height : Int = 24
  val maxpool_filter_width : Int = 2
  val maxpool_filter_height : Int = 2
  val maxpool_filter_batch : Int = 3
  val maxPoolLayer = Module(new MaxPoolingLayer(
    dtype = dtype,
    dataWidth = maxpool_data_width,
    dataHeight = maxpool_data_height,
    filterWidth = maxpool_filter_width,
    filterHeight = maxpool_filter_height,
    filterBatch = maxpool_filter_batch,
  ))
  maxPoolLayer.io.dataIn <> convLayer.io.dataOut

  // flatten
  val flatten_data_width : Int = 12
  val flatten_data_height : Int = 12
  val flatten_data_batch : Int = 3
  val flattenLayer = Module(new FlattenLayer(
    dtype = dtype,
    dataWidth = flatten_data_width,
    dataHeight = flatten_data_height,
    dataBatch = flatten_data_batch
  ))
  flattenLayer.io.dataIn <> maxPoolLayer.io.dataOut

  // dense
  val dense_inNo: Int = 12 * 12 * 3
  val dense_outNo: Int = 10
  val denseLayer = Module(new DenseLayer(
    dtype = dtype,
    bias = dense_bias,
    weights = dense_weights,
    frac_bits = frac_bits,
    inNo = dense_inNo,
    outNo = dense_outNo
  ))
  denseLayer.io.dataIn <> flattenLayer.io.dataOut

  //output
  val output_inNo: Int = 10
  val outputLayer = Module(new OutputLayer(
    dtype = dtype,
    inNo = output_inNo
  ))
  outputLayer.io.dataIn <> denseLayer.io.dataOut
  io.dataOut <> outputLayer.io.dataOut
  val latency: Int = convLayer.latency + maxPoolLayer.latency + flattenLayer.latency + denseLayer.latency + outputLayer.latency
}
