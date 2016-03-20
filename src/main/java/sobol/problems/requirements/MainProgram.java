package sobol.problems.requirements;

import java.io.FileWriter;
import java.io.PrintWriter;

import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.GreedyConstructor;
import sobol.problems.requirements.algorithm.search.HillClimbing;
import sobol.problems.requirements.algorithm.search.IteratedLocalSearch;
import sobol.problems.requirements.algorithm.search.SearchAlgorithm;
import sobol.problems.requirements.algorithm.search.VisIteratedLocalSearch;
import sobol.problems.requirements.algorithm.solution.Solution;
import sobol.problems.requirements.model.Project;
import sobol.problems.requirements.reader.RequirementReader;

/**
 * Programa principal do otimizador
 * 
 * @author Marcio
 */
public class MainProgram
{
	private static int CYCLES = 100;
	
	private static int MAXEVALUATIONS = 10000000;
	
	private static int SAMPLE_SIZE = 10;
	
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
	
	enum Algorithm { VISILS, ILS, HC }

	private SearchAlgorithm createAlgorithm(Algorithm type, PrintWriter detailsWriter, Project project, int budget, Constructor constructor) throws Exception
	{
		if (type == Algorithm.VISILS)
			return new VisIteratedLocalSearch(detailsWriter, project, budget, MAXEVALUATIONS, SAMPLE_SIZE, constructor);
		
		if (type == Algorithm.ILS)
			return new IteratedLocalSearch(detailsWriter, project, budget, MAXEVALUATIONS, constructor);

		if (type == Algorithm.HC)
			return new HillClimbing(detailsWriter, project, budget, MAXEVALUATIONS, constructor);
		
		return null;
	}

	private void runInstance(PrintWriter outWriter, PrintWriter detailsWriter, Algorithm type, Project project, double budgetFactor) throws Exception
	{
		Constructor constructor = new GreedyConstructor(project);
		int budget = (int) (budgetFactor * project.getTotalCost());
		
		String name = project.getName();
		name = name.substring(name.lastIndexOf('/') + 1);
		name = name.substring(0, name.lastIndexOf('.'));
		name = name + "-" + ((int)(budgetFactor * 100));

		double sum = 0.0;
		
		for (int i = 0; i < CYCLES; i++)
		{
			SearchAlgorithm algorithm = createAlgorithm(type, detailsWriter, project, budget, constructor);

			long initTime = System.currentTimeMillis();
			detailsWriter.println(type.name() + " " + project.getName() + " #" + CYCLES);
			boolean[] solution = algorithm.execute();
			detailsWriter.println();
			long executionTime = (System.currentTimeMillis() - initTime);

			String s = type.name() + "; " + name + "; " + i + "; " + executionTime + "; ";
			s += algorithm.getFitness() + "; " + algorithm.getIterationBestFound() + "; ";
			s += Solution.printSolution(solution);
			outWriter.println(s);
			
			sum += algorithm.getFitness();
			System.out.print("*");
		}

		System.out.println(type.name() + "\t" + name + "\t" + (sum/CYCLES));
	}

	private void runInstances(String[] filenames) throws Exception
	{
		double[] budgetFactors = {0.3, 0.5, 0.7};
		
		PrintWriter outWriter = new PrintWriter(new FileWriter("saida.txt"));
		PrintWriter detailsWriter = new PrintWriter(new FileWriter("saida details.txt"));

		for (String filename : filenames)
		{
			RequirementReader reader = new RequirementReader(filename);
			Project project = reader.execute();
			
			for (double budgetFactor : budgetFactors)
				runInstance(outWriter, detailsWriter, Algorithm.VISILS, project, budgetFactor);
		}
		
		outWriter.close();
		detailsWriter.close();
	}

	public static void main(String[] args) throws Exception
	{
		new MainProgram().runInstances(instanceFilenames);
//		new LandscapeReport().createLandscapes(instanceFilenames);
	}
}