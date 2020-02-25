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

def generate_test_output(wfname,bfname,ifname,rfname,frac_bits,path):
    weights = roundMatrix(getMatrixFromCsv(path + wfname),frac_bits,2)
    bias = roundMatrix(getMatrixFromCsv(path + bfname),frac_bits,1)
    inputs = roundMatrix(getMatrixFromCsv(path + ifname),frac_bits,1)
    output = np.dot(inputs,weights) + bias
    print(output)