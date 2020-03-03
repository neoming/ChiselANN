
package deeplearning

import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}

object MaxPoolingLayerSuit extends App {
    class MaxPoolingLayerTester(
        c: MaxPoolingLayer,
        ifname:String,
        rfname:String,
        dtype:SInt,
    )extends PeekPokeTester(c){
        val inputs:Seq[SInt] = TestTools.getOneDimArryAsSInt(ifname,dtype,0)
        for( i <- inputs.indices ){
            poke(c.io.dataIn.bits(i),inputs(i))
        }
        poke(c.io.dataIn.valid,true.B)
        print("the valid signal is :" + peek(c.io.dataOut.valid) + '\n')
        for(i <- 0 until c.latency){
            step(1)
            //print("the " + i + " cycle: " + peek(c.io.dataOut.bits) + " valid is " + peek(c.io.dataOut.valid) + "\n")
        }
        print("after " + c.latency + " cycles, the valid signal is :" + peek(c.io.dataOut.valid) + '\n')
        TestTools.writeRowToCsv(peek(c.io.dataOut.bits).toList, rfname)//write result
    }

    def runMaxPoolingTester(
        ifname:String,
        rfname:String,
        dtype:SInt,
    ) : Boolean = {
        Driver(() => new MaxPoolingLayer(dtype)){
            m => new MaxPoolingLayerTester(m,ifname,rfname,dtype)
        }
    }

    def testMaxPooling() :Unit = {
        val inputs = "test_cnn/v_conv_test_output_7.csv"
        val result = "test_cnn/v_maxpooling_test_output_7.csv"
        runMaxPoolingTester(inputs,result,SInt(16.W))
    }

    testMaxPooling()
}
