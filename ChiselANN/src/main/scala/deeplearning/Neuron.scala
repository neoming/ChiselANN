
package deeplearning

import chisel3._
import chisel3.util.log2Ceil

class Neuron(dtype : SInt, inputs: Int, act : SInt => SInt, bias : SInt,debug:Boolean) extends Module {
  val io = IO(new Bundle {
    val in      = Input(Vec(inputs,dtype ))
    val weights = Input(Vec(inputs,dtype))
    val bias    = Input(dtype)
    val out     = Output(dtype)
    val mul_res = if(debug) Some(Output(Vec(inputs,SInt((dtype.getWidth *2).W)))) else None
  })

  val latency: Int = log2Ceil( inputs ) + 2

  val mulResult: Vec[SInt] = Wire(Vec(inputs,SInt((dtype.getWidth *2).W)))
  for(i <- 0 until inputs){
    mulResult(i) := RegNext(io.in(i) * io.weights(i))
  }

  var sum = mulResult.toList
  while( sum.size > 1 ) {
    sum = sum.grouped( 2 ).map( grp => {
      val toSum = grp.reduce( _ + _ )
      RegNext( toSum )
    }).toList
  }

  io.out := RegNext(sum.head + bias)
  if(debug){io.mul_res.get := mulResult}
}