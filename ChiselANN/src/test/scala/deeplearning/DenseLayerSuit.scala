
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

class DenseLayerTester(c : DenseLayer) extends PeekPokeTester(c){
  val fracBits = 2

  //get img
  val buffer_img = scala.io.Source.fromFile("src/resources/denselayer_img.csv")
  val img_raw = buffer_img.getLines().toList
  val img = img_raw.map(_.split(",").toList.map( x => {
    BigInt(math.round( x.toFloat * (1 << fracBits)).toInt)
  }))

  poke(c.io.dataIn(0),img(0)(0))
  poke(c.io.dataIn(1),img(0)(1))
  poke(c.io.dataIn(2),img(1)(0))
  poke(c.io.dataIn(3),img(1)(1))
  peek(c.io.dataOut)
}

object Runner extends App{
  val fracBits = 0

  //get weights
  val buffer_weights = scala.io.Source.fromFile("src/resources/denselayer_weights.csv")
  val weights_raw = buffer_weights.getLines().toList
  val weights = weights_raw.map(_.split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits ) ).toInt)
  }).map( x => x.asSInt(16.W)))

  //get bias
  val buffer_bias = scala.io.Source.fromFile("src/resources/denselayer_bias.csv")
  val bias_raw = buffer_bias.getLines().toList
  val bias = bias_raw(0).split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits)).toInt)
  }).map( x => x.asSInt(16.W))

  //get img
  val buffer_img = scala.io.Source.fromFile("src/resources/denselayer_img.csv")
  val img_raw = buffer_img.getLines().toList
  val img = img_raw.map(_.split(",").toList.map( x => {
    BigInt(math.round( x.toFloat * (1 << fracBits)).toInt)
  }))

  println(img)
  println(weights)
  println(bias)

  Driver(() =>new DenseLayer(SInt(16.W).cloneType,4,2,bias,weights)){
      d => new DenseLayerTester(d)
  }
}