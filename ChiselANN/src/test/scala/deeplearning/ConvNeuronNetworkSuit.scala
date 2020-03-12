
package deeplearning

import java.io.File

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import chisel3.util.log2Ceil
import com.github.tototoshi.csv.CSVWriter
import deeplearning.TestTools.src_path

object ConvNeuronNetworkSuit extends App {
  class CNNTester(
    c :ConvNeuronNetwork,
    ifname:Seq[String],
    rfname:String,
    dtype:SInt,
    frac_bits:Int
  ) extends PeekPokeTester(c){

    for(b <- 0 until 10){
      val img = ifname.head + "_" + b + ".csv"
      val label = ifname(1) + "_" + b + ".csv"
      val input_images: Seq[Seq[SInt]] = TestTools.getTwoDimArryAsSIntWithOutTrans(img ,dtype,frac_bits)
      val input_labels: Seq[UInt] = TestTools.getOneDimArryAsUInt(label ,UInt(log2Ceil(10).W),0)
      var output = Seq[BigInt]()
      var log = Seq[String]()
      var right_no : Int = 0
      for(i <- input_images.indices){
        print("the " + i + " test start:\n")
        for( j <- input_images(i).indices){
          poke(c.io.dataIn.bits(j),input_images(i)(j))
        }
        poke(c.io.dataIn.valid,true.B)
        step(1)
        poke(c.io.dataIn.valid,false.B)
        step(c.latency - 1)
        val result = expect(c.io.dataOut.bits,input_labels(i))
        if(result){
          right_no = right_no + 1
        } else {
          val log_msg = "[error] got " + peek(c.io.dataOut.bits) + " expected " + input_labels(i).toInt
          log = log :+ log_msg
          print(log_msg)
        }
        print("the " + i + " test result is:" + peek(c.io.dataOut.bits) + " expect " + input_labels(i).toInt + "\n")
        val out = peek(c.io.dataOut.bits)
        output = output :+ out
      }
      TestTools.writeRowToCsv(output,rfname + "_" + b + ".csv")
      TestTools.writeRowToCsv(log,"test/SInt16Frac8/wrong_msg_" + b + ".csv")
      print("the right number is " + right_no + "/" + input_labels.size + "\n")
    }
  }

  def runCNNTester(
    cbfname:String,//conv bias file name
    cwfname:Seq[String],//conv weights file name List
    dbfname:String,//dense bias file name
    dwfname:String,//dense weights file name
    ifname:Seq[String],
    rfname:String,
    dtype:SInt,
    frac_bits: Int
  ) : Boolean = {
    val conv_weights = cwfname.indices.map(i => {
      val temp_weights = TestTools.getTwoDimArryAsSInt(cwfname(i),dtype,frac_bits)
      temp_weights
    }).toList
    val conv_bias = TestTools.getOneDimArryAsSInt(cbfname,dtype,frac_bits)

    val dense_bias = TestTools.getOneDimArryAsSInt(dbfname,dtype,frac_bits)
    val dense_weights = TestTools.getTwoDimArryAsSInt(dwfname,dtype,frac_bits)

    /*chisel3.Driver.execute(args, () => new ConvNeuronNetwork(
      dtype,dense_bias,dense_weights,
      conv_bias,conv_weights,frac_bits = frac_bits))*/

    Driver(() => new ConvNeuronNetwork(
      dtype,dense_bias,dense_weights,
      conv_bias,conv_weights,frac_bits = frac_bits)){
      c => new CNNTester(c,ifname,rfname,dtype,frac_bits)
    }
  }

  def testCNN16W4F():Unit={
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
    val result = "test/SInt16Frac4/test_output"

    runCNNTester(conv_bias,conv_weights,dense_bias,dense_weights,
      input,result,SInt(16.W),4)
  }

  def testCNN16W8F():Unit={
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
    val result = "test/SInt16Frac8/test_output"

    runCNNTester(conv_bias,conv_weights,dense_bias,dense_weights,
      input,result,SInt(16.W),8)
  }

  testCNN16W8F()
}
