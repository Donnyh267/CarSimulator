package cs485.neuralnetwork;

import java.io.File;
import java.util.ArrayList;

import org.encog.ml.data.MLData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

import cs485.simulatedcarracing.HumanDriver;

import scr.Action;
import scr.SensorModel;

public class NeuralNetwork {
	
	private static int INPUTLAYER = 22;
	private static int[] HIDDENLAYER = {20,8};
	private static int OUTPUTLAYER = 4;
	
	private final String FILENAME = "network.eg";
	private BasicNetwork network;
	private BasicNeuralDataSet dataset;
	
	public NeuralNetwork() {
		try {
			load();
		} catch (Exception e) {
			initialize();
			System.out.println("No file loaded. Created new NN");
		}
		dataset = new BasicNeuralDataSet();
	}
	
	//Build & Initialize the Network
	public void initialize() {
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(INPUTLAYER));	//Input Layer
		for (int i:HIDDENLAYER) 
			network.addLayer(new BasicLayer(i));	//Hidden Layer; you can add extra hidden layers
		network.addLayer(new BasicLayer(OUTPUTLAYER));	//Output Layer
		network.getStructure().finalizeStructure();
		network.reset();	//Initialize weights
	}
	
	public void addData(SensorModel sensors, Action action){
		addData(sensorToInput(sensors), actionToOutput(action));
	}
	
	public void addData(BasicNeuralData input, BasicNeuralData idealOutout){
		dataset.add(input, idealOutout);
	}
	
	//trainer function
	public void train() {
		final ResilientPropagation train = new ResilientPropagation(network, dataset);
//		Backpropagation train = new Backpropagation(network, dataset);
//		NelderMeadTraining train = new NelderMeadTraining(network, dataset);
//		LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(network, dataset);
//		QuickPropagation train = new QuickPropagation(network, dataset);
//		ScaledConjugateGradient train = new ScaledConjugateGradient(network, dataset); 
//		ManhattanPropagation train = new ManhattanPropagation(network, dataset, 9.9); 
		
		System.out.println("dataset: " + dataset.getRecordCount()); 
		System.out.println("Training Network...");
		train.setThreadCount(5);
		int epoch = 1;
		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch += 1;
		} while (epoch <= 30000 && train.getError() > 0.001);
		train.finishTraining();
		System.out.println("Neural Network's Error:"+train.getError());
		
		this.save();
	}
	
	//Get Action based upon SensorModel
	public Action getAction(SensorModel sensors) {
		
		
		//Enter sensor Data into Neural Network
		BasicNeuralData input = sensorToInput(sensors);
		MLData output = network.compute(input);
		
		//Create Action
		Action newAction = new Action();
		newAction.accelerate = output.getData(0);
		newAction.brake = output.getData(1);
		newAction.clutch = output.getData(2);
//		newAction.gear = (int) output.getData(3);
		newAction.gear = HumanDriver.getGear(sensors);
		newAction.steering = output.getData(3);
//		if (output.getData(5) < 10 && output.getData(5) >= 0)
			newAction.restartRace = false;
//		else
//			newAction.restartRace = true;
//		newAction.focus = (int) output.getData(6);
		return newAction;
	}
	
	public static BasicNeuralData sensorToInput(SensorModel sensors) {
		//Fill sensor Data
		ArrayList<Double> inputList = new ArrayList<Double>();
		inputList.add(sensors.getSpeed()/250);	//Speed
		inputList.add(sensors.getAngleToTrackAxis()/360);	//Angle
		for (double d : sensors.getTrackEdgeSensors())	//19 Track Edge sensors
			inputList.add(d/200);
		/*
		for (double d : sensors.getFocusSensors())	//5 Focus Sensors
			inputList.add(d);
		*/
//		inputList.add(sensors.getTrackPosition());	//Track Position
//		inputList.add((double)sensors.getGear()/6);	//Gear
		inputList.add((double)sensors.getRPM()/12000);
		
		double[] inputArray = new double[inputList.size()];
		for (int i = 0; i < inputList.size(); i++)
			inputArray[i] = inputList.get(i);
		
		BasicNeuralData input = new BasicNeuralData(inputArray);
		return input;
	}
	
	public static BasicNeuralData actionToOutput(Action action) {
		BasicNeuralData output = new BasicNeuralData(7);
		output.setData(0, action.accelerate);
		output.setData(1, action.brake);
		output.setData(2, action.clutch);
//		output.setData(3, action.gear);
		output.setData(3, action.steering);
//		if (action.restartRace)
//			output.setData(5, 1);
//		else
//			output.setData(5, 0);
//		output.setData(5, action.focus);
		return output;
	}
	
	private void save() {
		EncogDirectoryPersistence.saveObject(new File(FILENAME), this.network);
	}
	
	private void load() {
		network = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(FILENAME));
	}

}
