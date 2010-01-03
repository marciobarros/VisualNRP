package br.unirio.visualnrp.calc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.GreedyConstructor;
import br.unirio.visualnrp.algorithm.search.HillClimbing;
import br.unirio.visualnrp.algorithm.search.IteratedLocalSearch;
import br.unirio.visualnrp.algorithm.search.SearchAlgorithm;
import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.algorithm.search.VisIteratedLocalSearch;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Classe responsável pela otimização
 * 
 * @author Marcio
 */
public class Optimizer
{
	/**
	 * Number of optimization cycles
	 */
	private static int CYCLES = 30;
	
	/**
	 * Maximum number of evaluation rounds for the algorithms
	 */
	private static int MAXEVALUATIONS = 10000000;
	
	/**
	 * Sample size for the VISILS algorithm
	 */
	private static int SAMPLE_SIZE = 10;

	/**
	 * Available search algorithms
	 */
	enum Algorithm { VISILS, ILS, HC }

	/**
	 * Creates a search algoritm for the problem at hand
	 */
	private SearchAlgorithm createAlgorithm(Algorithm type, PrintWriter detailsWriter, Project project, int budget, int riskImportance, Constructor constructor) throws Exception
	{
		if (type == Algorithm.VISILS)
			return new VisIteratedLocalSearch(detailsWriter, project, budget, riskImportance, MAXEVALUATIONS, SAMPLE_SIZE, constructor);
		
		if (type == Algorithm.ILS)
			return new IteratedLocalSearch(detailsWriter, project, budget, riskImportance, MAXEVALUATIONS, constructor);

		if (type == Algorithm.HC)
			return new HillClimbing(detailsWriter, project, budget, riskImportance, MAXEVALUATIONS, constructor);
		
		return null;
	}
	
	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createReportForBudget(PrintWriter out, Project project, int budgetFactor, int riskImportance, Algorithm algorithm, IFitnessCalculator calculator) throws Exception
	{
		int budget = (int) (project.getTotalCost() * (budgetFactor / 100.0));
		Constructor constructor = new GreedyConstructor(project);
		calculator.prepare(project);
		
		String shortName = project.getName();
		shortName = shortName.substring(shortName.lastIndexOf('/') + 1);
		shortName = shortName.substring(0, shortName.lastIndexOf('.'));

		double sum = 0.0;
		double max = 0;
		
		for (int i = 0; i < CYCLES; i++)
		{
			SearchAlgorithm searchAlgorithm = createAlgorithm(algorithm, null, project, budget, riskImportance, constructor);
			boolean[] solution = searchAlgorithm.execute(calculator);

			String s = algorithm.name() + "," + shortName + "," + i + "," + budgetFactor + "," + riskImportance;
			s += "," + searchAlgorithm.getFitness() + "," + Solution.printSolution(solution);
			out.println(s);
			
			sum += searchAlgorithm.getFitness();
			if (searchAlgorithm.getFitness() > max) max = searchAlgorithm.getFitness();
			System.out.print("*");
		}

		System.out.println(String.format(" %-6s\t%-14s\t%.4f\t%.4f", algorithm.name(), shortName + "-" + budgetFactor, (sum/CYCLES), max));
	}
	
	/**
	 * Creates the optimization report report for a given instance
	 */
	private void createReport(PrintWriter out, Project project, int[] budgetFactors, int[] riskImportances, Algorithm[] algorithms, IFitnessCalculator calculator) throws Exception
	{
		for (int budgetFactor : budgetFactors)
		{
			for (int riskImportance : riskImportances)
			{
				for (Algorithm algorithm : algorithms)
				{
					createReportForBudget(out, project, budgetFactor, riskImportance, algorithm, calculator);
				}
			}
		}
	}
	
	/**
	 * Runs the optimization report for a set of instances
	 */
	private void execute(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, Algorithm[] algorithms, String outputFilename, IFitnessCalculator calculator) throws Exception
	{
		File file = new File(outputFilename);
		
		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();
		
		FileWriter outFile = new FileWriter(outputFilename);
		PrintWriter out = new PrintWriter(outFile);
		out.println("alg,instance,cycle,budget,risk,fit,solution");

		for (Instance instance : instances)
		{
			RequirementReader reader = new RequirementReader(instance.getFilename());
			Project project = reader.execute();
			System.out.println("Processing " + project.getName() + " ...");
			createReport(out, project, budgetFactors, riskImportances, algorithms, calculator);
		}
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Runs the optimization report for profit risk
	 */
	public void executeProfitRisk(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		Algorithm[] algorithms = { Algorithm.ILS, Algorithm.VISILS };
		execute(instances, budgetFactors, riskImportances, algorithms, outputFilename, new ProfitRiskFitnessCalculator());
	}
	
	/**
	 * Runs the optimization report for cost risk
	 */
	public void executeCostRisk(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		Algorithm[] algorithms = { Algorithm.ILS, Algorithm.VISILS };
		execute(instances, budgetFactors, riskImportances, algorithms, outputFilename, new CostRiskFitnessCalculator());
	}
	
	/**
	 * Runs the optimization report for profit
	 */
	public void executeProfit(Iterable<Instance> instances, int[] budgetFactors, String outputFilename) throws Exception
	{
		Algorithm[] algorithms = { /*Algorithm.HC, Algorithm.ILS, */Algorithm.VISILS };
		execute(instances, budgetFactors, new int[] { 0 }, algorithms, outputFilename, new ProfitFitnessCalculator());
	}
}