package br.unirio.visualnrp.calc.fitness;

import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.model.Project;

/**
 * Class that supports the calculation of fitness for profit risk only (maximization)
 * 
 * @author marciobarros
 */
public class ProfitRiskOnlyFitnessCalculator implements IFitnessCalculator
{
	private double availableBudget;
	
	public ProfitRiskOnlyFitnessCalculator(Project project, double availableBudget)
	{
		this.availableBudget = availableBudget;
	}
	
	public double evaluate(Solution solution)
	{
		int cost = solution.getCost();
		
		if (cost > availableBudget)
			return -cost;

		return solution.getProfitRisk();
	}
}