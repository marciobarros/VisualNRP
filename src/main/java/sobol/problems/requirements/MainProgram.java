package sobol.problems.requirements;

import sobol.problems.requirements.calc.LandscapeReleasePlanning;
import sobol.problems.requirements.calc.LandscapeReport;
import sobol.problems.requirements.calc.Optimizer;

/**
 * Programa principal
 * 
 * @author Marcio
 */
@SuppressWarnings("unused")
public class MainProgram
{
	private static String[] classicInstanceFilenames =
	{
		"data/requirements/classic/nrp1.txt",
		"data/requirements/classic/nrp2.txt",
		"data/requirements/classic/nrp3.txt",
		"data/requirements/classic/nrp4.txt",
		"data/requirements/classic/nrp5.txt"
	};
	
	private static String[] realisticInstanceFilenames =
	{
		"data/requirements/realistic/nrp-e1.txt",
		"data/requirements/realistic/nrp-e2.txt",
		"data/requirements/realistic/nrp-e3.txt",
		"data/requirements/realistic/nrp-e4.txt",
		"data/requirements/realistic/nrp-g1.txt",
		"data/requirements/realistic/nrp-g2.txt",
		"data/requirements/realistic/nrp-g3.txt",
		"data/requirements/realistic/nrp-g4.txt",
		"data/requirements/realistic/nrp-m1.txt",
		"data/requirements/realistic/nrp-m2.txt",
		"data/requirements/realistic/nrp-m3.txt",
		"data/requirements/realistic/nrp-m4.txt"
	};
	
	private void runOptimizer() throws Exception
	{
		new Optimizer()
			.clean()
			.execute(classicInstanceFilenames, new double[] {0.3, 0.5, 0.7})
			.execute(realisticInstanceFilenames, new double[] {0.3, 0.5});
	}

	private void runLandscapeReport() throws Exception
	{
		new LandscapeReleasePlanning().execute(classicInstanceFilenames[0]);

//		new LandscapeReport()
//			.execute(classicInstanceFilenames, new int[] {30}, new int[] {20, 50, 80});
		
//		new LandscapeReport()
//			.execute(classicInstanceFilenames, new int[] {30, 50, 70}, new int[] {0})
//			.execute(realisticInstanceFilenames, new int[] {30, 50}, new int[] {0});
	}

	public static void main(String[] args) throws Exception
	{
//		new MainProgram().runOptimizer();
		new MainProgram().runLandscapeReport();
	}
}