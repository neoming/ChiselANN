
import tensorflow as tf
import numpy as np
import extract_tools as tool

path = tool.cnn_path
# get data from data set
mnist = tf.keras.datasets.mnist
(x_train, y_train),(x_test, y_test) = mnist.load_data()
x_train, x_test = x_train / 255.0, x_test / 255.0

x_train = x_train.reshape(x_train.shape[0],28,28,1)
x_test = x_test[:1000]
x_test = x_test.reshape(x_test.shape[0],28,28,1)
# load model
model = tf.keras.models.load_model(path + 'chisel_cnn.h5')
model.evaluate(x_test, y_test[0:1000])

#model.summary()

def extract_conv_output():
    #extract conv output
    # get 5 images from test and print label
    input_img = x_test[:5]#7 2 1 0 4
    conv = model.get_layer(index = 0)
    conv_output = conv(input_img)
    conv_output_7 = conv_output[0]
    conv0_output_7 = np.empty((24,24), dtype = float)
    conv1_output_7 = np.empty((24,24), dtype = float)
    conv2_output_7 = np.empty((24,24), dtype = float)

    for i in range(24):
        for j in range(24):
            conv0_output_7[i][j] = conv_output_7[i][j][0]
            conv1_output_7[i][j] = conv_output_7[i][j][1]
            conv2_output_7[i][j] = conv_output_7[i][j][2]

    tool.write_to_file(conv0_output_7,path+"conv0_output_7.csv",2)
    tool.write_to_file(conv1_output_7,path+"conv1_output_7.csv",2)
    tool.write_to_file(conv2_output_7,path+"conv2_output_7.csv",2)


def extract_maxPool_output():
    #extract maxPool output
    import numpy as np
    input_img = x_test[:5]#7 2 1 0 4
    conv = model.get_layer(index = 0)
    conv_output = conv(input_img)
    maxPool = model.get_layer(index = 1)
    maxPool_output = maxPool(conv_output)
    maxPool_output_7 = maxPool_output[0]
    maxPool0_output_7 = np.empty((12,12), dtype = float)
    maxPool1_output_7 = np.empty((12,12), dtype = float)
    maxPool2_output_7 = np.empty((12,12), dtype = float)

    for i in range(12):
        for j in range(12):
            maxPool0_output_7[i][j] = maxPool_output_7[i][j][0]
            maxPool1_output_7[i][j] = maxPool_output_7[i][j][1]
            maxPool2_output_7[i][j] = maxPool_output_7[i][j][2]

    tool.write_to_file(maxPool0_output_7,path+"maxPool0_output_7.csv",2)
    tool.write_to_file(maxPool1_output_7,path+"maxPool1_output_7.csv",2)
    tool.write_to_file(maxPool2_output_7,path+"maxPool2_output_7.csv",2)

def extract_flatten_output():
    conv = model.get_layer(index = 0)
    maxPool = model.get_layer(index = 1)
    flatten = model.get_layer(index = 2)
    input_img = x_test[:5]#7 2 1 0 4
    conv_output = conv(input_img)
    maxPool_output = maxPool(conv_output)
    flatten_output = flatten(maxPool_output)
    flatten_output_7 = flatten_output[0]
    tool.write_to_file(flatten_output_7,path+"flatten_output_7.csv",1)

#extract_flatten_output()