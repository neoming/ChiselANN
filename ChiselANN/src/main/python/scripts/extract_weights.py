# this file is to extract model parameter and write it into csv file

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

# load model from h5 file
model = tf.keras.models.load_model('./tensorflow/resources/chisel_ann.h5')

# get layers
flatten = model.get_layer(index=0)
dense = model.get_layer(index=1)
dense1 = model.get_layer(index=2)

# write layers bias and weights to csv file(flatten doesn't need)
write_to_file(dense.weights[1].numpy(),'./tensorflow/resources/dense_weights_bias.csv',1)
write_to_file(dense.weights[0].numpy(),'./tensorflow/resources/dense_weights.csv',2)
write_to_file(dense1.weights[1].numpy(),'./tensorflow/resources/dense1_weights_bias.csv',1)
write_to_file(dense1.weights[0].numpy(),'./tensorflow/resources/dense1_weights.csv',2)