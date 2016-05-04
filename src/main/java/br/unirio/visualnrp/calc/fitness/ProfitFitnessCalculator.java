package br.unirio.visualnrp.calc.fitness;

import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.model.Project;

/**
 * Class that supports the calculation of fitness for profit
 * 
 * @author marciobarros
 */
public class ProfitFitnessCalculator implements IFitnessCalculator
{
	private double availableBudget;

	public ProfitFitnessCalculator(Project project, double availableBudget)
	{
		this.availableBudget = availableBudget;
	}
	
	public double evaluate(Solution solution)
	{
		int cost = solution.getCost();
		return (cost <= availableBudget) ? solution.getProfit() : -cost;
	}
}