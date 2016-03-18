package sobol.problems.requirements;

import sobol.problems.requirements.report.landscape.LandscapeReport;

public class VisualizationMainProgram
{
	private static String[] instanceFilenames =
	{
		"data/requirements/classic/nrp1.txt",
		"data/requirements/classic/nrp2.txt",
		"data/requirements/classic/nrp3.txt",
		"data/requirements/classic/nrp4.txt",
		"data/requirements/classic/nrp5.txt",
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
	
	public static final void main(String[] args) throws Exception
	{
		new LandscapeReport().createLandscapes(instanceFilenames);
	}
}