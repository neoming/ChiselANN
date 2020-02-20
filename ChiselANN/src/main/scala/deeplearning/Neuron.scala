
package deeplearning

import chisel3._

class Neuron(dtype : SInt, inputs: Int, act : SInt => SInt, bias : SInt,debug:Boolean) extends Module {
  val io = IO(new Bundle {
    val in      = Input(Vec(inputs,dtype ))
    val weights = Input(Vec(inputs,dtype))
    val bias    = Input(dtype)
    val out     = Output(dtype)
    val mul_res = if(debug) Some(Output(Vec(inputs,SInt((dtype.getWidth *2).W)))) else None
  })

  val mulResult = Wire(Vec(inputs,SInt((dtype.getWidth *2).W)))
  for(i <- 0 until inputs){
    mulResult(i) := io.in(i) * io.weights(i)
  }

  val result = mulResult.reduceTree((x:SInt,y:SInt) => (x + y)) + bias
  io.out := result

  if(debug){io.mul_res.get := mulResult}
}