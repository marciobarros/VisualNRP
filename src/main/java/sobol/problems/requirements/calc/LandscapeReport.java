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
	private static int CYCLES = 100;
	
	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createLandscapeForBudget(PrintWriter out, Project project, Constructor constructor, int budgetFactor) throws Exception
	{
		double availableBudget = project.getTotalCost() * (budgetFactor / 100.0);

		for (int i = 1; i <= project.getCustomerCount(); i++)
		{
			for (int j = 0; j < CYCLES; j++)
			{
				boolean[] solution = constructor.generateSolutionWith(i);
				int cost = project.calculateCost(solution);
				int profit = project.calculateProfit(solution);
				int fitness = (cost <= availableBudget) ? profit : -cost;
				out.println(budgetFactor + "," + i + "," + fitness);
			}
		}
	}
	
	/**
	 * Creates the landscape report for a given instance
	 */
	private void createLandscape(String filename, Project project, Constructor constructor, double[] budgetFactors) throws Exception
	{
		FileWriter outFile = new FileWriter(filename);
		PrintWriter out = new PrintWriter(outFile);
		
		out.println("budget,cust,fit");
		
		for (double budgetFactor : budgetFactors)
			createLandscapeForBudget(out, project, constructor, (int)(budgetFactor * 100));
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Creates the landscape reports for all instances
	 */
	public LandscapeReport execute(String[] instanceFilenames, double[] budgetFactors) throws Exception
	{
		for (String filename : instanceFilenames)
		{
			RequirementReader reader = new RequirementReader(filename);
			Project project = reader.execute();
			System.out.println("Processando " + project.getName() + " ...");
			
			String landscapeFilename = "results/landscape/" + filename.substring(filename.lastIndexOf('/')+1);
			Constructor constructor = new RandomConstructor(project);
			createLandscape(landscapeFilename, project, constructor, budgetFactors);
		}
		
		return this;
	}
}