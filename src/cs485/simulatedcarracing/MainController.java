package cs485.simulatedcarracing;

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
		// TODO Auto-generated method stub
		return null;
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
