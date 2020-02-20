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

testcase = list()
testcase.append("dense1_weights.csv")
testcase.append("dense1_weights_bias.csv")
testcase.append("dense_output_0.csv")
testcase.append("test_dense1_output_0.csv")
generate_test_output(testcase[0],testcase[1],testcase[2],testcase[3],4)

