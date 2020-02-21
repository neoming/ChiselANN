import tensorflow as tf
import numpy as np

path = "./ChiselANN/src/main/resources/"

def round_to( x, frac_bits ):
    factor = 1 << frac_bits
    return np.round( x * factor )

def getMatrixFromCsv(fname):
    return np.loadtxt(path + fname,delimiter=',',dtype=float)

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

def generate_test_output(wfname,bfname,ifname,rfname,frac_bits):
    weights = roundMatrix(getMatrixFromCsv(wfname),frac_bits,2)
    bias = roundMatrix(getMatrixFromCsv(bfname),frac_bits,1)
    inputs = roundMatrix(getMatrixFromCsv(ifname),frac_bits,1)
    output = np.dot(inputs,weights) + bias
    print(output)

dense1test = list()
dense1test.append("dense1_weights.csv")
dense1test.append("dense1_weights_bias.csv")
dense1test.append("dense_output_0.csv")
dense1test.append("test_dense1_output_0.csv")
generate_test_output(dense1test[0],dense1test[1],dense1test[2],dense1test[3],4)

densetest = list()
densetest.append("dense_weights.csv")
densetest.append("dense_weights_bias.csv")
densetest.append("flatten_output_0.csv")
densetest.append("test_dense_output_0.csv")
generate_test_output(densetest[0],densetest[1],densetest[2],densetest[3],4)
dense_output = roundMatrix(getMatrixFromCsv("dense_output_0.csv"),4,1)
print(dense_output)