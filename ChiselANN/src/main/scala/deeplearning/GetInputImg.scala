
package deeplearning

import chisel3._
import chisel3.util._

class GetInputImg(
                 dtype : SInt,
                 dataWidth : Int,
                 dataHeight : Int,
                 filterHeight : Int,
                 )extends Module{
  val io = IO(new Bundle() {
    val write_data = Input(Vec(dataWidth,dtype))
    val write = Input(Bool())
    val write_addr = Input(UInt(log2Ceil(dataHeight).W))
    val dataReady = Input(Bool())
    val read_addr = Output(UInt(log2Ceil(dataHeight).W))
    val dataOut = Decoupled(Vec(dataWidth, dtype))
  })

  val addr_width = log2Ceil(dataHeight)
  val imgMem = Module(new ImgMem(dtype,dataHeight,dataWidth))
  val base_addr = RegInit(0.U(addr_width.W))
  val addr = RegInit(0.U(addr_width.W))
  val wrap = addr === (filterHeight-1).asUInt()

  withClock(clock){
    when(io.dataReady){
      addr := addr + 1.U(addr_width.W)
    }.otherwise{
      addr := 0.U(addr_width.W)
    }
  }
  when(wrap){
    addr := 0.U(addr_width.W)
    base_addr := base_addr + 1.U(addr_width.W)
  }

  when(io.write){
    imgMem.io.addr := io.write_addr
  }.otherwise{
    imgMem.io.addr := base_addr + addr
  }

  io.read_addr :=  base_addr + addr
  imgMem.io.write := io.write
  imgMem.io.dataIn := io.write_data

  io.dataOut.bits := imgMem.io.dataOut
  io.dataOut.valid := io.dataReady
}
