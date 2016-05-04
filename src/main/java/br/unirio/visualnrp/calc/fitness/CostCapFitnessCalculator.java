package br.unirio.visualnrp.calc.fitness;

import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.model.Project;

/**
 * Class that supports the calculation of fitness for cost cap
 * 
 * @author marciobarros
 */
public class CostCapFitnessCalculator implements IFitnessCalculator
{
	private double availableBudget;
	private double riskImportance;
	
	// TODO calcular ratio medio e maximo
	
	public CostCapFitnessCalculator(Project project, double availableBudget, int riskImportance)
	{
		this.availableBudget = availableBudget;
		this.riskImportance = riskImportance / 100.0;
	}
	
	public double evaluate(Solution solution)
	{
		int cost = solution.getCost();
		
		if (cost > availableBudget)
			return -cost;

		double worstCost = solution.getWorstCost();
		double ratio = (worstCost - cost) / cost;

		if (ratio > riskImportance)
			return -cost;
		
		return solution.getProfit();
	}
}