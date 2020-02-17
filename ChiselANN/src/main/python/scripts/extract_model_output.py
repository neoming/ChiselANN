# this file is to extract each layers' output and write it into csv fie

import tensorflow as tf
import csv

# transfer float32 to str
def float_to_str( x ):
  if isinstance( x[0], int ):
    return x
  return [ "{:.6f}".format( y ) for y in x ]

# write to csv file
def write_to_file( inputs, fname, no_dims = 2):
  f_out = open( fname, "w" ,newline='')
  wrt = csv.writer( f_out )
  
  if no_dims == 2:
    for a in inputs:
        tmp = wrt.writerow( float_to_str( a ) )
  else:
    tmp = wrt.writerow( float_to_str( inputs ) )
  f_out.close()

# get data from data set
mnist = tf.keras.datasets.mnist
(x_train, y_train),(x_test, y_test) = mnist.load_data()
x_train, x_test = x_train / 255.0, x_test / 255.0

# load model
model = tf.keras.models.load_model('./tensorflow/resources/chisel_ann.h5')
print(model.summary())

# get layers
flatten = model.get_layer(index = 0)
dense = model.get_layer(index = 1)
dense1 = model.get_layer(index = 2)

# get 5 images from test and print label
input_img = tf.convert_to_tensor(x_test[:5])
for i in range(0,5):
    print(y_test[i])

# get flatten layer output
flatten_output = flatten(input_img)
write_to_file(flatten_output[0].numpy(),'./tensorflow/resources/flatten_output_7.csv',1)
write_to_file(flatten_output[1].numpy(),'./tensorflow/resources/flatten_output_2.csv',1)
write_to_file(flatten_output[2].numpy(),'./tensorflow/resources/flatten_output_1.csv',1)
write_to_file(flatten_output[3].numpy(),'./tensorflow/resources/flatten_output_0.csv',1)
write_to_file(flatten_output[4].numpy(),'./tensorflow/resources/flatten_output_4.csv',1)

# get dense layer output
dense_output = dense(flatten_output)
write_to_file(dense_output[0].numpy(),'./tensorflow/resources/dense_output_7.csv',1)
write_to_file(dense_output[1].numpy(),'./tensorflow/resources/dense_output_2.csv',1)
write_to_file(dense_output[2].numpy(),'./tensorflow/resources/dense_output_1.csv',1)
write_to_file(dense_output[3].numpy(),'./tensorflow/resources/dense_output_0.csv',1)
write_to_file(dense_output[4].numpy(),'./tensorflow/resources/dense_output_4.csv',1)

# get dense1 layer output
dense1_output = dense1(dense_output)
write_to_file(dense1_output[0].numpy(),'./tensorflow/resources/dense1_output_7.csv',1)
write_to_file(dense1_output[1].numpy(),'./tensorflow/resources/dense1_output_2.csv',1)
write_to_file(dense1_output[2].numpy(),'./tensorflow/resources/dense1_output_1.csv',1)
write_to_file(dense1_output[3].numpy(),'./tensorflow/resources/dense1_output_0.csv',1)
write_to_file(dense1_output[4].numpy(),'./tensorflow/resources/dense1_output_4.csv',1)