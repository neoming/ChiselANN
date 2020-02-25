
package deeplearning

import chisel3._
import chisel3.util._

class Conv2DLayer(
    dtype : SInt,
    dataWidth : Int = 28,
    dataHeight : Int = 28,
    filterWidth : Int = 5,
    filterHeight : Int = 5,
    filterBatch : Int = 3,
    strideWidth : Int = 1,
    strideHeight : Int = 1,
    weights : Seq[Seq[Seq[SInt]]],//[filter_index][i][j]
    bias : Seq[SInt],//[filter_index]
    frac_bits : Int = 0,
) extends Module{

    val inputNo: Int = dataWidth * dataWidth;
    val outputWidth: Int = (dataWidth - filterWidth + 1 )/strideWidth
    val outputHeight: Int = (dataHeight - filterHeight + 1 )/strideHeight
    val outputNo: Int = outputWidth * outputHeight * filterBatch
    val neuronInputNo: Int = filterHeight * filterWidth

    val io = IO(new Bundle{
        val dataIn = Flipped(Decoupled(Vec(inputNo, dtype)))
        val dataOut = Decoupled(Vec(outputNo,dtype))
    })

    val filters:Seq[Seq[Seq[Neuron]]] = ( 0 until filterBatch ).map( f =>{
        val neurons = {
            ( 0 until outputWidth).map(i => {
                ( 0 until outputHeight).map(j => {
                    val neuron = Module(new Neuron(dtype , neuronInputNo ,false,frac_bits))
                    neuron.io.bias := bias(f)
                    val inputBaseW: Int = i * strideWidth
                    val inputBaseH: Int  = j * strideHeight
                    for(w <- 0 until filterWidth){
                        for(h <- 0 until filterHeight){
                            val inputW: Int = inputBaseW + w
                            val inputH: Int = inputBaseH + h
                            val inputIndex: Int = inputW + inputH * dataWidth
                            val neuronInputIndex: Int = w + filterWidth * h
                            val outputIndex: Int = f * filterWidth * filterWidth + w + filterWidth * h

                            neuron.io.in(neuronInputIndex) := io.dataIn.bits(inputIndex)
                            neuron.io.weights(neuronInputIndex) := weights(f)(w)(h)
                            io.dataOut.bits(outputIndex) := neuron.io.act
                        }
                    }
                    neuron
                }).toList
            }).toList
        }
        neurons
    }).toList

    val latency: Int = filters.head.head.head.total_latency
    io.dataIn.ready := true.B
    io.dataOut.valid := ShiftRegister(io.dataIn.valid, latency, false.B, true.B)
}
