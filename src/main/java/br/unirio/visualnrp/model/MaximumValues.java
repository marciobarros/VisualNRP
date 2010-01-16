package br.unirio.visualnrp.model;

/**
 * Class that represents a registry of maximum values for the instances
 * 
 * @author marciobarros
 */
public class MaximumValues
{
	private Instance instance;
	private int budget;
	private int maximumProfit;
	private double maximumCostRisk;
	private double maximumProfitRisk;
	
	/**
	 * Initializes a registry of maximum values
	 */
	public MaximumValues(Instance instance, int budget, int maximumProfit, double maximumCostRisk, double maximumProfitRisk)
	{
		this.instance = instance;
		this.budget = budget;
		this.maximumProfit = maximumProfit;
		this.maximumCostRisk = maximumCostRisk;
		this.maximumProfitRisk = maximumProfitRisk;
	}
	
	/**
	 * Returns the instance
	 */
	public Instance getInstance()
	{
		return instance;
	}
	
	/**
	 * Returns the budget
	 */
	public int getBudget()
	{
		return budget;
	}
	
	/**
	 * Returns the maximum profit
	 */
	public int getMaximumProfit()
	{
		return maximumProfit;
	}
	
	/**
	 * Returns the maximum cost risk
	 */
	public double getMaximumCostRisk()
	{
		return maximumCostRisk;
	}
	
	/**
	 * Returns the maximum profit risk
	 */
	public double getMaximumProfitRisk()
	{
		return maximumProfitRisk;
	}
}