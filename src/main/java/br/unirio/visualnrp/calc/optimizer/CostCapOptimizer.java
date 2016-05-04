package br.unirio.visualnrp.calc.optimizer;

import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.calc.fitness.CostCapFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Class that performs the cost cap based optimization
 * 
 * @author Marcio
 */
public class CostCapOptimizer extends GenericOptimizer
{
	/**
	 * Creates the optimization report for a given instance
	 */
	private void createReportForInstance(PrintWriter out, Project project, int[] budgetFactors, int[] riskImportances, Algorithm[] algorithms) throws Exception
	{
		for (int i = 0; i < budgetFactors.length; i++)
		{
			for (int riskImportance : riskImportances)
			{
				int budgetFactor = budgetFactors[i];
				int availableBudget = (int) (project.getTotalCost() * (budgetFactor / 100.0));
				CostCapFitnessCalculator calculator = new CostCapFitnessCalculator(project, availableBudget, riskImportance);
				
				for (Algorithm algorithm : algorithms)
				{
					createReportForBudget(out, project, budgetFactor, riskImportance, algorithm, calculator);
				}
			}
		}
	}
	
	/**
	 * Runs the optimization report for cost risk
	 */
	public void execute(List<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		PrintWriter out = createOutputFile(outputFilename);

		for (Instance instance : instances)
		{
			RequirementReader reader = new RequirementReader();
			Project project = reader.execute(instance);
			System.out.println("Processing " + project.getName() + " ...");
			createReportForInstance(out, project, budgetFactors, riskImportances, ALGORITHM_ILS_VISILS);
		}
		
		out.close();
	}
}