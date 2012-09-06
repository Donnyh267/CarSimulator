package cs485.neuralnetwork;

import java.awt.List;
import java.util.ArrayList;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import scr.Action;
import scr.SensorModel;

public class NeuralNetwork {
	
	private BasicNetwork network = new BasicNetwork();
	
	public void initialize() {
		network.addLayer(new BasicLayer(28));	//Input Layer
		network.addLayer(new BasicLayer(10));	//Hidden Layer
		network.addLayer(new BasicLayer(7));	//Output Layer
		network.getStructure().finalizeStructure();
		network.reset();	//Initialize weights
	}
	
	//trainer function
	public void train() {
		
	}
	
	public Action getAction(SensorModel sensors) {
		//Fill sensor Data
		ArrayList<Double> inputList = new ArrayList<Double>();
		inputList.add(sensors.getSpeed());	//Speed
		inputList.add(sensors.getAngleToTrackAxis());	//Angle
		for (double d : sensors.getTrackEdgeSensors())	//19 Track Edge sensors
			inputList.add(d);
		for (double d : sensors.getFocusSensors())	//5 Focus Sensors
			inputList.add(d);
		inputList.add(sensors.getTrackPosition());	//Track Position
		inputList.add((double)sensors.getGear());	//Gear
		
		double[] inputArray = new double[inputList.size()];
		for (int i = 0; i < inputList.size(); i++)
			inputArray[i] = inputList.get(i);
		
		//Enter sensor Data into Neural Network
		MLData input = (MLData) new BasicNeuralData(inputArray);
		MLData output = network.compute(input);
		
		//Create Action
		Action newAction = new Action();
		newAction.accelerate = output.getData(0);
		newAction.brake = output.getData(1);
		newAction.clutch = output.getData(2);
		newAction.gear = (int) output.getData(3);
		newAction.steering = output.getData(4);
		if (output.getData(5) < 0.50 && output.getData(5) >= 0)
			newAction.restartRace = true;
		else
			newAction.restartRace = false;
		newAction.focus = (int) output.getData(6);
		return newAction;
	}

}