package deeplearning

import chisel3._

class DNN(
    dtype: SInt, //input data type
    inNo: Int = 784, //input Number
    outNo: Int = 10, //output Number
    dense_bias: Seq[SInt], //length equal to output Number
    dense_weights: Seq[Seq[SInt]], //[outNo][inNo]
    dense1_bias: Seq[SInt], //length equal to output Number
    dense1_weights: Seq[Seq[SInt]], //[outNo][inNo]
    frac_bits: Int = 0,
) extends Module {

  val io = IO(new Bundle {
    val dataIn = Input(Vec(inNo, dtype))
    val dataOut = Output(Vec(outNo, dtype))
  })

  // 784 * 30 dense layer
  val dense_inNo: Int = inNo
  val dense_outNo = 30
  val dense = Module(
    new DenseLayer(
      dtype,
      dense_inNo,
      dense_outNo,
      dense_bias,
      dense_weights,
      frac_bits
    ))

  dense.io.dataIn <> io.dataIn

  // 30 * 10 dense layer
  val dense1_inNo: Int = dense_outNo
  val dense1_outNo: Int = outNo
  val dense1 = Module(
    new DenseLayer(
      dtype,
      dense1_inNo,
      dense_outNo,
      dense1_bias,
      dense1_weights,
      frac_bits
    ))

  dense1.io.dataIn <> dense.io.dataOut

  io.dataOut <> dense1.io.dataOut
}
