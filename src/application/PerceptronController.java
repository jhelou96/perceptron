package application;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import Jama.Matrix;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class PerceptronController {
	@FXML
	private TextField tfNbInputCells;
	@FXML
	private TextField tfNbOutputCells;
	@FXML
	private TextField tfNbLayers;
	@FXML
	private Button btSaveConfig;
	@FXML
	private Button btIncInputs;
	@FXML
	private Button btDecInputs;
	@FXML
	private Button btIncOutputs;
	@FXML
	private Button btDecOutputs;
	@FXML
	private Button btIncLayers;
	@FXML
	private Button btDecLayers;
	@FXML
	private VBox vbNeuronsPerLayer;
	@FXML
	private HBox hbActivationWeights;
	@FXML
	private VBox vbActivationThresholds;
	@FXML
	private TextField tfErrThreshold;
	@FXML
	private Slider slErrThreshold;
	@FXML
	private ToggleGroup tgActivationFunctions;
	@FXML
	private ToggleGroup tgErrorFunctions;
	@FXML
	private RadioButton rbLinearFunction;
	@FXML
	private RadioButton rbUnitFunction;
	@FXML
	private RadioButton rbIdentityFunction;
	@FXML
	private RadioButton rbSigmoidFunction;
	@FXML
	private RadioButton rbGaussianFunction;
	@FXML
	private RadioButton rbMeanAbsErrorFunction;
	@FXML
	private RadioButton rbMeanDifErrorFunction;
	@FXML
	private RadioButton rbMeanSquErrorFunction;
	@FXML
	private TextField tfMaxIterations;
	@FXML
	private Tab tbTraining;
	@FXML
	private Tab tbPrediction;
	@FXML
	private VBox vbTrainingResults;
	@FXML
	private Tab tbTrainingResults;
	@FXML
	private Tab tbTrainingVisualization;
	@FXML
	private Group gPerceptronDrawing;
	@FXML
	private HBox hbPredictionInputs;
	@FXML
	private VBox vbPredictionResults;
	@FXML
	private VBox vbTrainingSets;

	private TextField[][][] tfsWeights;
	private ArrayList<TextField> tfsThresholds;
	private ArrayList<TextField> tfsPredictionInputs;
	private ArrayList<ArrayList<ArrayList<TextField>>> trainingSets;

	/*
	 * Configuration inputs
	 */
	private int nbInputCells;
	private int nbOutputCells;
	private int nbLayers;
	private ArrayList<Integer> nbNeuronsPerLayer;
	private int[] thresholds;
	private String activationFunction;
	private int maxIterations;
	private String errorFunction;
	private double errThreshold;
	private Matrix[] weightMatrices;
	private Matrix givenInputMatrix;
	private Matrix givenOutputMatrix;
	private int nbTrainingSets;

	private ArrayList<Matrix> actualOutputs;
	
	private Matrix[] tempWeightMatrices;

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the FXML file has been loaded.
	 */
	@FXML
	public void initialize() {
		nbInputCells = 1;
		nbOutputCells = 1;
		nbLayers = 1;

		tfsWeights = new TextField[100][100][100]; // 3D Array containing all the weights textfields

		tfsThresholds = new ArrayList<TextField>();
		addLayerThresholdEntry();

		nbNeuronsPerLayer = new ArrayList<Integer>();
		addNeuronsPerLayerEntry();

		generateWeightsMatrices();

		// Bind error threshold textfield to slider
		slErrThreshold.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				tfErrThreshold.setText(String.format("%.2f", newValue));
			}
		});

		activationFunction = "linearFunction";
		maxIterations = 10;
		errorFunction = "meanAbsErrorFunction";
		errThreshold = 0.1;

		rbLinearFunction.setUserData("linearFunction");
		rbUnitFunction.setUserData("unitFunction");
		rbIdentityFunction.setUserData("identityFunction");
		rbSigmoidFunction.setUserData("sigmoidFunction");
		rbGaussianFunction.setUserData("gaussianFunction");
		rbMeanAbsErrorFunction.setUserData("meanAbsErrorFunction");
		rbMeanDifErrorFunction.setUserData("meanDifErrorFunction");
		rbMeanSquErrorFunction.setUserData("meanSquErrorFunction");
	}

	/**
	 * Increments the number of input cells Method is triggered when the Increment
	 * input button is pressed
	 * 
	 * @param event
	 */
	@FXML
	public void incInputsButtonHandler(ActionEvent event) {
		nbInputCells++;
		tfNbInputCells.setText(nbInputCells + "");

		generateWeightsMatrices();

		// Deactivate training and testing tabs until config is saved
		tbTraining.setDisable(true);
		tbPrediction.setDisable(true);
	}

	/**
	 * Decrements the number of input cells Method is triggered when the Decrement
	 * input button is pressed
	 * 
	 * @param event
	 */
	@FXML
	public void decInputsButtonHandler(ActionEvent event) {
		if (nbInputCells <= 1)
			return;

		nbInputCells--;
		tfNbInputCells.setText(nbInputCells + "");

		generateWeightsMatrices();

		// Deactivate training and testing tabs until config is saved
		tbTraining.setDisable(true);
		tbPrediction.setDisable(true);
	}

	/**
	 * Increments the number of output cells Method is triggered when the Increment
	 * output button is pressed
	 * 
	 * @param event
	 */
	@FXML
	public void incOutputsButtonHandler(ActionEvent event) {
		nbOutputCells++;
		tfNbOutputCells.setText(nbOutputCells + "");

		// Deactivate training and testing tabs until config is saved
		tbTraining.setDisable(true);
		tbPrediction.setDisable(true);
	}

	/**
	 * Decrements the number of output cells Method is triggered when the Decrement
	 * output button is pressed
	 * 
	 * @param event
	 */
	@FXML
	public void decOutputsButtonHandler(ActionEvent event) {
		if (nbOutputCells <= 1)
			return;

		nbOutputCells--;
		tfNbOutputCells.setText(nbOutputCells + "");

		// Deactivate training and testing tabs until config is saved
		tbTraining.setDisable(true);
		tbPrediction.setDisable(true);
	}

	/**
	 * Increments the number of layers Method is triggered when the Increment layer
	 * button is pressed
	 * 
	 * @param event
	 */
	@FXML
	public void incLayersButtonHandler(ActionEvent event) {
		nbLayers++;
		tfNbLayers.setText(nbLayers + "");

		addNeuronsPerLayerEntry();
		generateWeightsMatrices();
		addLayerThresholdEntry();

		// Deactivate training and testing tabs until config is saved
		tbTraining.setDisable(true);
		tbPrediction.setDisable(true);
	}

	/**
	 * Decrements the number of layers Method is triggered when the Decrement layer
	 * button is pressed
	 * 
	 * @param event
	 */
	@FXML
	public void decLayersButtonHandler(ActionEvent event) {
		if (nbLayers <= 1)
			return;

		nbLayers--;
		tfNbLayers.setText(nbLayers + "");

		// Remove related neurons
		nbNeuronsPerLayer.remove(nbLayers);
		vbNeuronsPerLayer.getChildren().remove(nbLayers);

		// Remove related threshold
		tfsThresholds.remove(nbLayers);
		vbActivationThresholds.getChildren().remove(nbLayers);

		generateWeightsMatrices();

		// Deactivate training and testing tabs until config is saved
		tbTraining.setDisable(true);
		tbPrediction.setDisable(true);
	}

	@FXML
	/**
	 * Saves the initial configuration provided by the user Triggered when the Save
	 * button is pressed
	 * 
	 * @param event
	 */
	public void saveConfigButtonHandler(ActionEvent event) throws ParseException {
		try {
			// Save weights
			double[][][] weights = new double[nbLayers][][];
			for (int i = 0; i < nbLayers; i++) {
				// Determine number of matrix rows
				int nbRows;
				if (i == 0)
					nbRows = nbInputCells;
				else
					nbRows = nbNeuronsPerLayer.get(i - 1);
				weights[i] = new double[nbRows][];
				for (int j = 0; j < nbRows; j++) {
					weights[i][j] = new double[nbNeuronsPerLayer.get(i)];
					for (int k = 0; k < nbNeuronsPerLayer.get(i); k++) {
						weights[i][j][k] = Integer.parseInt(tfsWeights[i][j][k].getText());
					}
				}
			}

			// Save weights as matrices
			weightMatrices = new Matrix[nbLayers];
			for (int i = 0; i < nbLayers; i++)
				weightMatrices[i] = new Matrix(weights[i]);

			// Save thresholds
			thresholds = new int[tfsThresholds.size()];
			for (int i = 0; i < tfsThresholds.size(); i++) {
				thresholds[i] = Integer.parseInt(tfsThresholds.get(i).getText());
			}

			// Save activation function
			if (tgActivationFunctions.getSelectedToggle() != null)
				activationFunction = tgActivationFunctions.getSelectedToggle().getUserData().toString();

			// Save error function
			if (tgErrorFunctions.getSelectedToggle() != null)
				errorFunction = tgErrorFunctions.getSelectedToggle().getUserData().toString();

			// Save max iterations
			maxIterations = Integer.parseInt(tfMaxIterations.getText());

			// Save error threshold
			NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
			errThreshold = format.parse(tfErrThreshold.getText()).doubleValue();

			// Activate training and testing tabs
			tbTraining.setDisable(false);
			tbPrediction.setDisable(false);
			
			// Clear previous results
			vbTrainingResults.getChildren().clear();
			gPerceptronDrawing.getChildren().clear();
			vbPredictionResults.getChildren().clear();
			
			// Generate prediction input/output entries
			hbPredictionInputs.getChildren().clear();
			tfsPredictionInputs = new ArrayList<TextField>();
			for (int i = 0; i < nbInputCells; i++) {
				TextField tf = new TextField();
				tf.setPromptText("Input " + (i + 1));
				hbPredictionInputs.getChildren().add(tf);
				tfsPredictionInputs.add(tf);
			}
			
			// Generate training set
			nbTrainingSets = 0;
			trainingSets = new ArrayList<ArrayList<ArrayList<TextField>>>();
			vbTrainingSets.getChildren().clear();
			addTrainingSet();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Invalid settings provided !");

			alert.showAndWait();
		}
	}

	@FXML
	/**
	 * Runs training algorithm Triggered when the Run Training button is clicked
	 * 
	 * @param event
	 */
	public void runTrainingButtonHandler(ActionEvent event) {
		try {
			vbTrainingResults.getChildren().clear();
			
			for(int a = 0; a < trainingSets.size(); a++) {
				vbTrainingResults.getChildren().add(new Text("Training on set: " + (a + 1) + "\n"));
				
				// Save input matrix
				double[][] inputs = new double[nbInputCells][1];
				for (int i = 0; i < nbInputCells; i++) {
					inputs[i][0] = Double.parseDouble(trainingSets.get(a).get(0).get(i).getText());
				}
				givenInputMatrix = new Matrix(inputs);
	
				// Save output matrix
				double[][] outputs = new double[nbOutputCells][1];
				for (int i = 0; i < nbOutputCells; i++) {
					outputs[i][0] = Double.parseDouble(trainingSets.get(a).get(1).get(i).getText());
				}
				givenOutputMatrix = new Matrix(outputs);
	
				// Activate results tabs
				tbTrainingResults.setDisable(false);
				tbTrainingVisualization.setDisable(false);
	
				// Compute actual output and update weights accordingly
				for (int i = 0; i < maxIterations; i++) {
					computeActualOutput(null, 0);
	
					double meanError = computeMeanError();
					if (meanError > errThreshold)
						updateWeights(null, nbLayers - 1);
					else {
						vbTrainingResults.getChildren().add(new Text(
								"It took " + (i + 1) + " iterations (" + i + " update(s)) for the algorithm to converge"));
	
						break;
					}
	
					// Print results on screen
					VBox vbResults = new VBox();
					vbResults.setMaxHeight(Double.MAX_VALUE);
					vbResults.getChildren().add(new Text("Iteration " + (i + 1)));
					vbResults.getChildren().add(new Text("Actual output:"));
					vbResults.getChildren().add(new Text(matrixToString(actualOutputs.get(actualOutputs.size() - 1))));
					vbResults.getChildren().add(new Text("Mean error: " + String.format("%.2f", meanError)));
	
					for (int j = 0; j < weightMatrices.length; j++) {
						vbResults.getChildren().add(new Text("Updated weights for layer " + (j + 1) + ":"));
						vbResults.getChildren().add(new Text(matrixToString(weightMatrices[j])));
					}
					
					vbTrainingResults.getChildren().add(vbResults);
				}
	
				// Draw the perceptron
				drawPerceptron();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Configuration error: Training aborted !");

			alert.showAndWait();
		}
	}
	
	@FXML
	/**
	 * Runs prediction based on inputs provided by the user
	 * @param event
	 */
	public void runPredictionButtonHandler(ActionEvent event) {
		try {
			// Save input matrix
			double[][] inputs = new double[nbInputCells][1];
			for (int i = 0; i < nbInputCells; i++) {
				inputs[i][0] = Double.parseDouble(tfsPredictionInputs.get(i).getText());
			}
			
			givenInputMatrix = new Matrix(inputs);
			computeActualOutput(null, 0);
			
			vbPredictionResults.getChildren().clear();
			vbPredictionResults.getChildren().add(new Label("Output: "));
			vbPredictionResults.getChildren().add(new Label(matrixToString(actualOutputs.get(actualOutputs.size() - 1))));
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Configuration error: Training aborted !");

			alert.showAndWait();
		}
	}
	
	@FXML
	public void addTrainingSetButtonHandler(ActionEvent event) {
		addTrainingSet();
	}
	
	@FXML
	public void removeTrainingSetButtonHandler(ActionEvent event) {
		removeTrainingSet();
	}
	
	private void removeTrainingSet() {
		if(nbTrainingSets > 1) {
			nbTrainingSets--;
			
			vbTrainingSets.getChildren().remove(vbTrainingSets.getChildren().size() - 1); // Remove outputs
			vbTrainingSets.getChildren().remove(vbTrainingSets.getChildren().size() - 1); // Remove inputs
			vbTrainingSets.getChildren().remove(vbTrainingSets.getChildren().size() - 1); // Remove label
			
			trainingSets.remove(trainingSets.size() - 1);
		}
	}
	
	private void addTrainingSet() {
		nbTrainingSets++;
		vbTrainingSets.getChildren().add(new Label("Set " + nbTrainingSets));
		
		trainingSets.add(new ArrayList<>());
		
		// Input set
		HBox hbTrainingInputs = new HBox();
		hbTrainingInputs.setSpacing(10);
		
		ArrayList<TextField> tfsTrainingInputs = new ArrayList<TextField>();
		for (int i = 0; i < nbInputCells; i++) {
			TextField tf = new TextField();
			tf.setPromptText("Input " + (i + 1));
			hbTrainingInputs.getChildren().add(tf);
			tfsTrainingInputs.add(tf);
		}
		trainingSets.get(trainingSets.size() - 1).add(tfsTrainingInputs);
		
		vbTrainingSets.getChildren().add(hbTrainingInputs);
		
		// Output set
		HBox hbTrainingOutputs = new HBox();
		hbTrainingOutputs.setSpacing(10);
		
		ArrayList<TextField> tfsTrainingOutputs = new ArrayList<TextField>();
		for (int i = 0; i < nbOutputCells; i++) {
			TextField tf = new TextField();
			tf.setPromptText("Output " + (i + 1));
			hbTrainingOutputs.getChildren().add(tf);
			tfsTrainingOutputs.add(tf);
		}
		trainingSets.get(trainingSets.size() - 1).add(tfsTrainingOutputs);
		
		vbTrainingSets.getChildren().add(hbTrainingOutputs);
	}

	/**
	 * Computes the actual output based on the training inputs provided by the user
	 * Recursive method that goes through all the layers
	 * @param matrix
	 * @param i
	 */
	private void computeActualOutput(Matrix matrix, int i) {
		if (matrix == null || i == 0) {
			matrix = givenInputMatrix;
			actualOutputs = new ArrayList<Matrix>();
		}

		Matrix weight = weightMatrices[i];

		Matrix computedMatrix = weight.transpose().times(matrix);
		Matrix thresholdMatrix = new Matrix(computedMatrix.getRowDimension(), computedMatrix.getColumnDimension(), thresholds[i]);
		computedMatrix = computedMatrix.minus(thresholdMatrix);
		double[][] computedMatrixTo2DArray = computedMatrix.getArray();

		// Compute activation function
		double[][] actualOutput = new double[computedMatrixTo2DArray.length][computedMatrixTo2DArray[0].length];
		if (activationFunction.equals("unitFunction")) {
			for (int j = 0; j < computedMatrixTo2DArray.length; j++) {
				for (int k = 0; k < computedMatrixTo2DArray[j].length; k++) {
					if (computedMatrixTo2DArray[j][k] > 0)
						actualOutput[j][k] = 1;
					else
						actualOutput[j][k] = 0;
				}
			}
		} else if (activationFunction.equals("linearFunction")) {
			for (int j = 0; j < computedMatrixTo2DArray.length; j++) {
				for (int k = 0; k < computedMatrixTo2DArray[j].length; k++) {
					if (computedMatrixTo2DArray[j][k] < -1)
						actualOutput[j][k] = 0;
					else if (computedMatrixTo2DArray[j][k] > 1)
						actualOutput[j][k] = 1;
					else
						actualOutput[j][k] = (computedMatrixTo2DArray[j][k] + 1) / 2;
				}
			}
		} else if (activationFunction.equals("identityFunction")) {
			for (int j = 0; j < computedMatrixTo2DArray.length; j++) {
				for (int k = 0; k < computedMatrixTo2DArray[j].length; k++) {
					actualOutput[j][k] = computedMatrixTo2DArray[j][k];
				}
			}
		} else if (activationFunction.equals("sigmoidFunction")) {
			for (int j = 0; j < computedMatrixTo2DArray.length; j++) {
				for (int k = 0; k < computedMatrixTo2DArray[j].length; k++) {
					actualOutput[j][k] = (Math.exp(computedMatrixTo2DArray[j][k]) - 1)
							/ (Math.exp(computedMatrixTo2DArray[j][k]) + 1);
				}
			}
		} else if (activationFunction.equals("gaussianFunction")) {
			for (int j = 0; j < computedMatrixTo2DArray.length; j++) {
				for (int k = 0; k < computedMatrixTo2DArray[j].length; k++) {
					actualOutput[j][k] = Math.exp(Math.pow(computedMatrixTo2DArray[j][k] - 1, 2));
				}
			}
		}

		actualOutputs.add(new Matrix(actualOutput)); // Save the output of each iteration

		// Jump to next layer
		if (i + 1 < nbLayers)
			computeActualOutput(new Matrix(actualOutput), i + 1);
	}

	/**
	 * Computes the mean error between the given and actual outputs based on the
	 * error function selected
	 * 
	 * @return mean error
	 */
	private double computeMeanError() {
		double[][] actualOutput = actualOutputs.get(actualOutputs.size() - 1).getArray(); // Get last training
																								// output matrix as 2D
																								// array
		double[][] givenOutput = givenOutputMatrix.getArray();

		double summation = 0;
		for (int i = 0; i < actualOutput.length; i++) {
			if (errorFunction.equals("meanDifErrorFunction"))
				summation += givenOutput[i][0] - actualOutput[i][0];
			else if (errorFunction.equals("meanAbsErrorFunction"))
				summation += Math.abs(givenOutput[i][0] - actualOutput[i][0]);
			else if (errorFunction.equals("meanSquErrorFunction"))
				summation += Math.pow(givenOutput[i][0] - actualOutput[i][0], 2);
		}

		return (summation / actualOutput.length);
	}

	/**
	 * Updates the weights
	 * @param prevErr
	 * @param i
	 */
	private void updateWeights(Matrix prevErr, int i) {
		Matrix err;
		if (prevErr == null) { //First iteration
			tempWeightMatrices = weightMatrices.clone(); // Holds original weights provided by the user --> Used to compute err
			err = givenOutputMatrix.minus(actualOutputs.get(actualOutputs.size() - 1));
		} else
			err = tempWeightMatrices[i + 1].times(prevErr);

		Matrix delta;
		if (i > 0)
			delta = err.times(actualOutputs.get(i - 1).transpose()).transpose();
		else
			delta = err.times(givenInputMatrix.transpose()).transpose();

		weightMatrices[i] = weightMatrices[i].plus(delta);

		if (i > 0)
			updateWeights(err, i - 1);

		return;
	}

	/**
	 * Converts a matrix to a string
	 * @param matrix
	 * @return
	 */
	private String matrixToString(Matrix matrix) {
		StringBuilder matrixAsString = new StringBuilder("");
		double[][] matrixTo2DArray = matrix.getArray();
		for (int i = 0; i < matrixTo2DArray.length; i++) {
			matrixAsString.append("|\t");
			for (int j = 0; j < matrixTo2DArray[i].length; j++) {
				matrixAsString.append(String.format("%-+8.2f", matrixTo2DArray[i][j]));
			}
			matrixAsString.append("\t|\n");
		}

		return matrixAsString.toString().substring(0, matrixAsString.toString().length() - 1);
	}

	/**
	 * Adds a new GUI entry allowing the user to choose the number of neurons for a
	 * given layer
	 */
	private void addNeuronsPerLayerEntry() {
		nbNeuronsPerLayer.add(1);

		AnchorPane pane = new AnchorPane();
		pane.getChildren().add(new Label("Number of neurons for layer " + nbLayers));

		Button btDecNbNeurons = new Button("-");
		btDecNbNeurons.setId((nbNeuronsPerLayer.size() - 1) + "");
		AnchorPane.setTopAnchor(btDecNbNeurons, 25.0);
		pane.getChildren().add(btDecNbNeurons);

		TextField tfNbNeurons = new TextField("1");
		tfNbNeurons.setAlignment(Pos.CENTER);
		tfNbNeurons.setPrefWidth(80);
		AnchorPane.setTopAnchor(tfNbNeurons, 25.0);
		AnchorPane.setLeftAnchor(tfNbNeurons, 32.0);
		pane.getChildren().add(tfNbNeurons);

		Button btIncNbNeurons = new Button("+");
		btIncNbNeurons.setId((nbNeuronsPerLayer.size() - 1) + "");
		AnchorPane.setTopAnchor(btIncNbNeurons, 25.0);
		AnchorPane.setLeftAnchor(btIncNbNeurons, 120.0);
		pane.getChildren().add(btIncNbNeurons);

		// Decrement number of neurons for that layer when the decrement button is
		// pressed
		btDecNbNeurons.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				int idButtonClicked = Integer.parseInt(((Button) e.getSource()).getId()); // Get the ID of the button clicked which is mapped to the nbNeuronsPerLayer array

				if (nbNeuronsPerLayer.get(idButtonClicked) <= 1)
					return;

				nbNeuronsPerLayer.set(idButtonClicked, nbNeuronsPerLayer.get(idButtonClicked) - 1);
				tfNbNeurons.setText(nbNeuronsPerLayer.get(idButtonClicked) + "");

				generateWeightsMatrices();

				// Deactivate training and testing tabs until config is saved
				tbTraining.setDisable(true);
				tbPrediction.setDisable(true);
			}
		});

		// Increment number of neurons for that layer when the increment button is pressed
		btIncNbNeurons.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				int idButtonClicked = Integer.parseInt(((Button) e.getSource()).getId()); // Get the ID of the button clicked which is mapped to the nbNeuronsPerLayer array

				nbNeuronsPerLayer.set(idButtonClicked, nbNeuronsPerLayer.get(idButtonClicked) + 1);
				tfNbNeurons.setText(nbNeuronsPerLayer.get(idButtonClicked) + "");

				generateWeightsMatrices();

				// Deactivate training and testing tabs until config is saved
				tbTraining.setDisable(true);
				tbPrediction.setDisable(true);
			}
		});

		vbNeuronsPerLayer.getChildren().add(pane);
	}

	/**
	 * Generates the weights matrices based on the number of inputs, layers and
	 * neurons per layer Method triggered every time one of the above properties is
	 * updated
	 */
	private void generateWeightsMatrices() {
		hbActivationWeights.getChildren().clear();

		for (int i = 0; i < nbLayers; i++) {
			VBox weightsPerLayer = new VBox();
			weightsPerLayer.getChildren().add(new Label("Weights layer " + (i + 1)));

			GridPane matrix = new GridPane();
			matrix.setHgap(5);
			matrix.setVgap(5);
			matrix.setPadding(new Insets(10, 10, 0, 0));

			// Determine number of matrix rows
			int nbRows;
			if (i == 0)
				nbRows = nbInputCells;
			else
				nbRows = nbNeuronsPerLayer.get(i - 1);

			for (int j = 0; j < nbRows; j++) {
				for (int k = 0; k < nbNeuronsPerLayer.get(i); k++) {
					TextField tf = new TextField("0");
					tf.setPrefWidth(30);
					matrix.add(tf, k, j);

					// Save textfield in array
					tfsWeights[i][j][k] = tf;
				}
			}

			weightsPerLayer.getChildren().add(matrix);
			hbActivationWeights.getChildren().add(weightsPerLayer);
		}
	}

	/**
	 * Adds a new threshold entry when a layer gets added
	 */
	private void addLayerThresholdEntry() {
		VBox vbox = new VBox();
		vbox.getChildren().add(new Label("Threshold for layer " + nbLayers));

		TextField tf = new TextField("0");
		vbox.getChildren().add(tf);
		tfsThresholds.add(tf);

		vbActivationThresholds.getChildren().add(vbox);
	}

	/**
	 * Draws a perceptron after training
	 */
	private void drawPerceptron() {
		gPerceptronDrawing.getChildren().clear();

		// Draw input cells
		for (int i = 1; i <= nbInputCells; i++) {
			Circle input = new Circle(0, i * 100, 5, Color.BLACK);
			Text inputLabel = new Text(0, i * 100 - 10, "X" + i);
			gPerceptronDrawing.getChildren().addAll(input, inputLabel);
		}

		// Draw layers
		for (int i = 1; i <= nbLayers; i++) {
			// Draw neurons
			for (int j = 1; j <= nbNeuronsPerLayer.get(i - 1); j++) {
				Circle layer = new Circle(500 * i, j * 100, 30);
				layer.setFill(Color.TRANSPARENT);
				layer.setStroke(Color.BLACK);
				Text layerLabel = new Text(500 * i - 8, j * 100 + 5, "f, θ");
				gPerceptronDrawing.getChildren().addAll(layer, layerLabel);

				// Draw the connection between the inputs and the neurons of the first layer
				if (i == 1) {
					for (int k = 1; k <= nbInputCells; k++) {
						Line connection = new Line(500 - 30, 100 * j, 0, 100 * k);
						connection.setOnMouseEntered(event -> {
							connection.setStrokeWidth(2.0);
						});
						connection.setOnMouseExited(event -> {
							connection.setStrokeWidth(1.0);
						});
						// Save counters as final so they can be used inside the lambda
						final int index1 = i - 1;
						final int index2 = j - 1;
						final int index3 = k - 1;
						connection.setOnMouseClicked(event -> {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Information Dialog");
							alert.setHeaderText(null);
							alert.setContentText("Weight: " + weightMatrices[index1].get(index3, index2));

							alert.showAndWait();
						});

						gPerceptronDrawing.getChildren().add(connection);
					}
				}

				if (i < nbLayers && nbLayers > 1) { // Draw the connections between the neurons of each layer
					for (int k = 1; k <= nbNeuronsPerLayer.get(i); k++) {
						Line connection = new Line(500 * i + 30, 100 * j, 500 * (i + 1) - 30, 100 * k);
						connection.setOnMouseMoved(event -> {
							connection.setStrokeWidth(2.0);
						});
						connection.setOnMouseExited(event -> {
							connection.setStrokeWidth(1.0);
						});
						// Save counters as final so they can be used inside the lambda
						final int index1 = i;
						final int index2 = j - 1;
						final int index3 = k - 1;
						connection.setOnMouseClicked(event -> {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Information Dialog");
							alert.setHeaderText(null);
							alert.setContentText("Weight: " + weightMatrices[index1].get(index2, index3));

							alert.showAndWait();
						});

						gPerceptronDrawing.getChildren().add(connection);
					}
				} else { // Draw the outputs
					Circle output = new Circle(500 * i + 100, j * 100, 5, Color.BLACK);
					Text outputLabel = new Text(500 * i + 100, j * 100 - 10, "Y" + j);

					Line connection = new Line(500 * i + 30, 100 * j, 500 * i + 100, 100 * j);

					gPerceptronDrawing.getChildren().addAll(output, outputLabel, connection);
				}
			}
		}
	}
}
