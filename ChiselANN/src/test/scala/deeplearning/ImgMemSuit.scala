
package deeplearning

import chisel3._
import chisel3.iotesters. {PeekPokeTester,Driver}

object ImgMemSuit extends App {
  //chisel3.Driver.execute(args, () => new BufferMem(SInt(16.W),10))
  class ImgMemTester(c:ImgMem) extends PeekPokeTester(c){
    //write to mem
    poke(c.io.write , false.B)
    for(j <- 0 until 4){
      val line = (0 until 10).map( k => {
        val a = j + k
        a.asSInt(16.W)
      }).toList
      for( k <- line.indices){
        poke(c.io.dataIn(k),line(k))
      }
      println(line.toString())
      poke(c.io.addr,j)
      poke(c.io.write,true.B)
      step(1)
      poke(c.io.write,false.B)
    }
    for(j <- 0 until 4){
      poke(c.io.addr,j)
      println("mem[" + j + "]: " + peek(c.io.dataOut))
    }
  }

  Driver(() => new ImgMem(SInt(16.W),4,10)){
    c => new ImgMemTester(c)
  }

}
