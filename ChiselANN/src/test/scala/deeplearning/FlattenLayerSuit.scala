
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object FlattenLayerSuit extends App{

    class FlattenLayerTester(
        c:FlattenLayer,
        ifname:String,
        rfname:String,
        dtype:SInt
    )extends PeekPokeTester(c){
        val inputs: Seq[SInt] =TestTools.getOneDimArryAsSInt(ifname,dtype,0)
        for(i <- inputs.indices){
            poke(c.io.dataIn.bits(i),inputs(i))
        }
        poke(c.io.dataIn.valid,true.B)
        print(" the valid signal is :" + peek(c.io.dataOut.valid) + '\n')
        for(i <- 0 until c.latency){
            step(1)
            //print("the " + i + " cycle: " + peek(c.io.dataOut.bits) + " valid is " + peek(c.io.dataOut.valid) + "\n")
        }
        print("after " + c.latency + " cycles, the valid signal is :" + peek(c.io.dataOut.valid) + '\n')
        TestTools.writeRowToCsv(peek(c.io.dataOut.bits).toList, rfname)//write result
    }

    def runFlattenLayerTester(
        ifname:String,
        rfname:String,
        dtype:SInt
    ) : Boolean = {

        Driver(() => new FlattenLayer(dtype)){
            f => new FlattenLayerTester(f,ifname,rfname,dtype)
        }
    }

    def testFlatten(){
        val input = "test_cnn/v_maxpooling_test_output_7.csv"
        val output = "test_cnn/v_flatten_test_output_7.csv"
        runFlattenLayerTester(input,output,SInt(16.W))
    }

  testFlatten()
}