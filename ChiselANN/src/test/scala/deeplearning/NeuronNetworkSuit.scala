
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object NeuronNetworkSuit extends App{
  class NeuronNetworkTester(
    c:NeuronNetwork,
    ifname:String,
    rfname:String,
    dtype:SInt
  ) extends PeekPokeTester(c){
    val inputs: Seq[SInt] = TestTools.getOneDimArryAsSInt(ifname,dtype)
    for( i <- inputs.indices ){
      poke(c.io.dataIn.bits(i),inputs(i))
    }
    poke(c.io.dataIn.valid,true.B)
    for(i <- 0 until c.latency){
      step(1)
      print("the " + i + " cycle: " + peek(c.io.dataOut.bits) + " valid is " + peek(c.io.dataOut.valid) + "\n")
    }
  }

  def runNeuronNetworkTester(
    wfname:Seq[String],
    bfname:Seq[String],
    ifname:String,
    rfname:String,
    dtype:SInt,
    inNo:Int,
    outNo:Int,
    frac_bits:Int
  ) : Boolean = {
    val dense_weights = TestTools.getTwoDimArryAsSInt(wfname.head,dtype)
    val dense1_weights = TestTools.getTwoDimArryAsSInt(wfname(1),dtype)
    val dense_bias = TestTools.getOneDimArryAsSInt(bfname.head,dtype)
    val dense1_bias = TestTools.getOneDimArryAsSInt(bfname(1),dtype)
    Driver(() => new NeuronNetwork(
      dtype,inNo = inNo,outNo = outNo, dense_bias = dense_bias, dense_weights = dense_weights,
      dense1_bias = dense1_bias,dense1_weights = dense1_weights,frac_bits = frac_bits)){
      n => new NeuronNetworkTester(n,ifname,rfname,dtype)
    }
  }

  def testNeuronNetwork():Unit = {
    val dense_weights_file = "test_ann/dense_weights.csv"
    val dense_bias_file = "test_ann/dense_bias.csv"
    val dense1_weights_file = "test_ann/dense1_weights.csv"
    val dense1_bias_file = "test_ann/dense1_bias.csv"
    val wfname = Seq[String](dense_weights_file,dense1_weights_file)
    val bfname = Seq[String](dense_bias_file,dense1_bias_file)
    val input_file = "test_ann/flatten_output_0.csv"
    val result_file = "test_ann/test_neuron_0.csv"
    runNeuronNetworkTester(wfname,bfname,input_file,result_file,
      SInt(16.W),784,10,4)
  }

  testNeuronNetwork()
}
