
package deeplearning

import chisel3._
import chisel3.util._
import chisel3.iotesters.{Driver, PeekPokeTester}

object ConvBufferOutputSuit extends App {
  class ConvBufferOutputTester(c:ConvBufferOutput) extends PeekPokeTester(c) {
    var value = 0
    for(h <-0 until 4){
      for(b <- 0 until 2){
        for(w <- 0 until 4){
          poke(c.io.dataIn.bits(b)(w),value.asSInt(16.W))
          value = value + 1
        }
      }
      poke(c.io.dataIn.valid,true.B)
      step(1)
      poke(c.io.dataIn.valid,false.B)
      print("output bits: " + peek(c.io.dataOut.bits) + "\n")
      print("output valid: " + peek(c.io.dataOut.valid) + "\n")
      print("output addr: " + peek(c.io.addr) + "\n")
    }
  }

  def runConvBufferOutputTest(): Unit ={
    Driver(() => new ConvBufferOutput(
      dtype = SInt(16.W),
      height = 4,
      width = 4,
      batch = 2
    )){
      c => new ConvBufferOutputTester(c)
    }
  }

  runConvBufferOutputTest()
}
