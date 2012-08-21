package cs485.simulatedcarracing;

import org.encog.neural.networks.BasicNetwork;

import scr.Action;
import scr.Controller;
import scr.SensorModel;

public class MainController extends Controller {
	
	BasicNetwork network;
	
	public MainController() {
		network = new BasicNetwork();
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
