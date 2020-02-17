# 记录研究进度

从2月13号开始正式记录

## 2.12

+ 完成tensorflow神经网络的训练，以及训练模型的导出
+ 查看tensorflow relu函数的实现（其实就是max(x,0)）
+ 使用getLayer查看网络每一层的输出结果
  
## 2.13

+ 创建Github项目，python脚本和scala代码在同一个仓库里
+ Chisel代码完成了简单的4*2 DenseLayer 的测试
+ scala文件输入输出
+ 安装Edraw软件进行画图
+ 查看DenseLayer生成的.fir代码，并将neuron生成的结点数手动画图
+ 印象笔记进行思维导图绘制

## 2.14,2.15

情人节，去过节了，没有任何进展

## 2.16

+ `model.save('path_to_model.h5')`,`model.load('path_to_model.h5')`储存和加载训练好的模型
+ 用vscode写juypter notebook啦
+ 将权重导入到csv里面去，针对有空行的问题,有如下的解决方案
  
```python
f_out = open( fname, "w" ,newline='')#newline = ''
wrt = csv.writer( f_out )
wrt.writerow( "some thing" )
f_out.close()
```

+ 理解tensor shape,以及layer input shape中(None,28,28)的概念。None在训练时用来表示batch

## 2.17

+ 使用scala生成Denselayer，但是由于784\*30的全连接层参数过大导致仿真的时候stackoverflow。可以正常模拟30\*10的,栈不会溢出
+ 明日计划：自己搭建Python的神经网络进行计算，然后验证30*10的正确性
+ 安装modelsim，实在不行就通过.v进行验证吧=-=