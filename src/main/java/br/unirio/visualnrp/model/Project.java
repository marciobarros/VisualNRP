package br.unirio.visualnrp.model;

/**
 * Class that represents the next-release problem
 * 
 * @author Marcio
 */
public class Project
{
	private Instance instance;
	private int[][] requirementDependencySources;
	private int[][] customerRequirements;
	private int[] requirementCosts;
	private double[] requirementCostMinimumEstimate;
	private double[] requirementCostMaximumEstimate;
	private double[] requirementCostRisks;
	private int[] customerProfits;
	private double[] customerProfitMinimumEstimate;
	private double[] customerProfitMaximumEstimate;
	private double[] customerProfitRisks;
	
	/**
	 * Creates a project, given its name
	 */
	public Project(Instance instance)
	{
		this.instance = instance;
		this.requirementDependencySources = null;
		this.customerRequirements = null;
		
		this.requirementCosts = null;
		this.requirementCostMinimumEstimate = null;
		this.requirementCostMaximumEstimate = null;
		this.requirementCostRisks = null;

		this.customerProfits = null;
		this.customerProfitMinimumEstimate = null;
		this.customerProfitMaximumEstimate = null;
		this.customerProfitRisks = null;
	}
	
	/**
	 * Returns the instance of the project
	 */
	public Instance getInstance()
	{
		return instance;
	}
	
	/**
	 * Returns the name of the project
	 */
	public String getName()
	{
		return instance.getName();
	}
	
	/**
	 * Returns the number of requirements
	 */
	public int getRequirementCount()
	{
		if (requirementCosts == null)
			return 0;
		
		return requirementCosts.length;
	}
	
	/**
	 * Returns the total cost of the set of requirements
	 */
	public int getTotalCost()
	{
		int sum = 0;
		
		for (int i = 0; i < requirementCosts.length; i++)
			sum += requirementCosts[i];
		
		return sum;
	}
	
	/**
	 * Returns the total requirement related risk attained by the set of requirements
	 */
	public double getTotalCostRisk()
	{
		double sum = 0;
		
		for (int i = 0; i < requirementCostRisks.length; i++)
			sum += requirementCostRisks[i];
		
		return sum;
	}

	/**
	 * Adds a number of requirements to the project
	 */
	public void addRequirements(int count)
	{
		if (requirementCosts == null)
		{
			requirementCosts = new int[count];
			requirementCostRisks = new double[count];
			requirementCostMinimumEstimate = new double[count];
			requirementCostMaximumEstimate = new double[count];
			requirementDependencySources = new int[count][];
			return;
		}
		
		int len = requirementCosts.length;
		int[] newCosts = new int[len + count];
		double[] newRequirementRisk = new double[len + count];
		double[] newRequirementCostMinimumEstimate = new double[len + count];
		double[] newRequirementCostMaximumEstimate = new double[len + count];
		int[][] newDependencySources = new int[len + count][];
		
		for (int i = 0; i < len; i++)
		{
			newCosts[i] = requirementCosts[i];
			newRequirementRisk[i] = requirementCostRisks[i];
			newRequirementCostMinimumEstimate[i] = requirementCostMinimumEstimate[i];
			newRequirementCostMaximumEstimate[i] = requirementCostMaximumEstimate[i];
			newDependencySources[i] = requirementDependencySources[i];
		}
		
		this.requirementCosts = newCosts;
		this.requirementCostRisks = newRequirementRisk;
		this.requirementCostMinimumEstimate = newRequirementCostMinimumEstimate;
		this.requirementCostMaximumEstimate = newRequirementCostMaximumEstimate;
		this.requirementDependencySources = newDependencySources;
	}

	/**
	 * Returns the cost of a given requirement
	 */
	public int getRequirementCost(int requirement)
	{
		return requirementCosts[requirement];
	}

	/**
	 * Returns the worst-case cost of a given requirement
	 */
	public double getRequirementWorstCost(int requirement)
	{
		return requirementCostMaximumEstimate[requirement];
	}

	/**
	 * Returns the cost-related risk of a given requirement
	 */
	public double getRequirementCostRisk(int requirement)
	{
		return requirementCostRisks[requirement];
	}

	/**
	 * Sets the cost for a given requirement
	 */
	public void setRequirementCost(int requirement, int cost)
	{
		requirementCosts[requirement] = cost;
	}
	
	/**
	 * Count the number of precedent requirements for a given requirement
	 */
	public int getRequirementDependencySourcesCount(int requirement)
	{
		int[] sources = requirementDependencySources[requirement];
		
		if (sources == null)
			return 0;

		int count = 0;
		
		for (int i = 0; sources[i] >= 0; i++)
			count++;
		
		return count;
	}

	/**
	 * Returns the precedent requirements for a given requirement
	 */
	public int[] getRequirementDependencySources(int requirement)
	{
		return requirementDependencySources[requirement];
	}

	/**
	 * Adds a dependency among a precedent (source) and a dependent (target) requirement
	 */
	public void addRequirementDependency(int source, int target)
	{
		int[] sources = requirementDependencySources[target];
		
		if (sources == null)
		{
			requirementDependencySources[target] = new int[10];
			
			for (int i = 1; i < 10; i++)
				requirementDependencySources[target][i] = -1;
			
			requirementDependencySources[target][0] = source;
			return;
		}
		
		for (int i = 0; i < sources.length-1; i++)
		{
			if (sources[i] < 0)
			{
				sources[i] = source;
				return;
			}

			if (sources[i] == source)
				return;
		}
		
		int[] newSources = new int[sources.length + 10];
		
		for (int i = 0; i < sources.length; i++)
			newSources[i] = sources[i];
		
		for (int i = 0; i < 10; i++)
			newSources[sources.length + i] = -1;
		
		newSources[sources.length-1] = source;
		requirementDependencySources[target] = newSources;	
	}

	/**
	 * Returns the number of customers for the project
	 */
	public int getCustomerCount()
	{
		return customerProfits.length;
	}

	/**
	 * Sets the number of costumers for the project
	 */
	public void setCustomerCount(int count)
	{
		this.customerProfits = new int[count];
		this.customerRequirements = new int[count][];
		this.customerProfitRisks = new double[count];
		this.customerProfitMinimumEstimate = new double[count];
		this.customerProfitMaximumEstimate = new double[count];
	}
	
	/**
	 * Returns the total profit to be yielded by the set of requirements
	 */
	public int getTotalProfit()
	{
		int sum = 0;
		
		for (int i = 0; i < customerProfits.length; i++)
			sum += customerProfits[i];
		
		return sum;
	}
	
	/**
	 * Returns the total customer related risk attained by the set of requirements
	 */
	public double getTotalProfitRisk()
	{
		double sum = 0;
		
		for (int i = 0; i < customerProfitRisks.length; i++)
			sum += customerProfitRisks[i];
		
		return sum;
	}

	/**
	 * Returns the profit for a given customer
	 */
	public int getCustomerProfit(int customer)
	{
		return customerProfits[customer];
	}

	/**
	 * Returns the profit-related risk for a given customer
	 */
	public double getCustomerProfitRisk(int customer)
	{
		return customerProfitRisks[customer];
	}

	/**
	 * Sets the profit for a given customer
	 */
	public void setCustomerProfit(int customer, int profit)
	{
		this.customerProfits[customer] = profit;
	}

	/**
	 * Gets the requirements requested by a given customer
	 */
	public int[] getCustomerRequirements(int customer)
	{
		return customerRequirements[customer];
	}

	/**
	 * Sets the requirements desired by a given customer
	 */
	public void setCustomerRequirements(int customer, int[] requirements)
	{
		this.customerRequirements[customer] = requirements;
	}
	
	/**
	 * Gets all requirements desired by a customer (with dependencies)
	 */
	public boolean[] getCustomersRequirements(boolean[] customerSelection)
	{
		boolean[] requirements = new boolean[requirementCosts.length];
		
		for (int i = 0; i < requirements.length; i++)
			requirements[i] = false;
		
		for (int i = 0; i < customerSelection.length; i++)
			if (customerSelection[i])
				addCustomerRequirements(i, requirements);
		
		return requirements;
	}

	/**
	 * Adds all requirements of a given customer and their precedents
	 */
	private void addCustomerRequirements(int customer, boolean[] requirements)
	{
		int len = customerRequirements[customer].length;
		
		for (int j = 0; j < len; j++)
		{
			int requirement = customerRequirements[customer][j];
			
			if (requirement >= 0 && !requirements[requirement])
				addRequirementAndDependencies(requirement, requirements);
		}
	}

	/**
	 * Adds a requirement and the sources of its dependencies
	 */
	private void addRequirementAndDependencies(int requirement, boolean[] requirements)
	{
		requirements[requirement] = true;
		
		if (requirementDependencySources[requirement] == null)
			return;
		
		int len = requirementDependencySources[requirement].length;
			
		for (int k = 0; k < len; k++)
		{
			int source = requirementDependencySources[requirement][k];
			
			if (source >= 0 && !requirements[source])
				addRequirementAndDependencies(source, requirements);
		}
	}
	
	/**
	 * Calculates the cost of attending a set of customers
	 */
	public int calculateCost(boolean[] customerSelection)
	{
		boolean[] requirements = getCustomersRequirements(customerSelection);
		
		int sum = 0;
		
		for (int i = 0; i < requirements.length; i++)
			if (requirements[i])
				sum += requirementCosts[i];

		return sum;
	}

	/**
	 * Calculates overall profit achieved by attending a set of customers
	 */
	public int calculateProfit(boolean[] customerSelection)
	{
		int sum = 0;
		
		for (int i = 0; i < customerProfits.length; i++)
			if (customerSelection[i])
				sum += customerProfits[i];
		
		return sum;
	}

	/**
	 * Calculates overall profit-related risk attained by attending a set of customers
	 */
	public double calculateProfitRisk(boolean[] customerSelection)
	{
		double sum = 0;
		
		for (int i = 0; i < customerProfitRisks.length; i++)
			if (customerSelection[i])
				sum += customerProfitRisks[i];
		
		return sum;
	}

	/**
	 * Calculates overall cost-related risk attained by attending a set of customers
	 */
	public double calculateCostRisk(boolean[] customerSelection)
	{
		boolean[] requirements = new boolean[requirementCosts.length];
		
		for (int i = 0; i < requirements.length; i++)
			requirements[i] = false;
		
		for (int i = 0; i < customerSelection.length; i++)
			if (customerSelection[i])
				addCustomerRequirements(i, requirements);
		
		double sum = 0;
		
		for (int i = 0; i < requirements.length; i++)
			if (requirements[i])
				sum += requirementCostRisks[i];

		return sum;
	}

	/**
	 * Returns the number of requirements desired by a customer
	 */
	public int getCustomerRequirementsCount(int customerIndex)
	{
		return customerRequirements[customerIndex].length;
	}

	/**
	 * Returns the index of a requirement desired by a customer, given the customer and the number of the requirement
	 */
	public int getCustomerRequirementIndex(int customerIndex, int index)
	{
		return customerRequirements[customerIndex][index];
	}

	/**
	 * Checks whether a given requirement has precedent requirements
	 */
	public boolean requirementHasPrecedents(int requirementIndex)
	{
		return requirementDependencySources[requirementIndex] != null;
	}

	/**
	 * Count the number of precedents for a given requirement
	 */
	public int getRequirementPrecedentsCount(int requirementIndex)
	{
		return requirementDependencySources[requirementIndex].length;
	}

	/**
	 * Returns a precedent for a given requirement
	 */
	public int getRequirementPrecedentIndex(int requirementIndex, int index)
	{
		return requirementDependencySources[requirementIndex][index];
	}

	public void setRequirementCostEstimates(int requirementIndex, double minimumCost, double maximumCost)
	{
		this.requirementCostMinimumEstimate[requirementIndex] = minimumCost;
		this.requirementCostMaximumEstimate[requirementIndex] = maximumCost;
		this.requirementCostRisks[requirementIndex] = (maximumCost - minimumCost) / 6.0;
	}

	public void setCustomerProfitEstimates(int customerIndex, double minimumProfit, double maximumProfit)
	{
		this.customerProfitMinimumEstimate[customerIndex] = minimumProfit;
		this.customerProfitMaximumEstimate[customerIndex] = maximumProfit;
		this.customerProfitRisks[customerIndex] = (maximumProfit - minimumProfit) / 6.0;
	}
}