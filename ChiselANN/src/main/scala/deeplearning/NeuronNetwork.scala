package deeplearning

import chisel3._
import chisel3.util._

class NeuronNetwork(
    dtype: SInt, //input data type
    dense_bias: Seq[SInt], //length equal to output Number
    dense_weights: Seq[Seq[SInt]], //[outNo][inNo]
    dense1_bias: Seq[SInt], //length equal to output Number
    dense1_weights: Seq[Seq[SInt]], //[outNo][inNo]
    inNo: Int = 784, //input Number
    outNo: Int = 10, //output Number
    frac_bits: Int = 0,
) extends Module {

  val io = IO(new Bundle {
    val dataIn = Flipped(Decoupled(Vec(inNo, dtype)))
    val dataOut = Decoupled(UInt(log2Ceil(outNo).W))
  })

  // 784 * 30 dense layer
  val dense_inNo: Int = inNo
  val dense_outNo = 30
  val dense = Module(
    new DenseLayer(
      dtype = dtype,
      inNo = dense_inNo,
      outNo = dense_outNo,
      bias = dense_bias,
      weights = dense_weights,
      frac_bits = frac_bits
    ))

  dense.io.dataIn <> io.dataIn

  // 30 * 10 dense layer
  val dense1_inNo: Int = dense_outNo
  val dense1_outNo: Int = outNo
  val dense1 = Module(
    new DenseLayer(
      dtype = dtype,
      inNo = dense1_inNo,
      outNo = dense1_outNo,
      bias = dense1_bias,
      weights = dense1_weights,
      frac_bits = frac_bits
    ))

  dense1.io.dataIn <> dense.io.dataOut

  // 10 output layer
  val output_outNo: Int = dense1_outNo
  val output = Module(
    new OutputLayer(
      dtype,
      inNo = output_outNo
    ))

  output.io.dataIn <> dense1.io.dataOut

  val latency: Int = dense.latency + dense1.latency + output.latency
  io.dataOut <> output.io.dataOut
}
