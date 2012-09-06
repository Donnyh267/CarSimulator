package cs485.simulatedcarracing;

import java.util.HashMap;
import java.util.Map;

import org.encog.neural.networks.BasicNetwork;

import scr.Action;
import scr.Controller;
import scr.SensorModel;

public class MainController extends Controller {
	
	BasicNetwork network;
	HumanDriver human;
	
	public MainController() {
		network = new BasicNetwork();
		human = new HumanDriver();
	}

	@Override
	public Action control(SensorModel sensors) {
		return human.getAction(sensors);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
