
package deeplearning

import chisel3._
import chisel3.util.log2Ceil

class Neuron(
  dtype : SInt,
  inputs: Int,
  debug:Boolean,
  frac_bits:Int = 0,
) extends Module {

  val io = IO(new Bundle {
    val in      = Input(Vec(inputs,dtype ))
    val weights = Input(Vec(inputs,dtype))
    val bias    = Input(dtype)
    val act     = Output(dtype)
    val mul_res = if(debug) Some(Output(Vec(inputs,SInt((dtype.getWidth *2).W)))) else None
  })

  val mac_latency: Int = log2Ceil( inputs ) + 1
  val mulResult: Vec[SInt] = Wire(Vec(inputs,SInt((dtype.getWidth *2).W)))
  var mac: List[SInt] = mulResult.toList

  for(i <- 0 until inputs){
    mulResult(i) := RegNext(io.in(i) * io.weights(i))
  }

  while( mac.size > 1 ) {
    mac = mac.grouped(2).map( grp => {
      val toSum = grp.reduce( _ + _ )
      RegNext( toSum )
    }).toList
  }

  val act_latency: Int = 3

  val mac_shift: SInt = Wire(mac.head.cloneType)
  mac_shift := RegNext(mac.head >> frac_bits.U)
  val output = RegNext(mac_shift + io.bias)
  val relu: SInt = Wire( dtype )
  val zero: SInt = 0.S.asTypeOf( dtype )
  relu := zero

  when(output > zero) {
    relu := output
  }

  val total_latency: Int = mac_latency + act_latency
  io.act := RegNext(relu)

  if(debug){
    io.mul_res.get := mulResult
  }
}