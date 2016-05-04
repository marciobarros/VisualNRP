package br.unirio.visualnrp.calc.optimizer;

import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.calc.fitness.ProfitFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Classe responsável pela otimização do modelo baseado em lucro
 * 
 * @author Marcio
 */
public class ProfitOptimizer extends GenericOptimizer
{
	/**
	 * Optimizes a given instance
	 */
	private void createReportForInstance(PrintWriter out, Project project, int[] budgetFactors, Algorithm[] algorithms) throws Exception
	{
		for (int budgetFactor : budgetFactors)
		{
			int availableBudget = (int) (project.getTotalCost() * (budgetFactor / 100.0));
			ProfitFitnessCalculator calculator = new ProfitFitnessCalculator(project, availableBudget);
			
			for (Algorithm algorithm : algorithms)
			{
				createReportForBudget(out, project, budgetFactor, 0, algorithm, calculator);
			}
		}
	}
	
	/**
	 * Runs the optimization report for profit
	 */
	public void execute(List<Instance> instances, int[] budgetFactors, String outputFilename) throws Exception
	{
		PrintWriter out = createOutputFile(outputFilename);

		for (Instance instance : instances)
		{
			RequirementReader reader = new RequirementReader();
			Project project = reader.execute(instance);
			System.out.println("Processing " + project.getName() + " ...");
			createReportForInstance(out, project, budgetFactors, ALGORITHM_ALL);
		}
		
		out.close();
	}
}