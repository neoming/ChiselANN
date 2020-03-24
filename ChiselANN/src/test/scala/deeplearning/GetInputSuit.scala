
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
                     img : Option[Seq[Seq[SInt]]]
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
      img = img,
    ))

    getInputImg.io.inputBundle <> io.inputBundle
    io.mem_output := getInputImg.io.dataOut.bits

    val convBufferLine = Module(new ConvBufferLine(dtype,filterHeight,dataWidth))
    convBufferLine.io.dataIn <> getInputImg.io.dataOut
    io.output <> convBufferLine.io.dataOut
  }

  class InputTester(c : InputBuffer) extends PeekPokeTester(c){
    //write to mem
    poke(c.io.inputBundle.dataReady , false.B)
    for(j <- 0 until 28){
      val line = (0 until 5).map( k => {
        val a = j*10 + k
        a.asSInt(16.W)
      }).toList

      for( k <- line.indices){
        poke(c.io.inputBundle.write_data(k),line(k))
      }
      poke(c.io.inputBundle.write_addr,j.asUInt(5.W))
      poke(c.io.inputBundle.write,true.B)
      step(1)
      poke(c.io.inputBundle.write,false.B)
    }

    poke(c.io.inputBundle.dataReady , true.B)
    for( i <- 0 until 200){
      step(1)
      if(peek(c.io.output.valid) == 1){
        println("output.valid: " + peek(c.io.output.valid))
        //println("output.ready: " + peek(c.io.output.ready))
        println("output.bits: " + peek(c.io.output.bits))
        println("mem_output.bits: " + peek(c.io.mem_output))
      }
    }
  }

  def runInputBuffer():Unit = {
    Driver(() => new InputBuffer(SInt(16.W), 5, 28, 5,None))(c => new InputTester(c))
  }

  class GetInputImgTester(c : GetInputImg) extends PeekPokeTester(c){
    poke(c.io.inputBundle.write,false.B)
    poke(c.io.dataOut.ready,true.B)
    for(j <- 0 until 20){
      val line = (0 until 10).map( k => {
        val a = j + k
        a.asSInt(16.W)
      }).toList
      for( k <- line.indices){
        poke(c.io.inputBundle.write_data(k),line(k))
      }
      poke(c.io.inputBundle.write_addr,j)
      poke(c.io.inputBundle.write,true.B)
      step(1)
      poke(c.io.inputBundle.write,false.B)
    }
    poke(c.io.inputBundle.dataReady,true.B)
    for(i <- 0 until 200){
      //println("read_addr: " + peek(c.io.read_addr(0)) + " addr: " + peek(c.io.read_addr(1)) + " base_addr: " + peek(c.io.read_addr(2)))
      step(1)
    }
  }

  def runGetInputImg():Unit = {
    Driver(() => new GetInputImg(SInt(16.W),10,20,5,None)){
      c => new GetInputImgTester(c)
    }
  }

  //test mem init with img
  def getOutput(img:Seq[Seq[SInt]],baseIndex:Int,height:Int) : Seq[SInt] = {
    val result = (0 until height).map(i =>{
      img(i + baseIndex)
    }).toList.flatten
    result
  }

  class InputInitTester(c : InputBuffer,img:Seq[Seq[SInt]]) extends PeekPokeTester(c){
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

  def runInputBufferInit():Unit = {
    val input = "test_cnn/input_2d_7.csv"
    val img = TestTools.getTwoDimArryAsSIntWithOutTrans(input,SInt(16.W),4)
    Driver(() => new InputBuffer(SInt(16.W),28,28,5,Some(img))){
      c => new InputInitTester(c,img)
    }
  }
  //runGetInputImg()
  //runInputBuffer()
  runInputBufferInit()
}
