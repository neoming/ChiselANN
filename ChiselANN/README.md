# ChiselANN

毕业设计，使用chisel实现人工神经网络

## `tensorflow`: python script is used to define ANN and train the weight

```python
model = tf.keras.models.Sequential([
  tf.keras.layers.Flatten(input_shape=(28, 28)),
  tf.keras.layers.Dense(30, activation='relu'),
  tf.keras.layers.Dense(10, activation='softmax')
])
```

## `ChiselANN`: clone from [Chisel Project Template](https://github.com/freechipsproject/chisel-template)