
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

    val io = IO(new Bundle(){
      val inputBundle = new InputBundle(
        dtype = dtype,
        dataWidth = dataWidth,
        dataHeight = dataHeight
      )
      val output = Decoupled(Vec(filterHeight*dataWidth,dtype))
      val mem_output = Output(Vec(dataWidth,dtype))
    })

    val getInputImg = Module(new GetInputImg(
      dtype = dtype,
      dataWidth = dataWidth,
      dataHeight = dataHeight,
      filterHeight = filterHeight,
    ))

    getInputImg.io.inputBundle <> io.inputBundle
    io.mem_output := getInputImg.io.dataOut.bits

    val convBufferLine = Module(new ConvBufferLine(dtype,filterHeight,dataWidth))
    convBufferLine.io.dataIn <> getInputImg.io.dataOut
    io.output <> convBufferLine.io.dataOut
  }

  class InputBufferTester(c : InputBuffer,img:Seq[Seq[SInt]]) extends PeekPokeTester(c){
    //write to mem
    poke(c.io.inputBundle.dataReady , false.B)
    poke(c.io.inputBundle.write,false.B)
    for(i <- img.indices){
      for( j <- img(i).indices){
        poke(c.io.inputBundle.write_data(j),img(i)(j))
      }
      poke(c.io.inputBundle.write_addr,i.asUInt(log2Ceil(img.size).W))
      poke(c.io.inputBundle.write,true.B)
      step(1)
      poke(c.io.inputBundle.write,false.B)
    }

    var k = -1
    poke(c.io.inputBundle.dataReady , true.B)
    for( i <- 0 until 120){
      step(1)
      if(peek(c.io.output.valid) == 1){
        k = k + 1
        //println("output.ready: " + peek(c.io.output.ready))
        println("output.bits: " + peek(c.io.output.bits))
        val right = getOutput(img,k,5)
        for(i <- right.indices){
          expect(c.io.output.bits(i),right(i))
        }
      }
    }
  }

  //test mem init with img
  def getOutput(img:Seq[Seq[SInt]],baseIndex:Int,height:Int) : Seq[SInt] = {
    val result = (0 until height).map(i =>{
      img(i + baseIndex)
    }).toList.flatten
    result
  }

  def runInputBufferInit():Unit = {
    val input = "test_cnn/input_2d_7.csv"
    val img = TestTools.getTwoDimArryAsSIntWithOutTrans(input,SInt(16.W),4)
    Driver(() => new InputBuffer(SInt(16.W),28,28,5)){
      c => new InputBufferTester(c,img)
    }
  }

  runInputBufferInit()
}
