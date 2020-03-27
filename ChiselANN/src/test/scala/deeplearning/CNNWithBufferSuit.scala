
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import chisel3.util.log2Ceil

object CNNWithBufferSuit extends App {
  class CNNWithBufferTester(c:CNNWithBuffer,img:Seq[Seq[SInt]]) extends PeekPokeTester (c){
    //write img
    poke(c.io.inputBundle.dataReady,false.B)
    poke(c.io.inputBundle.write,false.B)
    for(i <- img.indices){
      for(j <- img(i).indices){
        poke(c.io.inputBundle.write_data(j),img(i)(j))
      }
      poke(c.io.inputBundle.write_addr,i.asUInt(log2Ceil(img.size).W))
      poke(c.io.inputBundle.write,true.B)
      step(1)
      poke(c.io.inputBundle.write,false.B)
    }
    //start cnn
    poke(c.io.inputBundle.dataReady,true.B)
    var i : Int= 0
    while (peek(c.io.dataOut.valid)==0){
      print("the cycles " + i + ": result is : " + peek(c.io.dataOut.bits) + "\n")
      step(1)
      i = i + 1
    }
    expect(c.io.dataOut.bits,7.U(log2Ceil(10).W))
  }

  def runCnnWBTester(
    cbfname:String,//conv bias file name
    cwfname:Seq[String],//conv weights file name List
    dbfname:String,//dense bias file name
    dwfname:String,//dense weights file name
    ifname:String,
    rfname:String,
    dtype:SInt,
    frac_bits: Int) : Unit = {
    val conv_weights = cwfname.indices.map(i => {
      val temp_weights = TestTools.getTwoDimArryAsSIntWithOutTrans(cwfname(i),dtype,frac_bits)
      temp_weights
    }).toList
    val conv_bias = TestTools.getOneDimArryAsSInt(cbfname,dtype,frac_bits)
    val dense_bias = TestTools.getOneDimArryAsSInt(dbfname,dtype,frac_bits)
    val dense_weights = TestTools.getTwoDimArryAsSInt(dwfname,dtype,frac_bits)
    val img = TestTools.getTwoDimArryAsSIntWithOutTrans(ifname,dtype,frac_bits)
    Driver(() => new CNNWithBuffer(
      dtype = dtype,
      dense_bias = dense_bias,
      dense_weights = dense_weights,
      conv_bias = conv_bias,
      conv_weights = conv_weights,
      frac_bits = frac_bits)){
      c => new CNNWithBufferTester(c,img)
    }
    /*val args = Array("-o", "CNNWithBuffer.v",
      "-X", "verilog",
      "--no-check-comb-loops",
      "--no-dce",
      "--info-mode=ignore",
      )
    chisel3.Driver.execute(args, () => new  CNNWithBuffer(
      dtype = dtype,
      dense_bias = dense_bias,
      dense_weights = dense_weights,
      conv_bias = conv_bias,
      conv_weights = conv_weights,
      frac_bits = frac_bits))*/
  }

  def testCNNWB():Unit={
    //get conv bias and weights
    val conv_weights_0 = "test_cnn/conv0_weights.csv"
    val conv_weights_1 = "test_cnn/conv1_weights.csv"
    val conv_weights_2 = "test_cnn/conv2_weights.csv"
    val conv_weights :Seq[String] = Seq(conv_weights_0,conv_weights_1,conv_weights_2)
    val conv_bias = "test_cnn/conv_bias.csv"

    //get dense bias and weight
    val dense_weights = "test_cnn/dense_weights.csv"
    val dense_bias = "test_cnn/dense_bias.csv"

    val input_img = "test_cnn/input_2d_7.csv"
    val result = "test/SInt16Frac8/test_output"

    runCnnWBTester(conv_bias,conv_weights,dense_bias,dense_weights,
      input_img,result,SInt(16.W),8)
  }
  //testCNNWB()

  def transformImg(img:Seq[SInt],width:Int):Seq[Seq[SInt]] = {
    val height:Int = img.size/width
    (0 until width).map(i =>{
      (0 until height).map(j =>{
        val unit:SInt = img(i + j*width)
        unit
      }).toList
    }).toList.transpose
  }

  class CNNTester(
                           c : CNNWithBuffer,
                           imgFile:String,
                           labelFile:String,
                           resultFile:String,
                           frac_bits:Int,
                           dtype:SInt
                         ) extends PeekPokeTester(c){

    for(b <- 1 until 10){
      val img = imgFile + "_" + b + ".csv"
      val label = labelFile + "_" + b + ".csv"
      val input_images: Seq[Seq[SInt]] = TestTools.getTwoDimArryAsSIntWithOutTrans(img ,dtype,frac_bits)
      val input_labels: Seq[UInt] = TestTools.getOneDimArryAsUInt(label ,UInt(log2Ceil(10).W),0)
      var output = Seq[BigInt]()
      var log = Seq[String]()
      var right_no : Int = 0
      for(testIndex <- input_images.indices){
        //write img_data to imgMem
        val img_data = transformImg(input_images(testIndex),28)
        poke(c.io.inputBundle.dataReady,false.B)
        poke(c.io.inputBundle.write,false.B)
        for(i <- img_data.indices){
          for(j <- img_data(i).indices){
            poke(c.io.inputBundle.write_data(j),img_data(i)(j))
          }
          poke(c.io.inputBundle.write_addr,i.asUInt(log2Ceil(img_data.size).W))
          poke(c.io.inputBundle.write,true.B)
          step(1)
          poke(c.io.inputBundle.write,false.B)
        }
        //start cnn
        poke(c.io.inputBundle.dataReady,true.B)

        while (peek(c.io.dataOut.valid)==0){
          step(1)
        }

        val result = expect(c.io.dataOut.bits,input_labels(testIndex))
        if(result){
          right_no = right_no + 1
          print("[pass] got " + peek(c.io.dataOut.bits) + " expected " + input_labels(testIndex).toInt + "\n")
        } else {
          val log_msg = "[error] got " + peek(c.io.dataOut.bits) + " expected " + input_labels(testIndex).toInt
          log = log :+ log_msg
          print(log_msg)
        }

        val out = peek(c.io.dataOut.bits)
        output = output :+ out
      }
      TestTools.writeRowToCsv(output,resultFile + "_" + b + ".csv")
      TestTools.writeRowToCsv(log,"test/SInt16Frac10/wrong_msg_" + b + ".csv")
      print("the right number is " + right_no + "/" + input_labels.size + "\n")
    }
  }

  def runCNNTester(
                  cwfname:Seq[String],
                  cbfname:String,
                  dbfname:String,
                  dwfname:String,
                  ifname:Seq[String],
                  rfname:String,
                  dtype:SInt,
                  frac_bits:Int,
                  ):Unit = {
    val conv_weights = cwfname.indices.map(i => {
      val temp_weights = TestTools.getTwoDimArryAsSIntWithOutTrans(cwfname(i),dtype,frac_bits)
      temp_weights
    }).toList
    val conv_bias = TestTools.getOneDimArryAsSInt(cbfname,dtype,frac_bits)
    val dense_bias = TestTools.getOneDimArryAsSInt(dbfname,dtype,frac_bits)
    val dense_weights = TestTools.getTwoDimArryAsSInt(dwfname,dtype,frac_bits)

    Driver(() => new CNNWithBuffer(
      dtype = dtype,
      dense_bias = dense_bias,
      dense_weights = dense_weights,
      conv_bias = conv_bias,
      conv_weights = conv_weights,
      frac_bits = frac_bits)){
      c => new CNNTester(
        c = c,
        imgFile = ifname.head,
        labelFile = ifname(1),
        resultFile = rfname,
        dtype = dtype,
        frac_bits = frac_bits
      )
    }
  }
  def runCNNTest():Unit = {
    //get conv bias and weights
    val conv_weights_0 = "test_cnn/conv0_weights.csv"
    val conv_weights_1 = "test_cnn/conv1_weights.csv"
    val conv_weights_2 = "test_cnn/conv2_weights.csv"
    val conv_weights :Seq[String] = Seq(conv_weights_0,conv_weights_1,conv_weights_2)
    val conv_bias = "test_cnn/conv_bias.csv"

    //get dense bias and weight
    val dense_weights = "test_cnn/dense_weights.csv"
    val dense_bias = "test_cnn/dense_bias.csv"

    val input_img = "test/test_images"
    val input_label = "test/test_labels"
    val input : Seq[String] = Seq(input_img,input_label)

    val result = "test/SInt16Frac10/test_output"

    runCNNTester(
      cbfname = conv_bias,
      cwfname = conv_weights,
      dbfname = dense_bias,
      dwfname = dense_weights,
      ifname = input,
      rfname = result,
      dtype = SInt(16.W),
      frac_bits = 10)
  }
  runCNNTest()
}
