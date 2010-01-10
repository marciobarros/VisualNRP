package br.unirio.visualnrp.calc.fitness;

import br.unirio.visualnrp.algorithm.search.Solution;
import br.unirio.visualnrp.model.Project;

/**
 * Class that supports the calculation of fitness for profit risk
 * 
 * @author marciobarros
 */
public class ProfitRiskFitnessCalculator implements IFitnessCalculator
{
	private double totalCost;
	private double totalRisk;
	private double availableBudget;
	private double riskImportance;
	private double maximumProfit;
	
	public ProfitRiskFitnessCalculator(Project project, double availableBudget, int riskImportance, int maximumProfit)
	{
		this.totalCost = project.getTotalCost();
		this.totalRisk = project.getTotalProfitRisk();
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
		double risk = solution.getProfitRisk();
		return (1 - riskImportance) * profit / maximumProfit + riskImportance * (totalRisk - risk) / totalRisk;
	}
}