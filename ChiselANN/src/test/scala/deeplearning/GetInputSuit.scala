
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import chisel3.util.{Decoupled, log2Ceil}

object GetInputSuit extends App {
  class InputBuffer(
                     dtype : SInt,
                     dataWidth : Int,
                     dataHeight : Int,
                     filterHeight : Int,
                   ) extends Module{

    val io = IO(new Bundle() {
      val write = Input(Bool())
      val write_addr = Input(UInt(log2Ceil(dataWidth).W))
      val write_data = Input(Vec(dataWidth,dtype))
      val dataReady = Input(Bool())
      val output = Decoupled(Vec(filterHeight*dataWidth,dtype))
      val mem_output = Output(Vec(dataWidth,dtype))
    })

    val getInputImg = Module(new GetInputImg(dtype,dataWidth,dataHeight,filterHeight))

    getInputImg.io.write := io.write
    getInputImg.io.write_addr := io.write_addr
    getInputImg.io.write_data := io.write_data
    getInputImg.io.dataReady := io.dataReady
    io.mem_output := getInputImg.io.dataOut.bits

    val convBufferLine = Module(new ConvBufferLine(dtype,filterHeight,dataWidth))
    convBufferLine.io.dataIn <> getInputImg.io.dataOut
    io.output <> convBufferLine.io.dataOut
  }

  class InputTester(c : InputBuffer) extends PeekPokeTester(c){
    //write to mem
    poke(c.io.dataReady , false.B)
    for(j <- 0 until 4){
      val line = (0 until 10).map( k => {
        val a = j*10 + k
        a.asSInt(16.W)
      }).toList

      for( k <- line.indices){
        poke(c.io.write_data(k),line(k))
      }
      poke(c.io.write_addr,j)
      poke(c.io.write,true.B)
      step(1)
      poke(c.io.write,false.B)
    }

    poke(c.io.dataReady , true.B)
    for( i <- 0 until 4){
      step(1)
      println("output.valid: " + peek(c.io.output.valid))
      println("output.ready: " + peek(c.io.output.ready))
      println("output.bits: " + peek(c.io.output.bits))
      println("mem_output.bits: " + peek(c.io.mem_output))
    }
  }

  def runInputBuffer():Unit = {
    Driver(() => new InputBuffer(SInt(16.W), 10, 4, 2))(c => new InputTester(c))
  }

  class GetInputImgTester(c : GetInputImg) extends PeekPokeTester(c){
    poke(c.io.write,false.B)
    poke(c.io.dataOut.ready,true.B)
    for(j <- 0 until 6){
      val line = (0 until 10).map( k => {
        val a = j + k
        a.asSInt(16.W)
      }).toList
      for( k <- line.indices){
        poke(c.io.write_data(k),line(k))
      }
      poke(c.io.write_addr,j)
      poke(c.io.write,true.B)
      step(1)
      poke(c.io.write,false.B)
    }
    poke(c.io.dataReady,true.B)
    for(i <- 0 until 9){
      println(peek(c.io.read_addr).toString())
      step(1)
    }
  }

  def runGetInputImg():Unit = {
    Driver(() => new GetInputImg(SInt(16.W),10,6,3)){
      c => new GetInputImgTester(c)
    }
  }
  runInputBuffer()
}
