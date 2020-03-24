
package deeplearning

import chisel3._
import chisel3.util.log2Ceil

class ImgMem(
            dtype : SInt,
            height : Int,
            width : Int,
            img : Option[Seq[Seq[SInt]]]
            ) extends Module{
  val io = IO(new Bundle {
    val write = Input(Bool())
    val addr = Input(UInt(log2Ceil(height).W))
    val dataIn = Input(Vec(width,dtype))
    val dataOut = Output(Vec(width,dtype))
  })

  val mem = Mem(height, Vec(width,dtype))
  io.dataOut := DontCare

  if(img.isDefined){
    val img_data = img.get
    for(i <- img_data.indices){
      val write_line = VecInit(img_data(i))
      mem.write(i.asUInt(log2Ceil(height).W),write_line)
    }
  }

  when (io.write) {
    mem.write(io.addr, io.dataIn)
  }

  io.dataOut := mem.read(io.addr)
}
