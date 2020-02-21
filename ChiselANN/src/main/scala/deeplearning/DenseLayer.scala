
package deeplearning

import chisel3._
import chisel3.util.log2Ceil

class DenseLayer (
  dtype : SInt,//input data type
  inNo : Int,//input Number
  outNo : Int,//output Number
  bias : Seq[SInt],//length equal to output Number
  weights : Seq[Seq[SInt]],//[outNo][inNo]
) extends Module{

  val io = IO(new Bundle() {
    val dataIn =  Input(Vec( inNo, dtype))
    val dataOut =  Output(Vec( outNo, dtype))
  })

  val neurons: List[Neuron] = ( 0 until outNo).map(i =>{
    val neuron = Module(new Neuron(dtype , inNo ,bias(i),false))
    neuron.io.in <> io.dataIn
    neuron.io.weights <> weights(i)
    neuron.io.bias := bias(i)
    io.dataOut(i) := neuron.io.act
    neuron
  }).toList

  val latency: Int = neurons.head.mac_latency + neurons.head.act_latency
}
