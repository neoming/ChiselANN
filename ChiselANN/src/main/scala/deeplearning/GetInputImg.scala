
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

    val inputBundle = new InputBundle(
      dtype = dtype,
      dataWidth = dataWidth,
      dataHeight = dataHeight,
    )

    //val read_addr = Output(Vec(3,UInt(log2Ceil(dataHeight).W)))
    val dataOut = Decoupled(Vec(dataWidth, dtype))
  })

  val imgMem = Module(new ImgMem(dtype,dataHeight,dataWidth))

  val addr_width = log2Ceil(dataHeight)
  val base_addr = RegInit(0.U(addr_width.W))
  val addr = RegInit(0.U(addr_width.W))
  val wrap: Bool = addr === (filterHeight-1).asUInt(addr_width.W)
  val read_addr_wrap : Bool = base_addr + addr === (dataHeight -1).asUInt(addr_width.W)

  withClock(clock){
    when(io.inputBundle.dataReady){
      addr := addr + 1.U(addr_width.W)
    }.otherwise{
      addr := 0.U(addr_width.W)
    }
  }
  when(wrap){
    addr := 0.U(addr_width.W)
    base_addr := base_addr + 1.U(addr_width.W)
    when(read_addr_wrap){
      base_addr := 0.U(addr_width.W)
    }
  }

  when(io.inputBundle.write){
    imgMem.io.addr := io.inputBundle.write_addr
  }.otherwise{
    imgMem.io.addr := base_addr + addr
  }

  //for testing addr
  /*io.read_addr(0) := addr + base_addr
  io.read_addr(1) := addr
  io.read_addr(2) := base_addr*/

  imgMem.io.write := io.inputBundle.write
  imgMem.io.dataIn := io.inputBundle.write_data

  io.dataOut.bits := imgMem.io.dataOut
  io.dataOut.valid := io.inputBundle.dataReady
}
