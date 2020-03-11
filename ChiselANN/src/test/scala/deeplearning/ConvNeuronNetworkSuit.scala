
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import chisel3.util.log2Ceil

object ConvNeuronNetworkSuit extends App {
  class CNNTester(
    c :ConvNeuronNetwork,
    ifname:Seq[String],
    rfname:String,
    dtype:SInt,
    frac_bits:Int
  ) extends PeekPokeTester(c){
    var total_right = 0
    for(b <- 0 until 100){
      val img = ifname.head + "_" + b + ".csv"
      val label = ifname(1) + "_" + b + ".csv"
      val input_images: Seq[Seq[SInt]] = TestTools.getTwoDimArryAsSIntWithOutTrans(img ,dtype,frac_bits)
      val input_labels: Seq[UInt] = TestTools.getOneDimArryAsUInt(label ,UInt(log2Ceil(10).W),0)
      var output = Seq[UInt]()
      var right_no : Int = 0
      for(i <- input_images.indices){
        print("the " + i + " test start:\n")
        for( j <- input_images(i).indices){
          poke(c.io.dataIn.bits(j),input_images(i)(j))
        }
        poke(c.io.dataIn.valid,true.B)
        step(1)
        poke(c.io.dataIn.valid,false.B)
        for(k <- 0 until c.latency - 1){
          step(1)
          /*print("the " + k + " cycle: " + peek(c.io.dataOut.bits)
            + " valid is " + peek(c.io.dataOut.valid) + "\n")*/
          /*print("conv valid: " + peek(c.conv.io.dataOut.valid) + " maxPool valid: " + peek(c.maxPool.io.dataOut.valid)
            + " flatten valid: " + peek(c.flatten.io.dataOut.valid ) + " dense valid: " + peek(c.dense.io.dataOut.valid)
            + "output valid: " + peek(c.output_layer.io.dataOut.valid) + "\n")*/
        }
        val result = expect(c.io.dataOut.bits,input_labels(i))
        if(result){
          right_no = right_no + 1
          total_right = total_right + 1
        }
        print("the " + i + " test result is:" + peek(c.io.dataOut.bits) + "expect" + input_labels(i) + "\n")
        output = output :+ c.io.dataOut.bits
      }
      TestTools.writeRowToCsv(output,rfname + "_" + b + ".csv")
      print("the right number is " + right_no + "/" + input_labels.size + "\n")
    }
    print("the right number is " + total_right + "/10000\n")
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
      val temp_weights = TestTools.getTwoDimArryAsSInt(cwfname(i),dtype)
      temp_weights
    }).toList
    val conv_bias = TestTools.getOneDimArryAsSInt(cbfname,dtype)

    val dense_bias = TestTools.getOneDimArryAsSInt(dbfname,dtype)
    val dense_weights = TestTools.getTwoDimArryAsSInt(dwfname,dtype)

    /*chisel3.Driver.execute(args, () => new ConvNeuronNetwork(
      dtype,dense_bias,dense_weights,
      conv_bias,conv_weights,frac_bits = frac_bits))*/

    Driver(() => new ConvNeuronNetwork(
      dtype,dense_bias,dense_weights,
      conv_bias,conv_weights,frac_bits = frac_bits)){
      c => new CNNTester(c,ifname,rfname,dtype,frac_bits)
    }
  }

  def testCNN():Unit={
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
    val result = "test/test_output"

    runCNNTester(conv_bias,conv_weights,dense_bias,dense_weights,
      input,result,SInt(16.W),4)
  }
  testCNN()
}
