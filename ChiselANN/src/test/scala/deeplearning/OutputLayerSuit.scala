
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object OutputLayerSuit extends App{

  class OutputLayerTester(
    c : OutputLayer,
    ifname:String,
    rfname:String,
    dtype:SInt
  )extends PeekPokeTester(c){
    val inputs: Seq[SInt] = TestTools.getOneDimArryAsSInt(ifname,dtype)
    for(i <- inputs.indices){poke(c.io.dataIn.bits(i),inputs(i))}
    poke(c.io.dataIn.valid,true.B)

    for(i <- 0 until c.latency){
      step(1)
      print("the " + i + " cycles output is " + peek(c.io.dataOut.bits) + " valid is " + peek(c.io.dataOut.valid) + "\n")
    }
    expect(c.io.dataOut.bits,7.U)
  }

  def runOutputTester(
   ifname:String,
   rfname:String,
   dtype:SInt,
   inNo:Int,
  ) : Unit = {
    Driver(() => new OutputLayer(dtype,inNo)){
      o => new OutputLayerTester(o,ifname,rfname,dtype)
    }
  }

  def testOutputLayer():Unit = {
    val input_file_name = "dense1_output_7.csv"
    runOutputTester(input_file_name,"",SInt(16.W),10)
  }

  testOutputLayer()
}
