
package deeplearning

import java.io.File

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import com.github.tototoshi.csv.CSVWriter

class NeuronDebugTester(c : Neuron) extends PeekPokeTester(c){
  val fracBits = 4
  //get weights
  val buffer_weights = scala.io.Source.fromFile("src/main/resources/dense1_weights.csv")
  val weights_raw = buffer_weights.getLines().toList
  val weights = weights_raw.map(_.split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits ) ).toInt)
  }).map( x => x.asSInt(16.W))).transpose

  //get img
  val buffer_img = scala.io.Source.fromFile("src/main/resources/dense_output_7.csv")
  val img_raw = buffer_img.getLines().toList
  val img = img_raw(0).split(",").toList.map( x => {
    BigInt(math.round( x.toFloat * (1 << fracBits)).toInt)
  }).map( x => x.asSInt(16.W))

  for(i <- 0 until 30){
    print(i + " weight is" + weights(0)(i)+" input is " + img(i)+"\n")
    poke(c.io.weights(i),weights(0)(i))
    poke(c.io.in(i),img(i))
  }
  step(10)
  print(peek(c.io.mul_res.get))
  print(peek(c.io.out) + "\n")
}

object NeuronRunner extends App{
  val fracBits = 4

  //get weights
  val buffer_weights = scala.io.Source.fromFile("src/main/resources/dense1_weights.csv")
  val weights_raw = buffer_weights.getLines().toList
  val weights = weights_raw.map(_.split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits ) ).toInt)
  }).map( x => x.asSInt(16.W))).transpose

  //get bias
  val buffer_bias = scala.io.Source.fromFile("src/main/resources/dense1_weights_bias.csv")
  val bias_raw = buffer_bias.getLines().toList
  val bias = bias_raw(0).split(",").toList.map(x => {
    BigInt(math.round( x.toFloat * ( 1 << fracBits)).toInt)
  }).map( x => x.asSInt(16.W))


  //chisel3.Driver.emitVerilog(new Neuron(SInt(16.W),30,ReLU,bias(0)))
  val ReLU: SInt => SInt = x => Mux(x >= 0.S, x, 0.S)
  Driver(() => new Neuron(SInt(16.W),30,ReLU,bias(0),true)){
    n => new NeuronDebugTester(n)
  }

}