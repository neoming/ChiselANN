
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object MaxMuxSuit extends App {
  class MaxMuxTester(
    c : MaxMux,
    dtype : SInt,
    inputs : Seq[SInt],
    output : SInt,
  ) extends PeekPokeTester (c){
    for ( i <- inputs.indices ){
      poke(c.io.dataIn(i),inputs(i))
    }
    for(i <- 0 until c.latency){
      step(1)
      print("the result at " + i + " cycle: " + peek(c.io.dataOut)  + "\n")
    }
    expect(c.io.dataOut,output)
  }

  def runMaxMuxTester(
    dtype : SInt,
    inputNo : Int,
    inputs : Seq[SInt],
    output: SInt,
  ) : Boolean = {
    Driver(() => new MaxMux(dtype,inputNo)){
      m => new MaxMuxTester(m,dtype,inputs,output)
    }
  }

  def runMaxMux():Unit = {
    val inputs : Seq[SInt] = Seq(12.S,4.S,3.S,4.S)
    val output : SInt = 12.S
    runMaxMuxTester(SInt(16.W),4,inputs,output)
  }

  runMaxMux()
}
