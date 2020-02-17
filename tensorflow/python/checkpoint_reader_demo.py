# a tensorflow ChickPointReader 

import os
import re
import tensorflow as tf
from tensorflow.python import pywrap_tensorflow
import numpy as np
np.set_printoptions(threshold=np.inf)  

model_dir = "./checkpoints"#checkpoints 目录

ckpt = tf.train.get_checkpoint_state(model_dir)#获取checkpoints 目录下的模型数据
ckpt_file = os.path.basename(ckpt.model_checkpoint_path)

read_path = os.path.join(model_dir,ckpt_file)#合成绝对路径

reader = pywrap_tensorflow.NewCheckpointReader(read_path)#对目标文件进行读写

print(reader.debug_string().decode("utf-8"))
print(reader.get_tensor("layer_with_weights-0/kernel/.ATTRIBUTES/VARIABLE_VALUE"))
data=open("data.txt",'w+')#数据写进这个文件
var_to_shape_map = reader.get_variable_to_shape_map()
for key in var_to_shape_map:
    print("tensor_name: ",key)
    print(reader.get_tensor(key))
    print('tensor_name: %s'%key,file=data)
    print(reader.get_tensor(key),file=data)
data.close()


