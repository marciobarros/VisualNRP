package br.unirio.visualnrp.calc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.GreedyConstructor;
import br.unirio.visualnrp.algorithm.search.HillClimbing;
import br.unirio.visualnrp.algorithm.search.IteratedLocalSearch;
import br.unirio.visualnrp.algorithm.search.SearchAlgorithm;
import br.unirio.visualnrp.algorithm.search.VisIteratedLocalSearch;
import br.unirio.visualnrp.algorithm.solution.Solution;
import br.unirio.visualnrp.instance.Instance;
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
	
	/**
	 * Calculates the fitness of a given solution
	 */
//	private double evaluate(boolean[] solution, Project project, double availableBudget, int riskImportance, double totalProfit, double totalCost, double totalRisk, IRiskLanscapeCalculator calculator) throws Exception
//	{
//		int cost = project.calculateCost(solution);
//		
//		if (cost > availableBudget)
//			return -cost / totalCost;
//
//		int profit = project.calculateProfit(solution);
//		double risk = calculator.calculateRisk(project, solution);
//
//		double alfa = riskImportance / 100.0;
//		return (1 - alfa) * profit / totalProfit + alfa * (totalRisk - risk) / totalRisk;
//	}
	
	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createReportForBudget(PrintWriter out, Project project, int budgetFactor, int riskImportance, Algorithm algorithm, double totalProfit, double totalCost, double totalRisk, IRiskLanscapeCalculator2 calculator) throws Exception
	{
		int budget = (int) (project.getTotalCost() * (budgetFactor / 100.0));
		Constructor constructor = new GreedyConstructor(project);
		
		String shortName = project.getName();
		shortName = shortName.substring(shortName.lastIndexOf('/') + 1);
		shortName = shortName.substring(0, shortName.lastIndexOf('.'));

		double sum = 0.0;
		double max = 0;
		
		for (int i = 0; i < CYCLES; i++)
		{
			SearchAlgorithm searchAlgorithm = createAlgorithm(algorithm, null, project, budget, constructor);
			boolean[] solution = searchAlgorithm.execute();
			// TODO como passar a função de fitness certa para o algoritmo ???

			String s = algorithm.name() + "," + shortName + "," + i + "," + budgetFactor + "," + riskImportance;
			s += "," + searchAlgorithm.getFitness() + "," + Solution.printSolution(solution);
			out.println(s);
			
			sum += searchAlgorithm.getFitness();
			if (searchAlgorithm.getFitness() > max) max = searchAlgorithm.getFitness();
			System.out.print("*");
		}

		System.out.println(" " + algorithm.name() + "\t" + shortName + "\t" + (sum/CYCLES) + "\t" + max);
	}
	
	/**
	 * Creates the optimization report report for a given instance
	 */
	private void createReport(PrintWriter out, Project project, int[] budgetFactors, int[] riskImportances, Algorithm[] algorithms, IRiskLanscapeCalculator2 calculator) throws Exception
	{
		double totalProfit = project.getTotalProfit();
		double totalCost = project.getTotalCost();
		double totalRisk = calculator.calculateTotalRisk(project);
		
		for (int budgetFactor : budgetFactors)
		{
			for (int riskImportance : riskImportances)
			{
				for (Algorithm algorithm : algorithms)
				{
					createReportForBudget(out, project, budgetFactor, riskImportance, algorithm, totalProfit, totalCost, totalRisk, calculator);
				}
			}
		}
	}
	
	/**
	 * Runs the optimization report for a set of instances
	 */
	private void execute(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, Algorithm[] algorithms, String outputFilename, IRiskLanscapeCalculator2 calculator) throws Exception
	{
		File file = new File(outputFilename);
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
		execute(instances, budgetFactors, riskImportances, algorithms, outputFilename, new ProfitRiskLanscapeCalculator2());
	}
	
	/**
	 * Runs the optimization report for cost risk
	 */
	public void executeCostRisk(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		Algorithm[] algorithms = { Algorithm.ILS, Algorithm.VISILS };
		execute(instances, budgetFactors, riskImportances, algorithms, outputFilename, new CostRiskLanscapeCalculator2());
	}
}

/**
 * Interface for the calculation of fitness landscapes
 * 
 * @author marciobarros
 */
interface IRiskLanscapeCalculator2
{
	double calculateTotalRisk(Project project);

	double calculateRisk(Project project, boolean[] solution);
}

/**
 * Class that supports the calculation of the fitness landscape for profit risk
 * 
 * @author marciobarros
 */
class ProfitRiskLanscapeCalculator2 implements IRiskLanscapeCalculator2
{
	public double calculateTotalRisk(Project project)
	{
		return project.getTotalCustomerRisk();
	}

	public double calculateRisk(Project project, boolean[] solution)
	{
		return project.calculateCustomerRisk(solution);
	}
}

/**
 * Class that supports the calculation of the fitness landscape for cost risk
 * 
 * @author marciobarros
 */
class CostRiskLanscapeCalculator2 implements IRiskLanscapeCalculator2
{
	public double calculateTotalRisk(Project project)
	{
		return project.getTotalRequirementRisk();	
	}

	public double calculateRisk(Project project, boolean[] solution)
	{
		return project.calculateRequirementRisk(solution);
	}
}