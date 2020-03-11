
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object ShifterSignalSuit extends App {
  class ShifterSignalTester(c:ShiftSignal) extends PeekPokeTester(c){
    poke(c.io.dataIn.bits,12.S(16))
    poke(c.io.dataIn.valid,true.B)
    for(i <- 0 until 4){
      step(1)
      print("dataOut valid " + peek(c.io.dataOut.valid) + "\n")
    }
    poke(c.io.dataIn.valid,false.B)
    for(i <- 0 until 4){
      step(1)
      print("dataOut valid " + peek(c.io.dataOut.valid) + "\n")
    }
  }
  Driver(() => new ShiftSignal()){
    c => new ShifterSignalTester(c)
  }
}
