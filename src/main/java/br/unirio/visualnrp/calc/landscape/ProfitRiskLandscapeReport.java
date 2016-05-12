package br.unirio.visualnrp.calc.landscape;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.RandomConstructor;
import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.calc.fitness.IFitnessCalculator;
import br.unirio.visualnrp.calc.fitness.ProfitRiskFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.MaximumValues;
import br.unirio.visualnrp.model.MaximumValuesList;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.MaximumValuesReader;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Class that represents the profit risk landscape report
 * 
 * @author marciobarros
 */
public class ProfitRiskLandscapeReport
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
	private void createLandscapeForInstance(Project project, Instance instance, int[] budgetFactors, int[] riskImportances, MaximumValuesList maximumValues, String outputFilename) throws Exception
	{
		Constructor constructor = new RandomConstructor(project);

		String landscapeFilename = String.format(outputFilename, project.getInstance().getName());
		File file = new File(landscapeFilename);
		file.getParentFile().mkdirs();
		
		FileWriter outFile = new FileWriter(landscapeFilename);
		PrintWriter out = new PrintWriter(outFile);
		out.println("budget,risk,cust,fit");
		
		for (int budgetFactor : budgetFactors)
		{
			double availableBudget = project.getTotalCost() * (budgetFactor / 100.0);
			MaximumValues values = maximumValues.getMaximumValues(instance, budgetFactor);
			
			if (values != null)
			{
				for (int riskImportance : riskImportances)
				{
					System.out.println("Processing " + project.getName() + " ...");
					IFitnessCalculator calculator = new ProfitRiskFitnessCalculator(project, availableBudget, riskImportance, values.getMaximumProfit(), values.getMaximumProfitRisk());
					createLandscapeForBudget(out, project, constructor, budgetFactor, riskImportance, calculator);
				}
			}
		}
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Creates the landscape report
	 */
	public void execute(List<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		MaximumValuesList maximumValues = new MaximumValuesReader().execute();
		
		for (int instanceIndex = 0; instanceIndex < instances.size(); instanceIndex++)
		{
			Instance instance = instances.get(instanceIndex);
			RequirementReader reader = new RequirementReader();
			Project project = reader.execute(instance);
			createLandscapeForInstance(project, instance, budgetFactors, riskImportances, maximumValues, outputFilename);
		}
	}
}