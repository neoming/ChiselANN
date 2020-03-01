import extract_tools as tool
path = tool.cnn_path

tool.getInput(path)
conv0_weights = path + "conv0_weights.csv"
conv1_weights = path + "conv1_weights.csv"
conv2_weights = path + "conv2_weights.csv"
bias = path + "conv_bias.csv"
conv0_output = path + "conv0_test_output_7.csv"
conv1_output = path + "conv1_test_output_7.csv"
conv2_output = path + "conv2_test_output_7.csv"
input_img = path + "input_2d_7.csv"
conv0_result = tool.generate_conv_test_output(conv0_weights,bias,input_img,conv0_output,4,0)
conv1_result = tool.generate_conv_test_output(conv1_weights,bias,input_img,conv1_output,4,1)
conv2_result = tool.generate_conv_test_output(conv2_weights,bias,input_img,conv2_output,4,2)
