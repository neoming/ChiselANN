import tensorflow as tf
import numpy as np
import extract_tools as tool
import csv

path = tool.cnn_path
# load model
model = tf.keras.models.load_model(path + 'chisel_cnn.h5')
#model.summary()

# get data from data set
mnist = tf.keras.datasets.mnist
(x_train, y_train),(x_test, y_test) = mnist.load_data()
x_train, x_test = x_train / 255.0, x_test / 255.0

# get conv weights
conv = model.get_layer(index = 0)
bias = conv.weights[1]
conv_weights = conv.weights[0]
conv0_weights = np.empty((5,5), dtype = float)
conv1_weights = np.empty((5,5), dtype = float)
conv2_weights = np.empty((5,5), dtype = float)
for i in range(5):
    for j in range(5):
        conv0_weights[i][j] = conv_weights[i][j][0][0]
        conv1_weights[i][j] = conv_weights[i][j][0][1]
        conv2_weights[i][j] = conv_weights[i][j][0][2]

def getFilter(matrix,pi,pj,width,height):
    result = np.empty((width,height), dtype = float)
    for i in range(width):
        for j in range(height):
            result[i][j] = matrix[pi+i][pj+j] 
    return result

def neuron(ma,mb,w,h,bias):
    neuron_result = 0.0
    for i in range(w):
        for j in range(h):
            neuron_result += ma[i][j] * mb[i][j]
    neuron_result = neuron_result + bias
    # relu
    if neuron_result > 0: return neuron_result
    else: return 0.0

def conv(matrix,i):
    conv_result = np.empty((24,24),dtype = float)
    for i in range(24):
        for j in range(24):
            conv_matrix = getFilter(matrix,i,j,5,5)
            if i == 0 :
                conv_weights = conv0_weights
            elif i == 1:
                conv_weights = conv1_weights
            else:
                conv_weights = conv2_weights
            conv_result[i][j] = neuron(conv_matrix,conv0_weights,5,5,bias[0])
    return conv_result

tool.write_to_file(conv(x_test[0],0),path + "conv0_manual_output_7.csv",2)
tool.write_to_file(conv(x_test[0],1),path + "conv1_manual_output_7.csv",2)
tool.write_to_file(conv(x_test[0],2),path + "conv2_manual_output_7.csv",2)
