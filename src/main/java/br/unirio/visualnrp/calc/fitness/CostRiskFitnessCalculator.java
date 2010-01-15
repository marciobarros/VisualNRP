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
	private double availableBudget;
	private double riskImportance;
	private double maximumProfit;
	private double maximumRisk;
	
	public CostRiskFitnessCalculator(Project project, double availableBudget, int riskImportance, int maximumProfit, double maximumRisk)
	{
		this.totalCost = project.getTotalCost();
		this.availableBudget = availableBudget;
		this.riskImportance = riskImportance / 100.0;
		this.maximumProfit = maximumProfit;
		this.maximumRisk = maximumRisk;
	}
	
	public double evaluate(Solution solution)
	{
		int cost = solution.getCost();
		
		if (cost > availableBudget)
			return -cost / totalCost;

		int profit = solution.getProfit();
		double profitFactor = ((double)profit) / maximumProfit;
		
		double risk = solution.getCostRisk();
		double riskFactor = (maximumRisk - risk) / maximumRisk;
		
		if (riskFactor < 0.0)
			System.out.println("RiskFactor < 0.0 ...... " + risk);
				
		if (riskFactor > 1.0)
			System.out.println("RiskFactor > 1.0 ...... " + risk);
				
		return (1 - riskImportance) * profitFactor + riskImportance * riskFactor;
	}
}