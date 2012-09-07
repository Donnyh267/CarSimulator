package cs485.simulatedcarracing;

import java.util.Random;

import scr.Action;
import scr.Controller;
import scr.SensorModel;
import cs485.neuralnetwork.NeuralNetwork;

public class MainController extends Controller {
	
	NeuralNetwork network;
	HumanDriver human;

	private static boolean HUMANLESS = true;
	private static boolean TEACHING = false;
	private static double DATA_PROBABILITY = 0.02;
	
	public MainController() {
		if (HUMANLESS || TEACHING)
			network = new NeuralNetwork();
		if (!HUMANLESS)
			human = new HumanDriver(this);
	}

	@Override
	public Action control(SensorModel sensors) {
		if (HUMANLESS) 
			return network.getAction(sensors);
		else {
			Action h = human.getAction(sensors);
			if (TEACHING) {
				Random r = new Random();
				if (r.nextDouble() < DATA_PROBABILITY) {
					System.out.println("Added a data pair");
					network.addData(sensors, h);
				}
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
		if (TEACHING) {
			System.out.println("Training the network..");
			network.train();
		}
		if (!HUMANLESS) {
			human.shutdown();
		}
	}
}
