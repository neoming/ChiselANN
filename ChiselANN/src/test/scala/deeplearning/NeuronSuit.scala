
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object NeuronSuit extends App{

  class NeuronDebugTester(c : Neuron,wfname:String,ifname:String,rfname:String,dtype:SInt) extends PeekPokeTester(c){

    val weights: Seq[Seq[SInt]] = TestTools.getTwoDimArryAsSInt(wfname,dtype)
    val input: Seq[SInt] = TestTools.getOneDimArryAsSInt(ifname,dtype)
    val output: SInt = TestTools.getOneDimArryAsSInt(rfname,dtype,0).head

    for(i <- input.indices){
      print(i + " weight is" + weights.head(i) + " input is " + input(i) + "\n")
      poke(c.io.weights(i),weights.head(i))
      poke(c.io.in(i),input(i))
    }

    step(c.mac_latency + c.act_latency)
    print(peek(c.io.mul_res.get))
    print(peek(c.io.act) + "\n")
    expect(c.io.act,output)
  }


  def runNeuronDebugTester(
    wfname:String,//weights file name
    bfname:String,//bias file name
    ifname:String,//input file name
    rfname:String,//test result file name
    dtype:SInt,
    inNo:Int,
  ) : Boolean = {
    val bias = TestTools.getOneDimArryAsSInt(bfname,dtype)
    //chisel3.Driver.emitVerilog(new DenseLayer(dtype,inNo,outNo,bias,weights)
    Driver(() =>new Neuron(SInt(16.W),inNo,bias.head,true)){
      n => new NeuronDebugTester(n,wfname,ifname,rfname,dtype)
    }
  }

  def main():Unit = {
    val weights_file_name = "dense1_weights.csv"
    val bias_file_name = "dense1_bias.csv"
    val input_file_name = "dense_output_0.csv"
    val result_file_name = "test_dense1_output_0.csv"
    runNeuronDebugTester(weights_file_name,bias_file_name,
      input_file_name,result_file_name,SInt(16.W),30)
  }

  main()
}