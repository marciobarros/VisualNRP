package br.unirio.visualnrp.calc.tunning;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.GreedyConstructor;
import br.unirio.visualnrp.algorithm.constructor.RandomConstructor;
import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.algorithm.search.SearchAlgorithm;
import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.calc.fitness.IFitnessCalculator;
import br.unirio.visualnrp.calc.fitness.ProfitFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Classe responsável pela definição do parâmetro de gerador de sementes para início da busca
 * 
 * @author Marcio
 */
public class InitialSeedTunning
{
	/**
	 * Number of optimization cycles
	 */
	private static int CYCLES = 30;
	
	/**
	 * Creates the output file and writes the header
	 */
	protected PrintWriter createOutputFile(String outputFilename) throws IOException
	{
		File file = new File(outputFilename);
		
		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();
		
		FileWriter outFile = new FileWriter(outputFilename);
		PrintWriter out = new PrintWriter(outFile);
		out.println("alg,instance,cycle,budget,risk,fit,solution");
		
		return out;
	}

	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	protected void createReportForBudget(PrintWriter out, Project project, int budgetFactor, int riskImportance, Algorithm algorithm, Constructor constructor, String constructorName, IFitnessCalculator calculator) throws Exception
	{
		double sum = 0.0;
		double maxFitness = 0;
		
		for (int i = 0; i < CYCLES; i++)
		{
			SearchAlgorithm searchAlgorithm = Algorithm.createAlgorithm(algorithm, null, project, constructor);
			boolean[] solution = searchAlgorithm.execute(calculator);

			Solution sol = new Solution(project);
			sol.setAllCustomers(solution);
			double fitness = calculator.evaluate(sol);
			
			String s = algorithm.name() + "-" + constructorName + "," + project.getName() + "," + i + "," + budgetFactor + "," + riskImportance;
			s += "," + fitness + "," + Solution.printSolution(solution);
			out.println(s);
			
			sum += fitness;
			if (fitness > maxFitness) maxFitness = fitness;
			System.out.print("*");
		}

		System.out.println(String.format(" %-6s\t%-14s\t%.4f\t%.4f", algorithm.name() + "-" + constructorName, project.getName() + "-" + budgetFactor, (sum/CYCLES), maxFitness));
	}

	/**
	 * Optimizes a given instance
	 */
	private void createReportForInstance(PrintWriter out, Project project, int[] budgetFactors) throws Exception
	{
		for (int budgetFactor : budgetFactors)
		{
			int availableBudget = (int) (project.getTotalCost() * (budgetFactor / 100.0));
			ProfitFitnessCalculator calculator = new ProfitFitnessCalculator(project, availableBudget);
			createReportForBudget(out, project, budgetFactor, 0, Algorithm.ILS, new GreedyConstructor(project), "GD", calculator);
			createReportForBudget(out, project, budgetFactor, 0, Algorithm.ILS, new RandomConstructor(project), "RS", calculator);
		}
	}
	
	/**
	 * Runs the optimization report for tunning the initial seed generator
	 */
	public void execute(List<Instance> instances, int[] budgetFactors, String outputFilename) throws Exception
	{
		PrintWriter out = createOutputFile(outputFilename);

		for (Instance instance : instances)
		{
			RequirementReader reader = new RequirementReader();
			Project project = reader.execute(instance);
			System.out.println("Processing " + project.getName() + " ...");
			createReportForInstance(out, project, budgetFactors);
		}
		
		out.close();
	}
}