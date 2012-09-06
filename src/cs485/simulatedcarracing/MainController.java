package cs485.simulatedcarracing;

import org.encog.neural.data.basic.BasicNeuralDataSet;

import scr.Action;
import scr.Controller;
import scr.SensorModel;
import cs485.neuralnetwork.NeuralNetwork;

public class MainController extends Controller {
	
	NeuralNetwork network;
	HumanDriver human;

	private static boolean HUMANLESS = false;
	private static boolean TEACHING = true;
	private BasicNeuralDataSet dataset;
	
	public MainController() {
		network = new NeuralNetwork();
		human = new HumanDriver(this);
		dataset = new BasicNeuralDataSet();
	}

	@Override
	public Action control(SensorModel sensors) {
		if (HUMANLESS) 
			return network.getAction(sensors);
		else {
			Action h = human.getAction(sensors);
			if (TEACHING) {
				//add sensors and humanAction to dataset
			}
			return h;
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO ask to train the network here
	}

}
