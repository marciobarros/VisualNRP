package br.unirio.visualnrp.calc.landscape;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.RandomConstructor;
import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.calc.fitness.CostRiskFitnessCalculator;
import br.unirio.visualnrp.calc.fitness.IFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Class that represents the landscape report
 * 
 * @author marciobarros
 */
public class CostRiskLandscapeReport
{
	/**
	 * Number of solutions per costumer
	 */
	private static int SOLUTIONS_PER_CUSTOMER = 100;
	
	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createLandscapeForBudget(PrintWriter out, Project project, Constructor constructor, int budgetFactor, int riskImportance, IFitnessCalculator calculator) throws Exception
	{
		Solution sSolution = new Solution(project);

		for (int i = 1; i <= project.getCustomerCount(); i++)
		{
			for (int j = 0; j < SOLUTIONS_PER_CUSTOMER; j++)
			{
				boolean[] solution = constructor.generateSolutionWith(i);
				sSolution.setAllCustomers(solution);
				double fitness = calculator.evaluate(sSolution);
				out.println(budgetFactor + "," + riskImportance + "," + i + "," + fitness);
			}
		}
	}

	/**
	 * Creates the landscape report for a given instance
	 */
	private void createLandscapeForInstance(Project project, int[] budgetFactors, int[] riskImportances, int[] maximumProfits, double[] maximumRisks, String outputFilename) throws Exception
	{
		Constructor constructor = new RandomConstructor(project);

		String landscapeFilename = String.format(outputFilename, project.getInstance().getName());
		File file = new File(landscapeFilename);
		file.getParentFile().mkdirs();
		
		FileWriter outFile = new FileWriter(landscapeFilename);
		PrintWriter out = new PrintWriter(outFile);
		out.println("budget,risk,cust,fit");
		
		for (int budgetIndex = 0; budgetIndex < budgetFactors.length; budgetIndex++)
		{
			int budgetFactor = budgetFactors[budgetIndex];
			double availableBudget = project.getTotalCost() * (budgetFactor / 100.0);

			int maximumProfit = maximumProfits[budgetIndex];
			double maximumRisk = maximumRisks[budgetIndex]; 
			
			for (int riskImportance : riskImportances)
			{
				System.out.println("Processing " + project.getName() + " ...");
				IFitnessCalculator calculator = new CostRiskFitnessCalculator(project, availableBudget, riskImportance, maximumProfit, maximumRisk);
				createLandscapeForBudget(out, project, constructor, budgetFactor, riskImportance, calculator);
			}
		}
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Creates the landscape report
	 */
	public void execute(List<Instance> instances, int[] budgetFactors, int[] riskImportances, int[][] maximumProfits, double[][] maximumRisks, String outputFilename) throws Exception
	{
		for (int instanceIndex = 0; instanceIndex < instances.size(); instanceIndex++)
		{
			Instance instance = instances.get(instanceIndex);
			RequirementReader reader = new RequirementReader();
			Project project = reader.execute(instance);
			createLandscapeForInstance(project, budgetFactors, riskImportances, maximumProfits[instanceIndex], maximumRisks[instanceIndex], outputFilename);
		}
	}
}