
package deeplearning

import chisel3.iotesters.{Driver, PeekPokeTester}

object FixedPointUnitTest extends App{
  
  val testResult = Driver(() => new FixedPointCalculation){
    f => new FixedPointUnitTester(f)
  }

  class FixedPointUnitTester(f : FixedPointCalculation) extends PeekPokeTester(f){
    print(f.io.output)
    print(chisel3.Driver.emitVerilog(new FixedPointCalculation))
  }
}