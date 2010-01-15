package br.unirio.visualnrp.calc.fitness;

import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.model.Project;

/**
 * Class that supports the calculation of fitness for cost risk
 * 
 * @author marciobarros
 */
public class CostRiskFitnessCalculator implements IFitnessCalculator
{
	private double totalCost;
	private double totalRisk;
	private double availableBudget;
	private double riskImportance;
	private double maximumProfit;
	
	public CostRiskFitnessCalculator(Project project, double availableBudget, int riskImportance, int maximumProfit, double maximumRisk)
	{
		this.totalCost = project.getTotalCost();
		this.totalRisk = project.getTotalCostRisk();
		this.totalRisk = maximumRisk;
		this.availableBudget = availableBudget;
		this.riskImportance = riskImportance / 100.0;
		this.maximumProfit = maximumProfit;
	}
	
	public double evaluate(Solution solution)
	{
		int cost = solution.getCost();
		
		if (cost > availableBudget)
			return -cost / totalCost;

		int profit = solution.getProfit();
		double risk = solution.getCostRisk();
		return (1 - riskImportance) * profit / maximumProfit + riskImportance * (totalRisk - risk) / totalRisk;
	}
}