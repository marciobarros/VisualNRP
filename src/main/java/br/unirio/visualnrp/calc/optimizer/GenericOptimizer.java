package br.unirio.visualnrp.calc.optimizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.GreedyConstructor;
import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.algorithm.search.SearchAlgorithm;
import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.calc.fitness.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;

/**
 * Superclass for all optimizers
 * 
 * @author marciobarros
 */
public class GenericOptimizer
{
	/**
	 * Number of optimization cycles
	 */
	private static int CYCLES = 1;

	/**
	 * Algorithm configuration - all algorithms
	 */
	protected static final Algorithm[] ALGORITHM_ALL = { Algorithm.HC, Algorithm.ILS, Algorithm.VISILS };

	/**
	 * Algorithm configuration - HC only
	 */
	protected static final Algorithm[] ALGORITHM_HC = { Algorithm.HC };

	/**
	 * Algorithm configuration - ILS only
	 */
	protected static final Algorithm[] ALGORITHM_ILS = { Algorithm.ILS };

	/**
	 * Algorithm configuration - VISILS only
	 */
	protected static final Algorithm[] ALGORITHM_VISILS = { Algorithm.VISILS };

	/**
	 * Algorithm configuration - ILS and VISILS
	 */
	protected static final Algorithm[] ALGORITHM_ILS_VISILS = { Algorithm.ILS, Algorithm.VISILS };

	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	protected void createReportForBudget(PrintWriter out, Project project, int budgetFactor, int riskImportance, Algorithm algorithm, IFitnessCalculator calculator) throws Exception
	{
		Constructor constructor = new GreedyConstructor(project);

		double sum = 0.0;
		double maxFitness = 0;
		
		for (int i = 0; i < CYCLES; i++)
		{
			SearchAlgorithm searchAlgorithm = Algorithm.createAlgorithm(algorithm, null, project, constructor);
			boolean[] solution = searchAlgorithm.execute(calculator);

			Solution sol = new Solution(project);
			sol.setAllCustomers(solution);
			double fitness = calculator.evaluate(sol);
			
			String s = algorithm.name() + "," + project.getName() + "," + i + "," + budgetFactor + "," + riskImportance;
			s += "," + fitness + "," + Solution.printSolution(solution);
			out.println(s);
			
			sum += fitness;
			if (fitness > maxFitness) maxFitness = fitness;
			System.out.print("*");
		}

		System.out.println(String.format(" %-6s\t%-14s\t%.4f\t%.4f", algorithm.name(), project.getName() + "-" + budgetFactor, (sum/CYCLES), maxFitness));
	}
	
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
}