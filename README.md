# Multilayer perceptron
A GUI application that allows the user to build a multilayer perceptron based on the following configurations:

* Number of inputs
* Number of outputs
* Number of hidden layers
* Number of neurons/cells per hidden layer
* Initial activation weights
* Activation threshold for each layer
* Activation function (Unit function, linear function, identity function, sigmoid function or gaussian function)
* Maximum number of iterations if the computation of an activation weight did not converge
* Error function
* Error threshold (used to decide if the weights should be updated or not)

### Training phase
The user can train the perceptron by providing training sets. There is no restriction on the number of training sets that the user can provide. A training set is made of a number of inputs and a number of outputs that will be used to compute the activation weights based on the perceptron built in the configuration phase.
The number of inputs and outputs in a training set depepnds on the numbers specified by the user previously.

Once the training sets are provided and the training phase is started, the back propagation algorithm will run and update the activation weights accordingly. When the training phase is over, the GUI will show the user the detailed steps followed by the algorithm such as the updated weights for each layer, the number of iterations it took for the algorithm to converge and so on as well as a vizualisation of the obtained perceptron.

### Testing phase
After the perceptron was trained, the user can provide it with a set of inputs. The perceptron will output a result based on the new activation weights that were computed using the training sets provided previously.

## Demo
![Alt](http://joeyhelou.com/perceptron/perceptron.png)

![Alt](http://joeyhelou.com/perceptron/perceptron2.png)

## More information
The detailed paper concerning this project can be found [here](http://joeyhelou.com/perceptron/perceptron.docx)
