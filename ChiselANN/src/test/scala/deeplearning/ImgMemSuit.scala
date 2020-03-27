
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import chisel3.util.log2Ceil

object ImgMemSuit extends App {
  //chisel3.Driver.execute(args, () => new BufferMem(SInt(16.W),10))
  class ImgMemTester(c:ImgMem,img : Seq[Seq[SInt]]) extends PeekPokeTester(c){
    //write to mem
    poke(c.io.write,false.B)
    for(i <- img.indices){
      for(j <- img.head.indices){
        poke(c.io.dataIn(j),img(i)(j))
      }
      poke(c.io.addr,i.asUInt(log2Ceil(img.size).W))
      poke(c.io.write,true.B)
      step(1)
      poke(c.io.write,false.B)
    }
    step(1)
    for(i <- img.indices){
      poke(c.io.addr,i.asUInt(log2Ceil(img.size).W))
      print("mem[" + i + "]: " + peek(c.io.dataOut) + "\n")
    }
  }

  def runWithImg() : Unit = {
    val input = "test_cnn/input_2d_7.csv"
    val img = TestTools.getTwoDimArryAsSIntWithOutTrans(input,SInt(16.W),4)
    Driver(() => new ImgMem(SInt(16.W),28,28)){
      c => new ImgMemTester(c,img)
    }
  }
  runWithImg()
}
