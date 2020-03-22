
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object ConvBufferLineSuit extends App {

  class ConvBufferLineTester(c : ConvBufferLine) extends PeekPokeTester(c){
    val line1:Seq[SInt] = (0 until 10).map( i =>{
      i.asSInt(16.W)
    }).toList
    val line2:Seq[SInt] = (10 until 20).map( i =>{
      i.asSInt(16.W)
    }).toList


    for(i <- c.io.dataIn.bits.indices){
      poke(c.io.dataIn.bits(i),line1(i))
    }
    println("dataout.ready: " + peek(c.io.dataIn.ready))
    poke(c.io.dataIn.valid,true.B)
    step(1)
    for(i <- c.io.dataIn.bits.indices){
      poke(c.io.dataIn.bits(i),line2(i))
    }
    println("dataout.valid: " + peek(c.io.dataOut.valid))
    println("dataout.ready: " + peek(c.io.dataIn.ready))
    println("result:" + peek(c.io.dataOut.bits))
    step(1)
    poke(c.io.dataIn.valid,false.B)
    println("dataout.valid: " + peek(c.io.dataOut.valid))
    println("dataout.ready: " + peek(c.io.dataIn.ready))
    println("result:" + peek(c.io.dataOut.bits))
    step(1)
    println("dataout.valid: " + peek(c.io.dataOut.valid))
    println("dataout.ready: " + peek(c.io.dataIn.ready))
    println("result:" + peek(c.io.dataOut.bits))
    step(1)
    println("dataout.valid: " + peek(c.io.dataOut.valid))
    println("dataout.ready: " + peek(c.io.dataIn.ready))
    println("result:" + peek(c.io.dataOut.bits))
    step(1)
    println("dataout.valid: " + peek(c.io.dataOut.valid))
    println("dataout.ready: " + peek(c.io.dataIn.ready))
    println("result:" + peek(c.io.dataOut.bits))
  }
  Driver(() => new ConvBufferLine(SInt(16.W),2,10)){
    c => new ConvBufferLineTester(c)
  }

}
