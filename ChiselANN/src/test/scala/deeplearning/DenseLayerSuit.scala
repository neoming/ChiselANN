
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import com.github.tototoshi.csv._

import java.io.File

class DenseLayerTester(c : DenseLayer) extends PeekPokeTester(c){
  val fracBits = 12

  //get img
  val buffer_img = scala.io.Source.fromFile("src/main/resources/dense_output_7.csv")
  val img_raw = buffer_img.getLines().toList
  val img = img_raw(0).split(",").toList.map( x => {
    BigInt(math.round( x.toFloat * (1 << fracBits)).toInt)
  }).map( x => x.asSInt(20.W))

  for( i <- 0 until 29 ){
    poke(c.io.dataIn(i),img(i))
  }
  /*val f = new File("test_dense_output_7.csv")
  val writer = CSVWriter.open(f)
  writer.writeRow(peek(c.io.dataOut).toList)*/
}

object DenseRunner extends App{
  val fracBits = 12

  //get weights
  val buffer_weights = scala.io.Source.fromFile("src/main/resources/dense_weights.csv")
  val weights_raw = buffer_weights.getLines().toList
  val weights = weights_raw.map(_.split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits ) ).toInt)
  }).map( x => x.asSInt(20.W))).transpose

  //get bias
  val buffer_bias = scala.io.Source.fromFile("src/main/resources/dense_weights_bias.csv")
  val bias_raw = buffer_bias.getLines().toList
  val bias = bias_raw(0).split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits)).toInt)
  }).map( x => x.asSInt(20.W))

  //get img
  //get img
  val buffer_img = scala.io.Source.fromFile("src/main/resources/flatten_output_0.csv")
  val img_raw = buffer_img.getLines().toList
  val img = img_raw(0).split(",").toList.map( x => {
    BigInt(math.round( x.toFloat * (1 << fracBits)).toInt)
  }).map( x => x.asSInt(20.W))

  println(img)
  println(weights)
  println(bias)

  print(chisel3.Driver.emitVerilog(new DenseLayer(SInt(20.W),784,30,bias,weights)))
  Driver(() =>new DenseLayer(SInt(20.W),784,30,bias,weights)){
    d => new DenseLayerTester(d)
  }
}

object Dense1Runner extends App{
  val fracBits = 12

  //get weights
  val buffer_weights = scala.io.Source.fromFile("src/main/resources/dense1_weights.csv")
  val weights_raw = buffer_weights.getLines().toList
  val weights = weights_raw.map(_.split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits ) ).toInt)
  }).map( x => x.asSInt(20.W))).transpose

  //get bias
  val buffer_bias = scala.io.Source.fromFile("src/main/resources/dense1_weights_bias.csv")
  val bias_raw = buffer_bias.getLines().toList
  val bias = bias_raw(0).split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits)).toInt)
  }).map( x => x.asSInt(20.W))

  //get img
  //get img
  val buffer_img = scala.io.Source.fromFile("src/main/resources/dense_output_7.csv")
  val img_raw = buffer_img.getLines().toList
  val img = img_raw(0).split(",").toList.map( x => {
    BigInt(math.round( x.toFloat * (1 << fracBits)).toInt)
  }).map( x => x.asSInt(20.W))

  println(img)
  println(weights)
  println(bias)

  Driver(() =>new DenseLayer(SInt(20.W),30,10,bias,weights)){
      d => new DenseLayerTester(d)
  }
}