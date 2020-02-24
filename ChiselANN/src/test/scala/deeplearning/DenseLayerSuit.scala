
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object DenseLayerSuit extends App{

  class DenseLayerTester(
    c:DenseLayer,
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

    TestTools.writeRowToCsv(peek(c.io.dataOut.bits).toList, rfname)//write result
  }

  def runDenseTester(
    wfname:String,//weights file name
    bfname:String,//bias file name
    ifname:String,//input file name
    rfname:String,//test result file name
    dtype:SInt,
    inNo:Int,
    outNo:Int,
    frac_bits:Int,
  ) : Boolean = {
    val weights = TestTools.getTwoDimArryAsSInt(wfname,dtype)
    val bias = TestTools.getOneDimArryAsSInt(bfname,dtype)
    Driver(() =>new DenseLayer(dtype,inNo,outNo,bias,weights,frac_bits)){
      d => new DenseLayerTester(d,ifname,rfname,dtype)
    }
  }

  def testDense1():Unit = {
    val weights_file_name = "dense1_weights.csv"
    val bias_file_name = "dense1_bias.csv"
    val input_file_name = "dense_output_0.csv"
    val result_file_name = "test_dense1_output_0.csv"
    runDenseTester(weights_file_name,bias_file_name,input_file_name,result_file_name,SInt(16.W),30,10,4)
  }

  def testDense():Unit = {
    val weights_file_name = "dense_weights.csv"
    val bias_file_name = "dense_bias.csv"
    val input_file_name = "flatten_output_0.csv"
    val result_file_name = "test_dense_output_0.csv"
    runDenseTester(weights_file_name,bias_file_name,input_file_name,result_file_name,SInt(16.W),784,30,4)
  }

  testDense1()
}