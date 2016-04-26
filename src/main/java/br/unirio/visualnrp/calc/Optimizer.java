package br.unirio.visualnrp.calc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import sobol.problems.requirements.model.Project;
import sobol.problems.requirements.reader.RequirementReader;
import br.unirio.visualnrp.algorithm.constructor.GreedyConstructor;
import br.unirio.visualnrp.algorithm.search.HillClimbing;
import br.unirio.visualnrp.algorithm.search.IteratedLocalSearch;
import br.unirio.visualnrp.algorithm.search.SearchAlgorithm;
import br.unirio.visualnrp.algorithm.search.VisIteratedLocalSearch;
import br.unirio.visualnrp.algorithm.solution.Solution;

/**
 * Classe responsável pela otimização
 * 
 * @author Marcio
 */
public class Optimizer
{
	private static int CYCLES = 30;
	
	private static int MAXEVALUATIONS = 10000000;
	
	private static int SAMPLE_SIZE = 10;
	
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
		double max = 0;
		
		for (int i = 0; i < CYCLES; i++)
		{
			SearchAlgorithm algorithm = createAlgorithm(type, detailsWriter, project, budget, constructor);

			long initTime = System.currentTimeMillis();
			detailsWriter.println(type.name() + " " + project.getName() + " #" + CYCLES);
			boolean[] solution = algorithm.execute();
			detailsWriter.println();
			long executionTime = (System.currentTimeMillis() - initTime);

			String s = type.name() + "; " + name + "; " + i + "; " + executionTime;
			s += "; " + algorithm.getFitness() + "; " + algorithm.getIterationBestFound();
			s += "; " + Solution.printSolution(solution);
			outWriter.println(s);
			
			sum += algorithm.getFitness();
			if (algorithm.getFitness() > max) max = algorithm.getFitness();
			System.out.print("*");
		}

		System.out.println(" " + type.name() + "\t" + name + "\t" + (sum/CYCLES) + "\t" + max);
	}
	
	private List<Project> loadProjects(String[] filenames)
	{
		List<Project> projects = new ArrayList<Project>();
		
		for (String filename : filenames)
		{
			RequirementReader reader = new RequirementReader(filename);
			Project project = reader.execute();
			projects.add(project);
		}
		
		return projects;
	}

	public Optimizer execute(String[] filenames, double[] budgetFactors) throws Exception
	{
		PrintWriter outWriter = new PrintWriter(new FileWriter("saida.txt", true));
		PrintWriter detailsWriter = new PrintWriter(new FileWriter("saida details.txt", true));

		List<Project> projects = loadProjects(filenames);
		
		for (double budgetFactor : budgetFactors)
			for (Project project : projects)
				for (Algorithm algorithm : Algorithm.values())
					runInstance(outWriter, detailsWriter, algorithm, project, budgetFactor);
		
		outWriter.close();
		detailsWriter.close();
		return this;
	}
	
	public Optimizer clean()
	{
		File out = new File("saida.txt");
		
		if (out.exists())
			out.delete();
		
		File details = new File("saida details.txt");
		
		if (details.exists())
			details.delete();

		return this;
	}
}