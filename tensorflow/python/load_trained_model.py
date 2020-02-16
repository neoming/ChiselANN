import tensorflow as tf
import matplotlib.pyplot as plt

# get data from data set
mnist = tf.keras.datasets.mnist
(x_train, y_train),(x_test, y_test) = mnist.load_data()
x_train, x_test = x_train / 255.0, x_test / 255.0

# load model
#with open('./tensorflow/model/chisel_ann.json') as json_file:
#    json_config = json_file.read()
#new_model = tf.keras.models.model_from_json(json_config)
#new_model.load_weights('./tensorflow/model/chisel_ann.h5')

#new_model.compile(optimizer='adam',
#              loss='sparse_categorical_crossentropy',
#              metrics=['accuracy'])
#new_model.evaluate(x_test, y_test)

model = tf.keras.models.load_model('./tensorflow/model/chisel_ann.h5')
#model.evaluate(x_test, y_test)
model.get_layer(index=0)