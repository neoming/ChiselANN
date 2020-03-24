
package deeplearning

import chisel3._
import chisel3.util._
import chisel3.iotesters.{Driver, PeekPokeTester}

object ConvLayerSuit extends App {
  class ConvLayerWithInput(
   dtype : SInt,
   img : Option[Seq[Seq[SInt]]],
   conv_bias : Seq[SInt],
   conv_weights :  Seq[Seq[Seq[SInt]]],
   frac_bits: Int = 0,
  ) extends Module{

    val dataWidth = 28
    val dataHeight = 28
    val filterWidth = 5
    val filterHeight = 5
    val filterBatch = 3
    val strideHeight = 1
    val strideWidth = 1
    val output = 10

    val outputWidth: Int = (dataWidth - filterWidth + 1 )/strideWidth
    val outputHeight: Int = (dataHeight - filterHeight + 1 )/strideHeight
    val outputNo: Int = outputWidth * outputHeight * filterBatch
    val neuronInputNo: Int = filterHeight * filterWidth

    val io = IO(new Bundle{
      val inputBundle = new InputBundle(dtype = dtype,dataWidth = dataWidth,dataHeight = dataHeight)
      val dataOut = Decoupled(Vec(outputNo,dtype))
      //for debug
      val conv_buffer = Output(Vec(filterHeight*dataWidth,dtype))
      val conv_buffer_valid = Output(Bool())
      val conv_line = Output(Vec(filterBatch,Vec(outputWidth,dtype)))
      val conv_line_valid = Output(Bool())
    })

    val getInputImg = Module(new GetInputImg(
      dtype = dtype,
      dataWidth = dataWidth,
      dataHeight = dataWidth,
      filterHeight = filterHeight,
      img = img,
    ))

    val convLayer = Module(new ConvLayer(
      dtype = dtype,
      weights = conv_weights,
      bias = conv_bias,
      dataWidth = dataWidth,
      dataHeight = dataHeight,
      filterHeight = filterHeight,
      filterWidth = filterWidth,
      filterBatch = filterBatch,
      strideHeight = strideHeight,
      strideWidth = strideWidth,
      frac_bits = frac_bits
    ))

    getInputImg.io.inputBundle <> io.inputBundle
    convLayer.io.dataIn <> getInputImg.io.dataOut
    io.dataOut <> convLayer.io.dataOut

    val latency : Int = convLayer.latency
    //for debug
    io.conv_line := convLayer.io.conv_line
    io.conv_line_valid := convLayer.io.conv_line_valid
    io.conv_buffer_valid := convLayer.io.conv_buffer_valid
    io.conv_buffer := convLayer.io.conv_buffer
  }

  class ConvLayerTester(
   c:ConvLayerWithInput,
   rfname:String,
   dtype:SInt,
  ) extends PeekPokeTester(c){

    poke(c.io.inputBundle.dataReady,true.B)
    print(" the valid signal is :" + peek(c.io.dataOut.valid) + '\n')
    var i = 0
    for(k <- 0 until c.latency){
      step(1)
      i = i + 1
      if(peek(c.io.conv_line_valid) == 1){
        print("cycle " + i + ": conv_line = " + peek(c.io.conv_line) + " ;\n" )
      }
      if(peek(c.io.conv_buffer_valid) == 1){
        print("cycle " + i + ": conv_buffer = " + peek(c.io.conv_buffer) + " ;\n" )
      }
    }
    step(1)
    print("after " + c.latency + " cycles, the valid signal is :" + peek(c.io.dataOut.valid) + '\n')
    TestTools.writeRowToCsv(peek(c.io.dataOut.bits).toList, rfname)//write result
  }

  def runConvTester(
   wfnames:Seq[String],//weights file name
   bfname:String,//bias file name
   ifname:String,//input file name
   rfname:String,//test result file name
   dtype:SInt,
   frac_bits:Int,
  ) : Boolean = {

    val weights = wfnames.indices.map(i => {
      val temp_weights = TestTools.getTwoDimArryAsSIntWithOutTrans(fname = wfnames(i),dtype = dtype,frac = frac_bits)
      temp_weights
    }).toList

    val bias = TestTools.getOneDimArryAsSInt(fname = bfname,dtyp = dtype,frac = frac_bits)
    val img = TestTools.getTwoDimArryAsSIntWithOutTrans(fname = ifname,dtype = dtype,frac = frac_bits)
    Driver(() => new ConvLayerWithInput(
      dtype = dtype,
      img = Some(img),
      conv_weights = weights,
      conv_bias = bias,
      frac_bits = frac_bits
    )){
      c => new ConvLayerTester(c,rfname,dtype)
    }
  }

  def testConv():Unit = {
    val weights_0 = "test_cnn/conv0_weights.csv"
    val weights_1 = "test_cnn/conv1_weights.csv"
    val weights_2 = "test_cnn/conv2_weights.csv"
    val weights :Seq[String] = Seq(weights_0,weights_1,weights_2)
    val bias = "test_cnn/conv_bias.csv"
    val input = "test_cnn/input_2d_7.csv"
    val result = "test_cnn/test_conv_with_buffer_output_7.csv"
    runConvTester(weights,bias,input,result,SInt(16.W),frac_bits = 4)
  }

  testConv()

  def testFlatten():Unit = {
    val a : Seq[Seq[Seq[Int]]] = (0 until 3).map(i =>{
      (0 until 3).map(j =>{
        (0 until 3).map(k =>{
          val value = i * 100 + j*10 + k
          value
        }).toList
      }).toList
    }).toList

    val b = a.flatten.flatten
    println(a)
    println(a.flatten)
    println(a.flatten.flatten)
    //println(b(1)(2)(3))
  }
}
