
package deeplearning

import chisel3._

class Neuron(dtype : SInt, inputs: Int, act : SInt => SInt, bias : SInt) extends Module {
  val io = IO(new Bundle {
    val in      = Input(Vec(inputs,dtype ))
    val weights = Input(Vec(inputs,dtype))
    val bias    = Input(dtype)
    val out     = Output(dtype)
  })

  val mac = io.in.zip(io.weights).map{ case(a:SInt, b:SInt) => a * b}.reduce(_ + _) + bias
  io.out := act(mac)
}