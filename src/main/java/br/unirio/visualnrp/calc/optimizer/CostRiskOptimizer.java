package br.unirio.visualnrp.calc.optimizer;

import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.calc.fitness.CostRiskFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.MaximumValues;
import br.unirio.visualnrp.model.MaximumValuesList;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.MaximumValuesReader;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Class that performs the cost risk based optimization
 * 
 * @author Marcio
 */
public class CostRiskOptimizer extends GenericOptimizer
{
	/**
	 * Creates the optimization report for a given instance
	 */
	private void createReportForInstance(PrintWriter out, Project project, Instance instance, int[] budgetFactors, int[] riskImportances, MaximumValuesList maximumValues, Algorithm[] algorithms) throws Exception
	{
		for (int budgetFactor : budgetFactors)
		{
			int availableBudget = (int) (project.getTotalCost() * (budgetFactor / 100.0));
			MaximumValues values = maximumValues.getMaximumValues(instance, budgetFactor);
			
			if (values != null)
			{
				for (int riskImportance : riskImportances)
				{
					CostRiskFitnessCalculator calculator = new CostRiskFitnessCalculator(project, availableBudget, riskImportance, values.getMaximumProfit(), values.getMaximumCostRisk());
					
					for (Algorithm algorithm : algorithms)
					{
						createReportForBudget(out, project, budgetFactor, riskImportance, algorithm, calculator);
					}
				}
			}
		}
	}
	
	/**
	 * Runs the optimization report for cost risk
	 */
	public void execute(List<Instance> instances, int[] budgetFactors, int[] riskImportances, String outputFilename) throws Exception
	{
		MaximumValuesList maximumValues = new MaximumValuesReader().execute();
		
		PrintWriter out = createOutputFile(outputFilename);

		for (int i = 0; i < instances.size(); i++)
		{
			Instance instance = instances.get(i);
			RequirementReader reader = new RequirementReader();
			Project project = reader.execute(instance);
			System.out.println("Processing " + project.getName() + " ...");
			createReportForInstance(out, project, instance, budgetFactors, riskImportances, maximumValues, ALGORITHM_ILS_VISILS);
		}
		
		out.close();
	}
}