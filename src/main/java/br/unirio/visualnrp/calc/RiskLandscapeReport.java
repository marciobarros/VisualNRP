package br.unirio.visualnrp.calc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.RandomConstructor;
import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.instance.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Class that represents the landscape report
 * 
 * @author marciobarros
 */
public class RiskLandscapeReport
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
		double availableBudget = project.getTotalCost() * (budgetFactor / 100.0);
		Solution sSolution = new Solution(project);

		for (int i = 1; i <= project.getCustomerCount(); i++)
		{
			for (int j = 0; j < SOLUTIONS_PER_CUSTOMER; j++)
			{
				boolean[] solution = constructor.generateSolutionWith(i);
				sSolution.setAllCustomers(solution);
				double fitness = calculator.evaluate(sSolution, availableBudget, riskImportance);
				out.println(budgetFactor + "," + riskImportance + "," + i + "," + fitness);
			}
		}
	}
	
	/**
	 * Creates the landscape report for a given instance
	 */
	private void createLandscape(String filename, Project project, Constructor constructor, int[] budgetFactors, int[] riskImportances, IFitnessCalculator calculator) throws Exception
	{
		File file = new File(filename);
		file.getParentFile().mkdirs();
		
		FileWriter outFile = new FileWriter(filename);
		PrintWriter out = new PrintWriter(outFile);
		out.println("budget,risk,cust,fit");
		
		for (int budgetFactor : budgetFactors)
		{
			for (int riskImportance : riskImportances)
			{
				createLandscapeForBudget(out, project, constructor, budgetFactor, riskImportance, calculator);
			}
		}
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Creates the risk landscape reports for all instances
	 */
	private void execute(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename, IFitnessCalculator calculator) throws Exception
	{
		for (Instance instance : instances)
		{
			RequirementReader reader = new RequirementReader(instance.getFilename());
			Project project = reader.execute();
			
			System.out.println("Processing " + project.getName() + " ...");
			calculator.prepare(project);
			
			Constructor constructor = new RandomConstructor(project);
			String landscapeFilename = String.format(outputFilename, instance.getName());
			createLandscape(landscapeFilename, project, constructor, budgetFactors, riskImportances, calculator);
		}
	}
	
	/**
	 * Creates the landscape report for the profit risk
	 */
	public void executeProfitRisk(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		execute(instances, budgetFactors, riskImportances, outputFilename, new ProfitRiskFitnessCalculator());
	}
	
	/**
	 * Creates the landscape report for the cost risk
	 */
	public void executeCostRisk(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		execute(instances, budgetFactors, riskImportances, outputFilename, new CostRiskFitnessCalculator());
	}
}