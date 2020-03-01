
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object Conv2DLayerSuit extends App {

  class Conv2DLayerTester(
    c:Conv2DLayer,
    ifname:String,
    rfname:String,
    dtype:SInt
  ) extends PeekPokeTester(c){
    val inputs:Seq[SInt] = TestTools.getOneDimArryAsSInt(ifname,dtype);
    for(i <- inputs.indices){
      poke(c.io.dataIn.bits(i),inputs(i))
    }
    poke(c.io.dataIn.valid,true.B)

    for(i <- 0 until c.latency){
      step(1)
      //print("the " + i + " cycle: " + peek(c.io.dataOut.bits) + " valid is " + peek(c.io.dataOut.valid) + "\n")
    }

    TestTools.writeRowToCsv(peek(c.io.dataOut.bits).toList, rfname)//write result
  }

  def runConv2DTester(
    wfnames:Seq[String],//weights file name
    bfname:String,//bias file name
    ifname:String,//input file name
    rfname:String,//test result file name
    dtype:SInt,
    frac_bits:Int,
  ) : Boolean = {

    val weights = wfnames.indices.map(i => {
      val temp_weights = TestTools.getTwoDimArryAsSInt(wfnames(i),dtype)
      temp_weights
    }).toList
    val bias = TestTools.getOneDimArryAsSInt(bfname,dtype)
    Driver(() => new Conv2DLayer(dtype,weights = weights,bias = bias,frac_bits = frac_bits)){
      c => new Conv2DLayerTester(c,ifname,rfname,dtype)
    }
  }

  def testConv2D():Unit = {
    val weights_0 = "test_cnn/conv0_weights.csv"
    val weights_1 = "test_cnn/conv1_weights.csv"
    val weights_2 = "test_cnn/conv2_weights.csv"
    val weights :Seq[String] = Seq(weights_0,weights_1,weights_2)
    val bias = "test_cnn/conv_bias.csv"
    val input = "test_cnn/input_1d_7.csv"
    val result = "test_cnn/test_conv_output_7.csv"
    runConv2DTester(weights,bias,input,result,SInt(16.W),frac_bits = 4)
  }
  testConv2D()
}
