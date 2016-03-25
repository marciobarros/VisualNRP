package sobol.problems.requirements.calc;

import java.io.FileWriter;
import java.io.PrintWriter;

import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.RandomConstructor;
import sobol.problems.requirements.model.Project;
import sobol.problems.requirements.reader.RequirementReader;

/**
 * Class that represents the landscape report
 * 
 * @author marciobarros
 */
public class LandscapeReport
{
	/**
	 * Number of solutions per costumer
	 */
	private static int SOLUTIONS_PER_CUSTOMER = 100;
	
	/**
	 * Calculates the fitness of a given solution
	 */
	private double evaluate(boolean[] solution, Project project, double availableBudget, int riskImportance, double totalProfit, double totalCost, double totalRisk) throws Exception
	{
		int cost = project.calculateCost(solution);
		
		if (cost > availableBudget)
			return -cost / totalCost;

		int profit = project.calculateProfit(solution);
		double alfa = riskImportance / 100.0;

		double risk = project.calculateCustomerRisk(solution);
		return (1 - alfa) * profit / totalProfit + alfa * risk / totalRisk;
		
//		double risk = project.calculateRequirementRisk(solution);
//		return (1 - alfa) * profit / totalProfit + alfa * risk / totalRisk;
	}
	
	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createLandscapeForBudget(PrintWriter out, Project project, Constructor constructor, int budgetFactor, int riskImportance, double totalProfit, double totalCost, double totalRisk) throws Exception
	{
		double availableBudget = project.getTotalCost() * (budgetFactor / 100.0);

		for (int i = 1; i <= project.getCustomerCount(); i++)
		{
			for (int j = 0; j < SOLUTIONS_PER_CUSTOMER; j++)
			{
				boolean[] solution = constructor.generateSolutionWith(i);
				double fitness = evaluate(solution, project, availableBudget, riskImportance, totalProfit, totalCost, totalRisk);
				out.println(budgetFactor + "," + riskImportance + "," + i + "," + fitness);
			}
		}
	}
	
	/**
	 * Creates the landscape report for a given instance
	 */
	private void createLandscape(String filename, Project project, Constructor constructor, int[] budgetFactors, int[] riskImportances) throws Exception
	{
		FileWriter outFile = new FileWriter(filename);
		PrintWriter out = new PrintWriter(outFile);
		
		out.println("budget,risk,cust,fit");
		double totalProfit = project.getTotalProfit();
		double totalCost = project.getTotalCost();
		double totalRisk = project.getTotalCustomerRisk();
//		double totalRisk = project.getTotalRequirementRisk();
		
		for (int budgetFactor : budgetFactors)
		{
			for (int riskImportance : riskImportances)
			{
				createLandscapeForBudget(out, project, constructor, budgetFactor, riskImportance, totalProfit, totalCost, totalRisk);
			}
		}
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Creates the landscape reports without risk for all instances
	 */
	public LandscapeReport execute(String[] instanceFilenames, int[] budgetFactors, int[] riskImportances) throws Exception
	{
		for (String filename : instanceFilenames)
		{
			RequirementReader reader = new RequirementReader(filename);
			Project project = reader.execute();
			System.out.println("Processando " + project.getName() + " ...");
			
			String landscapeFilename = "results/landscape/" + filename.substring(filename.lastIndexOf('/')+1);
			Constructor constructor = new RandomConstructor(project);
			createLandscape(landscapeFilename, project, constructor, budgetFactors, riskImportances);
		}
		
		return this;
	}
}