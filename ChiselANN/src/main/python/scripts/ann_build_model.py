# this file is to build a simple DNN model

import tensorflow as tf
import matplotlib.pyplot as plt
import extract_tools as tool

# get data from data set
mnist = tf.keras.datasets.mnist
(x_train, y_train),(x_test, y_test) = mnist.load_data()
x_train, x_test = x_train / 255.0, x_test / 255.0

# define the model
model = tf.keras.models.Sequential([
  tf.keras.layers.Flatten(input_shape=(28, 28)),
  tf.keras.layers.Dense(20, activation='relu'),
  tf.keras.layers.Dense(10, activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

# print the model
model.summary()

# train the model
model.fit(x_train, y_train, epochs=5)

# evaluate the model
model.evaluate(x_test, y_test)

# save model in h5 format
model.save(tool.ann_path + "chisel_ann_20_10.h5")

# load model in h5 format 
#model = tf.keras.models.load_model('./tensorflow/model/chisel_ann_20_10.h5')

# evaluate to check the loaded model
#model.evaluate(x_test, y_test)
#model.get_layer(index=0)


# save model to the json format
#json_config = model.to_json()
#with open('chisel_ann.json', 'w') as json_file:
#    json_file.write(json_config)

# save weights to the h5 format
#model.save_weights('chisel_ann.h5')

# load model
#with open('./tensorflow/model/chisel_ann.json') as json_file:
#    json_config = json_file.read()
#new_model = tf.keras.models.model_from_json(json_config)
#new_model.load_weights('./tensorflow/model/chisel_ann.h5')

#new_model.compile(optimizer='adam',
#              loss='sparse_categorical_crossentropy',
#              metrics=['accuracy'])
#new_model.evaluate(x_test, y_test)