
package deeplearning

import chisel3._
import chisel3.util.Decoupled

class DenseLayer (
  dtype : SInt,//input data type
  inNo : Int,//input Number
  outNo : Int,//output Number
  bias : Seq[SInt],//length equal to output Number
  weights : Seq[Seq[SInt]],//[outNo][inNo]
) extends Module{

  val io = IO(new Bundle() {
    val dataIn =  Input(Vec( inNo, dtype ))
    val dataOut =  Output(Vec( outNo, dtype ))
  })

  val ReLU: SInt => SInt = x => Mux(x <= 0.S, 0.S, x)

  for(i <- 0 until outNo){
    val neuron = Module(new Neuron(dtype , inNo , ReLU,bias(i)))
    neuron.io.in <> io.dataIn
    neuron.io.weights <> weights(i)
    neuron.io.bias := bias(i)
    io.dataOut(i) := neuron.io.out
  }
}
