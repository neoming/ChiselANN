import tensorflow as tf
import numpy as np
import extract_tools as tool

path = tool.cnn_path
# load model
model = tf.keras.models.load_model(path + 'chisel_cnn.h5')

#model.summary()
conv = model.get_layer(index = 0)

# extract conv bias
conv_bias = conv.weights[1]
tool.write_to_file(conv_bias.numpy(),path + "conv_bias.csv",1)

# extract conv weights
conv_weights = conv.weights[0]
conv0_weights = np.empty((5,5), dtype = float)
conv1_weights = np.empty((5,5), dtype = float)
conv2_weights = np.empty((5,5), dtype = float)

for i in range(5):
    for j in range(5):
        conv0_weights[i][j] = conv_weights[i][j][0][0]
        conv1_weights[i][j] = conv_weights[i][j][0][1]
        conv2_weights[i][j] = conv_weights[i][j][0][2]

tool.write_to_file(conv0_weights,path + "conv0_weights.csv",2)
tool.write_to_file(conv1_weights,path + "conv1_weights.csv",2)
tool.write_to_file(conv2_weights,path + "conv2_weights.csv",2)