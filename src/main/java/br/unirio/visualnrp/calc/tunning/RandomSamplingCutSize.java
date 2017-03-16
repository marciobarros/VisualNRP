package br.unirio.visualnrp.calc.tunning;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.GreedyConstructor;
import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.algorithm.search.VisIteratedLocalSearch;
import br.unirio.visualnrp.calc.fitness.IFitnessCalculator;
import br.unirio.visualnrp.calc.fitness.ProfitFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Classe responsável por calcular o tamanho do corte da fase de random sampling
 * 
 * @author Marcio
 */
public class RandomSamplingCutSize
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
	protected void createReportForBudget(PrintWriter out, Project project, int budgetFactor, Constructor constructor, IFitnessCalculator calculator) throws Exception
	{
		double sum = 0.0;
		int maxCustomerCount = 0;
		
		for (int i = 0; i < CYCLES; i++)
		{
			VisIteratedLocalSearch visils = new VisIteratedLocalSearch(null, project, Algorithm.MAXEVALUATIONS, Algorithm.SAMPLE_SIZE, constructor);
			int customerCount = visils.executeRandomSampling(project, calculator).getCustomerCount();
			
			out.println(project.getName() + "," + i + "," + budgetFactor + "," + customerCount);
			
			sum += customerCount;
			if (customerCount > maxCustomerCount) maxCustomerCount = customerCount;
			System.out.print("*");
		}

		System.out.println(String.format(" %-14s\t%.4f\t%4d", project.getName() + "-" + budgetFactor, (sum/CYCLES), maxCustomerCount));
	}

	/**
	 * Runs a sequence of random samplings for a given instance
	 */
	private void createReportForInstance(PrintWriter out, Project project, int[] budgetFactors) throws Exception
	{
		for (int budgetFactor : budgetFactors)
		{
			int availableBudget = (int) (project.getTotalCost() * (budgetFactor / 100.0));
			ProfitFitnessCalculator calculator = new ProfitFitnessCalculator(project, availableBudget);
			createReportForBudget(out, project, budgetFactor, new GreedyConstructor(project), calculator);
		}
	}
	
	/**
	 * Runs a sequence of random samplings to measure their cut size
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