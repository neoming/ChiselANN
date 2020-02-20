
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object DenseLayerSuit extends App{

  def runDenseTester(
    wfname:String,//weights file name
    bfname:String,//bias file name
    ifname:String,//input file name
    rfname:String,//test result file name
    dtype:SInt,
    inNo:Int,
    outNo:Int
  ) : Boolean = {
    val weights = TestTools.getTwoDimArryAsSInt(wfname,dtype)
    val bias = TestTools.getOneDimArryAsSInt(bfname,dtype)
    //print(chisel3.Driver.emitVerilog(new DenseLayer(dtype,inNo,outNo,bias,weights))
    Driver(() =>new DenseLayer(dtype,inNo,outNo,bias,weights)){
      d => new DenseLayerTester(d,ifname,rfname,dtype)
    }
  }

  class DenseLayerTester(c : DenseLayer,ifname:String,rfname:String,dtype:SInt) extends PeekPokeTester(c){
    val inputs: Seq[SInt] = TestTools.getOneDimArryAsSInt(ifname,dtype)
    for( i <- inputs.indices ){
      poke(c.io.dataIn(i),inputs(i))
    }
    TestTools.writeRowToCsv(peek(c.io.dataOut).toList, rfname)//write result
  }

  def main():Unit = {
    val weights_file_name = "dense1_weights.csv"
    val bias_file_name = "dense1_weights_bias.csv"
    val input_file_name = "dense_output_0.csv"
    val result_file_name = "test_dense1_output_0.csv"
    runDenseTester(weights_file_name,bias_file_name,input_file_name,result_file_name,SInt(16.W),30,10)
  }

  main()
}