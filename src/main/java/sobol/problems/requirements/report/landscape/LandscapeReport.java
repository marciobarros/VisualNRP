package sobol.problems.requirements.report.landscape;

import java.io.FileWriter;
import java.io.PrintWriter;

import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.GreedyConstructor;
import sobol.problems.requirements.algorithm.solution.Solution;
import sobol.problems.requirements.model.Project;
import sobol.problems.requirements.reader.RequirementReader;

/**
 * Class that represents the landscape report
 * 
 * @author marciobarros
 */
public class LandscapeReport
{
	private static int CYCLES = 100;
	
	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createLandscapeForBudget(PrintWriter out, Project project, Constructor constructor, int budgetFactor) throws Exception
	{
		double availableBudget = project.getTotalCost() * budgetFactor / 100.0;

		for (int i = 1; i <= project.getCustomerCount(); i++)
		{
			for (int j = 0; j < CYCLES; j++)
			{
				boolean[] solution = constructor.generateSolutionWith(i);
				Solution sol = new Solution(project);
//				int cost = project.calculateCost(solution);
//				int profit = project.calculateProfit(solution);
				sol.setAllCustomers(solution);
				int cost = sol.getCost();
				int profit = sol.getProfit();
				int fitness = (cost <= availableBudget) ? profit : -cost;
				out.println(budgetFactor + "," + i + "," + fitness);
			}
		}
	}
	
	/**
	 * Creates the landscape report for a given instance
	 */
	private void createLandscape(String filename, Project project, Constructor constructor) throws Exception
	{
		FileWriter outFile = new FileWriter(filename);
		PrintWriter out = new PrintWriter(outFile);
		
		out.println("budget,cust,fit");
		createLandscapeForBudget(out, project, constructor, 30);
		createLandscapeForBudget(out, project, constructor, 50);
		createLandscapeForBudget(out, project, constructor, 70);
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Creates the landscape reports for all instances
	 */
	public void createLandscapes(String[] instanceFilenames) throws Exception
	{
		for (String filename : instanceFilenames)
		{
			RequirementReader reader = new RequirementReader(filename);
			Project project = reader.execute();
			System.out.println("Processando " + project.getName() + " ...");
			
			String landscapeFilename = "results/landscape/" + filename.substring(filename.lastIndexOf('/')+1);
			Constructor constructor = new GreedyConstructor(project);
			createLandscape(landscapeFilename, project, constructor);
		}
	}
}