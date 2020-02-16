import tensorflow as tf
import matplotlib.pyplot as plt

# get data from data set
mnist = tf.keras.datasets.mnist

(x_train, y_train),(x_test, y_test) = mnist.load_data()

x_train, x_test = x_train / 255.0, x_test / 255.0

# define the model
model = tf.keras.models.Sequential([
  tf.keras.layers.Flatten(input_shape=(28, 28)),
  tf.keras.layers.Dense(30, activation='relu'),
  tf.keras.layers.Dense(10, activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])
# print the model
model.summary()
#dense_layer_output = model.get_layer(index = 0)
#print(dense_layer_output)

# train the model
model.fit(x_train, y_train, epochs=5)
# evaluate the model
model.evaluate(x_test, y_test)

model.save('./tensorflow/model/chisel_ann.h5')
# save model to the json format
#json_config = model.to_json()
#with open('chisel_ann.json', 'w') as json_file:
#    json_file.write(json_config)

# save weights to the h5 format
#model.save_weights('chisel_ann.h5')

print(model.get_layer(index=2).bias) 
print()