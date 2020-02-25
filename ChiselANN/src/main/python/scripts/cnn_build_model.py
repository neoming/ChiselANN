import tensorflow as tf

# get data from data set
mnist = tf.keras.datasets.mnist
(x_train, y_train),(x_test, y_test) = mnist.load_data()
x_train, x_test = x_train / 255.0, x_test / 255.0

x_train = x_train.reshape(x_train.shape[0],28,28,1)
x_test = x_test.reshape(x_test.shape[0],28,28,1)

# define the model
model = tf.keras.models.Sequential([
  tf.keras.layers.Conv2D(3,(5,5),activation='relu',input_shape=(28,28,1)),
  tf.keras.layers.MaxPooling2D((2,2)),
  tf.keras.layers.Flatten(),
  tf.keras.layers.Dense(10,activation='softmax')
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
model.save('./ChiselANN/src/main/resources/test_cnn/chisel_cnn.h5')