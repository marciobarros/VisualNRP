package br.unirio.visualnrp.calc.optimizer;

import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.calc.fitness.ProfitRiskFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Class that performs the profit risk based optimization
 * 
 * @author Marcio
 */
public class ProfitRiskOptimizer extends GenericOptimizer
{
	/**
	 * Creates the optimization report for a given instance
	 */
	private void createReportForInstance(PrintWriter out, Project project, int[] budgetFactors, int[] riskImportances, int[] maximumProfits, Algorithm[] algorithms) throws Exception
	{
		for (int i = 0; i < budgetFactors.length; i++)
		{
			for (int riskImportance : riskImportances)
			{
				int budgetFactor = budgetFactors[i];
				int availableBudget = (int) (project.getTotalCost() * (budgetFactor / 100.0));
				ProfitRiskFitnessCalculator calculator = new ProfitRiskFitnessCalculator(project, availableBudget, riskImportance, maximumProfits[i]);
				
				for (Algorithm algorithm : algorithms)
				{
					createReportForBudget(out, project, budgetFactor, riskImportance, algorithm, calculator);
				}
			}
		}
	}
	
	/**
	 * Runs the optimization report
	 */
	public void execute(List<Instance> instances, int[] budgetFactors, int[] riskImportances, int[][] maximumProfits, String outputFilename) throws Exception
	{
		PrintWriter out = createOutputFile(outputFilename);

		for (int i = 0; i < instances.size(); i++)
		{
			Instance instance = instances.get(i);
			RequirementReader reader = new RequirementReader();
			Project project = reader.execute(instance);
			System.out.println("Processing " + project.getName() + " ...");
			createReportForInstance(out, project, budgetFactors, riskImportances, maximumProfits[i], ALGORITHM_ILS_VISILS);
		}
		
		out.close();
	}
}