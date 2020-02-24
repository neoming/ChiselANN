
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object OutputLayerSuit extends App{

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

  class OutputLayerTester(
    c : OutputLayer,
    ifname:String,
    rfname:String,
    dtype:SInt
  )extends PeekPokeTester(c){
    val inputs: Seq[SInt] = TestTools.getOneDimArryAsSInt(ifname,dtype)
    for(i <- inputs.indices){poke(c.io.input.bits(i),inputs(i))}
    poke(c.io.input.valid,true.B)

    for(i <- 0 until c.latency){
      step(1)
      print("the " + i + " cycles output is " + peek(c.io.output.bits) + " valid is " + peek(c.io.output.valid) + "\n")
    }
    expect(c.io.output.bits,7.U)
  }

  def testOutputLayer():Unit = {
    val input_file_name = "dense1_output_7.csv"
    runOutputTester(input_file_name,"",SInt(16.W),10)
  }

  testOutputLayer()
}
