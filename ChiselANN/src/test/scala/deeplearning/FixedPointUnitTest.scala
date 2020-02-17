
package deeplearning

import chisel3.Mux
import chisel3.experimental.FixedPoint
import chisel3.iotesters.{Driver, PeekPokeTester}


class FixedPointUnitTest(f : FixedPointCalculation) extends PeekPokeTester(f){

  print(f.io.output)
  print(chisel3.Driver.emitVerilog(new FixedPointCalculation))
}

object TestRuner extends App{
  val testResult = Driver(() => new FixedPointCalculation){
    f => new FixedPointUnitTest(f)
  }
}