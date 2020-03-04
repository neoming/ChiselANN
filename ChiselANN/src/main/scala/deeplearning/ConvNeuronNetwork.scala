
package deeplearning

import chisel3._
import chisel3.util._

class ConvNeuronNetwork(
    dtype : SInt,
    dense_bias : Seq[SInt],
    dense_weights : Seq[Seq[SInt]],
    conv_bias : Seq[SInt],
    conv_weights :  Seq[Seq[Seq[SInt]]],
    inNo : Int = 784,
    output : Int = 10,
    frac_bits: Int = 0,
) extends Module{

    val io = IO(new Bundle{
        val dataIn = Flipped(Decoupled(Vec(inNo, dtype)))
        val dataOut = Decoupled(UInt(log2Ceil(output).W))
    })

    val conv = Module(
      new Conv2DLayer(
        dtype = dtype,
        weights = conv_weights,
        bias = conv_bias,
        frac_bits = frac_bits)
    )
    conv.io.dataIn <> io.dataIn

    val maxPool = Module(new MaxPoolingLayer(dtype))
    maxPool.io.dataIn <> conv.io.dataOut

    val flatten = Module(new FlattenLayer(dtype))
    flatten.io.dataIn <> maxPool.io.dataOut

    val dense = Module(
      new DenseLayer(
        dtype = dtype,
        bias = dense_bias,
        weights = dense_weights,
        frac_bits = frac_bits)
    )
    dense.io.dataIn <> flatten.io.dataOut

    val output_layer  = Module(new OutputLayer(dtype))
    output_layer.io.dataIn <> dense.io.dataOut

    val latency: Int = conv.latency + maxPool.latency + flatten.latency + dense.latency + output_layer.latency
    io.dataOut <> output_layer.io.dataOut
}
