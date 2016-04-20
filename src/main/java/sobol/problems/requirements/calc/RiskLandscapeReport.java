package sobol.problems.requirements.calc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.RandomConstructor;
import sobol.problems.requirements.instance.Instance;
import sobol.problems.requirements.model.Project;
import sobol.problems.requirements.reader.RequirementReader;

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
	 * Calculates the fitness of a given solution
	 */
	private double evaluate(boolean[] solution, Project project, double availableBudget, int riskImportance, double totalProfit, double totalCost, double totalRisk, IRiskLanscapeCalculator calculator) throws Exception
	{
		int cost = project.calculateCost(solution);
		
		if (cost > availableBudget)
			return -cost / totalCost;

		int profit = project.calculateProfit(solution);
		double risk = calculator.calculateRisk(project, solution);

		double alfa = riskImportance / 100.0;
		return (1 - alfa) * profit / totalProfit + alfa * (totalRisk - risk) / totalRisk;
	}
	
	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createLandscapeForBudget(PrintWriter out, Project project, Constructor constructor, int budgetFactor, int riskImportance, double totalProfit, double totalCost, double totalRisk, IRiskLanscapeCalculator calculator) throws Exception
	{
		double availableBudget = project.getTotalCost() * (budgetFactor / 100.0);

		for (int i = 1; i <= project.getCustomerCount(); i++)
		{
			for (int j = 0; j < SOLUTIONS_PER_CUSTOMER; j++)
			{
				boolean[] solution = constructor.generateSolutionWith(i);
				double fitness = evaluate(solution, project, availableBudget, riskImportance, totalProfit, totalCost, totalRisk, calculator);
				out.println(budgetFactor + "," + riskImportance + "," + i + "," + fitness);
			}
		}
	}
	
	/**
	 * Creates the landscape report for a given instance
	 */
	private void createLandscape(String filename, Project project, Constructor constructor, int[] budgetFactors, int[] riskImportances, IRiskLanscapeCalculator calculator) throws Exception
	{
		File file = new File(filename);
		file.getParentFile().mkdirs();
		
		FileWriter outFile = new FileWriter(filename);
		PrintWriter out = new PrintWriter(outFile);
		
		out.println("budget,risk,cust,fit");
		double totalProfit = project.getTotalProfit();
		double totalCost = project.getTotalCost();
		double totalRisk = calculator.calculateTotalRisk(project);
		
		for (int budgetFactor : budgetFactors)
		{
			for (int riskImportance : riskImportances)
			{
				createLandscapeForBudget(out, project, constructor, budgetFactor, riskImportance, totalProfit, totalCost, totalRisk, calculator);
			}
		}
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Creates the risk landscape reports for all instances
	 */
	private void execute(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename, IRiskLanscapeCalculator calculator) throws Exception
	{
		for (Instance instance : instances)
		{
			RequirementReader reader = new RequirementReader(instance.getFilename());
			Project project = reader.execute();
			System.out.println("Processing " + project.getName() + " ...");
			
			//String landscapeFilename = "results/landscape/" + filename.substring(filename.lastIndexOf('/')+1);
			String landscapeFilename = String.format(outputFilename, instance.getName());
			
			Constructor constructor = new RandomConstructor(project);
			createLandscape(landscapeFilename, project, constructor, budgetFactors, riskImportances, calculator);
		}
	}
	
	/**
	 * Creates the landscape report for the profit risk
	 */
	public void executeProfitRisk(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		execute(instances, budgetFactors, riskImportances, outputFilename, new ProfitRiskLanscapeCalculator());
	}
	
	/**
	 * Creates the landscape report for the cost risk
	 */
	public void executeCostRisk(Iterable<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		execute(instances, budgetFactors, riskImportances, outputFilename, new CostRiskLanscapeCalculator());
	}
}

/**
 * Interface for the calculation of fitness landscapes
 * 
 * @author marciobarros
 */
interface IRiskLanscapeCalculator
{
	double calculateTotalRisk(Project project);

	double calculateRisk(Project project, boolean[] solution);
}

/**
 * Class that supports the calculation of the fitness landscape for profit risk
 * 
 * @author marciobarros
 */
class ProfitRiskLanscapeCalculator implements IRiskLanscapeCalculator
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
class CostRiskLanscapeCalculator implements IRiskLanscapeCalculator
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