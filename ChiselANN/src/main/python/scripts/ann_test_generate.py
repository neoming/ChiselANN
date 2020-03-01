import tensorflow as tf
import extract_tools as tool

path = tool.ann_path

dense1test = list()
dense1test.append(path+"dense1_weights.csv")
dense1test.append(path+"dense1_weights_bias.csv")
dense1test.append(path+"dense_output_0.csv")
dense1test.append(path+"test_dense1_output_0.csv")
tool.generate_test_output(dense1test[0],dense1test[1],dense1test[2],dense1test[3],4)

densetest = list()
densetest.append(path+"dense_weights.csv")
densetest.append(path+"dense_weights_bias.csv")
densetest.append(path+"flatten_output_0.csv")
densetest.append(path+"test_dense_output_0.csv")
tool.generate_dense_test_output(densetest[0],densetest[1],densetest[2],densetest[3],4)
dense_output = tool.roundMatrix(tool.getMatrixFromCsv(path+"dense_output_0.csv"),4,1)
print(dense_output)