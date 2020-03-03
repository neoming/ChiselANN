import extract_tools as tool
path = tool.cnn_path

def testConv():
    #tool.getInput(path)
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

def testMaxPooling():
    maxp0_input = path + "conv0_output_7.csv"
    maxp0_output = path + "maxPooling0_test_output_7.csv"
    maxp0_result = tool.generate_maxpooling_test_output(maxp0_input,maxp0_output,4)
    
    maxp1_input = path + "conv1_output_7.csv"
    maxp1_output = path + "maxPooling1_test_output_7.csv"
    maxp1_result = tool.generate_maxpooling_test_output(maxp1_input,maxp1_output,4)
    
    maxp2_input = path + "conv2_output_7.csv"
    maxp2_output = path + "maxPooling2_test_output_7.csv"
    maxp2_result = tool.generate_maxpooling_test_output(maxp2_input,maxp2_output,4)
