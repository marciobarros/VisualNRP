package br.unirio.visualnrp.calc;

import br.unirio.visualnrp.algorithm.solution.Solution;
import br.unirio.visualnrp.model.Project;


/**
 * Interface for the calculation of fitness for optimizers
 * 
 * @author marciobarros
 */
public interface IFitnessCalculator
{
	void prepare(Project project);
	
	double evaluate(Solution solution, double availableBudget, double riskImportance);
}

/**
 * Class that supports the calculation of fitness for profit
 * 
 * @author marciobarros
 */
class ProfitFitnessCalculator implements IFitnessCalculator
{
	public void prepare(Project project)
	{
	}
	
	public double evaluate(Solution solution, double availableBudget, double riskImportance)
	{
		int cost = solution.getCost();
		return (cost <= availableBudget) ? solution.getProfit() : -cost;
	}
}

/**
 * Class that supports the calculation of fitness for profit risk
 * 
 * @author marciobarros
 */
class ProfitRiskFitnessCalculator implements IFitnessCalculator
{
	private Project project;
	private double totalCost;
	private double totalProfit;
	private double totalRisk;
	
	public void prepare(Project project)
	{
		this.project = project;
		this.totalCost = project.getTotalCost();
		this.totalProfit = project.getTotalProfit();
		this.totalCost = project.getTotalCustomerRisk();
	}
	
	public double evaluate(Solution solution, double availableBudget, double riskImportance)
	{
		int cost = solution.getCost();
		
		if (cost > availableBudget)
			return -cost / totalCost;

		int profit = solution.getProfit();
		double risk = project.calculateCustomerRisk(solution.getSolution());

		double alfa = riskImportance / 100.0;
		return (1 - alfa) * profit / totalProfit + alfa * (totalRisk - risk) / totalRisk;
	}
}

/**
 * Class that supports the calculation of fitness for cost risk
 * 
 * @author marciobarros
 */
class CostRiskFitnessCalculator implements IFitnessCalculator
{
	private Project project;
	private double totalCost;
	private double totalProfit;
	private double totalRisk;
	
	public void prepare(Project project)
	{
		this.project = project;
		this.totalCost = project.getTotalCost();
		this.totalProfit = project.getTotalProfit();
		this.totalCost = project.getTotalRequirementRisk();
	}
	
	public double evaluate(Solution solution, double availableBudget, double riskImportance)
	{
		int cost = solution.getCost();
		
		if (cost > availableBudget)
			return -cost / totalCost;

		int profit = solution.getProfit();
		double risk = project.calculateRequirementRisk(solution.getSolution());

		double alfa = riskImportance / 100.0;
		return (1 - alfa) * profit / totalProfit + alfa * (totalRisk - risk) / totalRisk;
	}
}