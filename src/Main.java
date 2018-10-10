import org.xcsp.modeler.api.ProblemAPI;

public class Main implements ProblemAPI{
	Station[] stations;
	int[] regions;
	Interference[] interferences;
	Liaison[] liaisons;
	
	class Station {
		int num;
		int region;
		int delta;
		int[] emetteur;
		int[] recepteur;
	}
	
	class Interference {
		int x;
		int y;
		int Delta;
	}
	
	class Liaison {
		int x;
		int y;
	}
	@Override
	public void model() {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void main(String[] args)
	{
		System.out.println("Tut tut");
	}
}
