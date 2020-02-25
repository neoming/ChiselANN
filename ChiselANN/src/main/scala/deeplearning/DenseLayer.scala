
package deeplearning

import chisel3._
import chisel3.util._

class DenseLayer (
  dtype : SInt,//input data type
  inNo : Int,//input Number
  outNo : Int,//output Number
  bias : Seq[SInt],//length equal to output Number
  weights : Seq[Seq[SInt]],//[outNo][inNo]
  frac_bits : Int = 0,//frac_bits
) extends Module{

  val io = IO(new Bundle() {
    val dataIn = Flipped(Decoupled(Vec(inNo, dtype)))
    val dataOut =  Decoupled(Vec( outNo, dtype))
  })

  val neurons: List[Neuron] = ( 0 until outNo).map(i =>{
    val neuron = Module(new Neuron(dtype , inNo ,false,frac_bits))
    neuron.io.in <> io.dataIn.bits
    neuron.io.weights <> weights(i)
    neuron.io.bias := bias(i)
    io.dataOut.bits(i) := neuron.io.act
    neuron
  }).toList

  val latency: Int = neurons.head.total_latency

  io.dataIn.ready := true.B
  io.dataOut.valid := ShiftRegister(io.dataIn.valid, latency, false.B, true.B)
}
