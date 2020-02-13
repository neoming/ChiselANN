import tensorflow as tf
import matplotlib.pyplot as plt

mnist = tf.keras.datasets.mnist

(x_train, y_train),(x_test, y_test) = mnist.load_data()

x_train, x_test = x_train / 255.0, x_test / 255.0

model = tf.keras.models.Sequential([
  tf.keras.layers.Flatten(input_shape=(28, 28)),
  tf.keras.layers.Dense(30, activation='relu'),
  tf.keras.layers.Dense(10, activation='softmax')
])

dense_layer_output = model.get_layer(index = 0)
print(dense_layer_output)
model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

model.summary()
#model.save_weights('./checkpoints/my_checkpoint')
#model.fit(x_train, y_train, epochs=5)
#model.evaluate(x_test, y_test)

#print(model.get_layer(index=2).bias)