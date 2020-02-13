
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

class FlattenLayerTester(f : FlattenLayer) extends PeekPokeTester(f){
  print(chisel3.Driver.emitVerilog(new FlattenLayer(SInt(32.W),2)))
}

object FlattenLayerTesterRuner extends App{
  val testResult = Driver(() => new FlattenLayer(SInt(32.W),1)){
    f => new FlattenLayerTester(f)
  }
}