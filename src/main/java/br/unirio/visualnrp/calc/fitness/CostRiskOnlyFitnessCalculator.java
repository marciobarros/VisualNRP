package br.unirio.visualnrp.calc.fitness;

import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.model.Project;

/**
 * Class that supports the calculation of fitness for cost risk only (maximization)
 * 
 * @author marciobarros
 */
public class CostRiskOnlyFitnessCalculator implements IFitnessCalculator
{
	private double availableBudget;
	
	public CostRiskOnlyFitnessCalculator(Project project, double availableBudget)
	{
		this.availableBudget = availableBudget;
	}
	
	public double evaluate(Solution solution)
	{
		int cost = solution.getCost();
		
		if (cost > availableBudget)
			return -cost;

		return solution.getCostRisk();
	}
}