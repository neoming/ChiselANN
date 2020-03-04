
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object ConvNeuronNetworkSuit extends App {
  class CNNTester(
    c :ConvNeuronNetwork,
    ifname:String,
    rfname:String,
    dtype:SInt,
    frac_bits:Int
  ) extends PeekPokeTester(c){
    val inputs: Seq[SInt] = TestTools.getOneDimArryAsSInt(ifname,dtype,frac_bits)
    for( i <- inputs.indices ){
      poke(c.io.dataIn.bits(i),inputs(i))
    }
    poke(c.io.dataIn.valid,true.B)
    for(i <- 0 until c.latency){
      step(1)
      print("the " + i + " cycle: " + peek(c.io.dataOut.bits)
       + " valid is " + peek(c.io.dataOut.valid) + "\n")
      print("conv valid: " + peek(c.conv.io.dataOut.valid) + " maxPool valid: " + peek(c.maxPool.io.dataOut.valid)
      + " flatten valid: " + peek(c.flatten.io.dataOut.valid ) + " dense valid: " + peek(c.dense.io.dataOut.valid)
      + "output valid: " + peek(c.output_layer.io.dataOut.valid) + "\n")
    }
    expect(c.io.dataOut.bits,7.U)
  }

  def runCNNTester(
    cbfname:String,//conv bias file name
    cwfname:Seq[String],//conv weights file name List
    dbfname:String,//dense bias file name
    dwfname:String,//dense weights file name
    ifname:String,
    rfname:String,
    dtype:SInt,
    frac_bits: Int
  ) : Boolean = {
    val conv_weights = cwfname.indices.map(i => {
      val temp_weights = TestTools.getTwoDimArryAsSInt(cwfname(i),dtype)
      temp_weights
    }).toList
    val conv_bias = TestTools.getOneDimArryAsSInt(cbfname,dtype)

    val dense_bias = TestTools.getOneDimArryAsSInt(dbfname,dtype)
    val dense_weights = TestTools.getTwoDimArryAsSInt(dwfname,dtype)

    chisel3.Driver.execute(args, () => new ConvNeuronNetwork(
      dtype,dense_bias,dense_weights,
      conv_bias,conv_weights,frac_bits = frac_bits))

    Driver(() => new ConvNeuronNetwork(
      dtype,dense_bias,dense_weights,
      conv_bias,conv_weights,frac_bits = frac_bits)){
      c => new CNNTester(c,ifname,rfname,dtype,frac_bits)
    }
  }

  def testCNN():Unit={
    //get conv bias and weights
    val conv_weights_0 = "test_cnn/conv0_weights.csv"
    val conv_weights_1 = "test_cnn/conv1_weights.csv"
    val conv_weights_2 = "test_cnn/conv2_weights.csv"
    val conv_weights :Seq[String] = Seq(conv_weights_0,conv_weights_1,conv_weights_2)
    val conv_bias = "test_cnn/conv_bias.csv"

    //get dense bias and weight
    val dense_weights = "test_cnn/dense_weights.csv"
    val dense_bias = "test_cnn/dense_bias.csv"

    val input = "test_cnn/input_1d_7.csv"
    val result = "test_cnn/test_conv_output_7.csv"

    runCNNTester(conv_bias,conv_weights,dense_bias,dense_weights,
      input,result,SInt(16.W),4)
  }
  testCNN()
}
