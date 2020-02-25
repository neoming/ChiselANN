# this file is to extract model parameter and write it into csv file

import tensorflow as tf

import extract_tools as tool

path = tool.ann_path
# load model from h5 file
model = tf.keras.models.load_model('./tensorflow/resources/chisel_ann.h5')

# get layers
flatten = model.get_layer(index=0)
dense = model.get_layer(index=1)
dense1 = model.get_layer(index=2)

# write layers bias and weights to csv file(flatten doesn't need)
tool.write_to_file(dense.weights[1].numpy(),path + 'dense_weights_bias.csv',1)
tool.write_to_file(dense.weights[0].numpy(),path + 'dense_weights.csv',2)
tool.write_to_file(dense1.weights[1].numpy(),path + 'dense1_weights_bias.csv',1)
tool.write_to_file(dense1.weights[0].numpy(),path + 'dense1_weights.csv',2)