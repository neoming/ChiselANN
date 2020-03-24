
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver,PeekPokeTester}
import chisel3.util._

object RomSuit extends App {
  class WeightRom(
                 dtype:SInt,
                 weights:Seq[Seq[Seq[SInt]]],
                 )extends Module{
    val io = IO(new Bundle() {
      val read_addr = Input(UInt(log2Ceil(weights.flatten.flatten.size).W))
      val read_data = Output(dtype)
      val data = Output(Vec(weights.flatten.flatten.size,dtype))
    })
    val weight_rom = VecInit(weights.flatten.flatten)
    io.data <> weight_rom
    io.read_data := weight_rom(io.read_addr)
  }

  class WeightRomTester( c : WeightRom,weights : Seq[Seq[Seq[SInt]]]) extends PeekPokeTester(c){
    for(f <- weights.indices){
      for(w <- weights.head.indices){
        for(h <- weights.head.head.indices){
          val index = f*weights.head.size*weights.head.head.size + h * weights.head.size + w
          poke(c.io.read_addr,index)
          expect(c.io.read_data,weights(f)(h)(w))
        }
      }
    }
    print("output: " + peek(c.io.data) + "\n")
  }

  def runWeightRom():Unit ={
    val weights_0 = "test_cnn/conv0_weights.csv"
    val weights_1 = "test_cnn/conv1_weights.csv"
    val weights_2 = "test_cnn/conv2_weights.csv"
    val weights :Seq[String] = Seq(weights_0,weights_1,weights_2)
    val conv_weight = weights.indices.map(i => {
      val temp_weights = TestTools.getTwoDimArryAsSIntWithOutTrans(fname = weights(i),dtype = SInt(16.W),frac = 4)
      temp_weights
    }).toList
    Driver(() => new WeightRom(SInt(16.W),weights = conv_weight)){
      c => new WeightRomTester(c,conv_weight)
    }
  }
  runWeightRom()
}
