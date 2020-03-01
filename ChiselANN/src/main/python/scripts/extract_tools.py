import csv
import numpy as np

ann_path = "./ChiselANN/src/main/resources/test_ann/"
cnn_path = "./ChiselANN/src/main/resources/test_cnn/"
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

def write_matrix_to_file( inputs, fname):
  f_out = open( fname, "w" ,newline='')
  wrt = csv.writer( f_out )
  for a in inputs:
    tmp = wrt.writerow(["{:>8d}".format(int(y)) for y in a])
  f_out.close()

def round_to( x, frac_bits ):
  factor = 1 << frac_bits
  return np.round( x * factor )

def getMatrixFromCsv(fname):
    return np.loadtxt(fname,delimiter=',',dtype=float)

def roundMatrix(matrix,frac_bits,dim = 2):
    if(dim == 1):
        result = np.empty(len(matrix),dtype=int)
        for i in range(len(matrix)):
            result[i] = round_to(matrix[i],frac_bits)
        return result
    else:
        result = np.empty([len(matrix),len(matrix[0])],dtype = int)
        for i in range(len(result)):
            for j in range(len(result[0])):
                result[i][j] = round_to(matrix[i][j],frac_bits)
        return result

def generate_dense_test_output(wfname,bfname,ifname,rfname,frac_bits,path):
    weights = roundMatrix(getMatrixFromCsv(wfname),frac_bits,2)
    bias = roundMatrix(getMatrixFromCsv(bfname),frac_bits,1)
    inputs = roundMatrix(getMatrixFromCsv(ifname),frac_bits,1)
    output = np.dot(inputs,weights) + bias
    print(output)
    return output


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

def conv(matrix,conv_weights,conv_bias):
    conv_result = np.empty((24,24),dtype = float)
    for i in range(24):
        for j in range(24):
            conv_matrix = getFilter(matrix,i,j,5,5)
            conv_result[i][j] = neuron(conv_matrix,conv_weights,5,5,conv_bias)
    return conv_result  

def generate_conv_test_output(wfname,bfname,ifname,rfname,frac_bits,filter):
    weights = roundMatrix(getMatrixFromCsv(wfname),frac_bits,2)
    bias = roundMatrix(getMatrixFromCsv(bfname),frac_bits,1)
    inputs = roundMatrix(getMatrixFromCsv(ifname),frac_bits,2)
    output = conv(inputs,weights,bias[filter])
    write_matrix_to_file(output,rfname)
    return output

def getInput(path):
  import tensorflow as tf
  # get data from data set
  mnist = tf.keras.datasets.mnist
  (x_train, y_train),(x_test, y_test) = mnist.load_data()
  x_train, x_test = x_train / 255.0, x_test / 255.0
  for i in range(5):
      write_to_file(x_test[i].flatten(),path + "input_1d_"+str(y_test[i])+".csv",1)
      write_to_file(x_test[i],path + "input_2d_"+str(y_test[i])+".csv",2)
