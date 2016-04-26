package br.unirio.visualnrp.command;

import java.util.ArrayList;
import java.util.List;

import br.unirio.visualnrp.calc.RiskLandscapeReport;
import br.unirio.visualnrp.instance.Instance;
import br.unirio.visualnrp.instance.InstanceCategory;

/**
 * Class that represents the command that generates the cost risk landscape report
 * 
 * @author Marcio
 */
class CommandCostRiskLandscape extends Command
{
	private List<Instance> instances;
	private int[] budgets;
	private int[] riskLevels;
	private String outputFilename;
	
	/**
	 * Initializes the command
	 */
	public CommandCostRiskLandscape()
	{
		super("LCRR", "Landscape report for the cost risk problem");
		
		this.instances = new ArrayList<Instance>();
		this.budgets = null;
		this.riskLevels = null;
		this.outputFilename = "";
		
		addParameterHelp("-i", "List of instances, separated by whitespaces, or category");
		addParameterHelp("-b", "Budget percentiles, separated by whitespaces (0 to 100)");
		addParameterHelp("-r", "Risk levels, separated by whitespaces (0 to 100)");
		addParameterHelp("-o", "Output filename");
	}

	/**
	 * Parses the parameters used by the command
	 */
	@Override
	public void parseParameters(String[] parameters) throws Exception
	{
		parseInstanceParameter(parameters);
		parseBudgetParameter(parameters);
		parseRiskLevelParameter(parameters);
		outputFilename = getParameterValue(parameters, "-o");
	}

	/**
	 * Parse the parameter related to instances
	 */
	private void parseInstanceParameter(String[] parameters) throws Exception
	{
		String[] instanceNames = getParameterValues(parameters, "-i");
		
		for (String instanceName : instanceNames)
		{
			Instance instance = Instance.get(instanceName);
			
			if (instance == null)
			{
				InstanceCategory category = InstanceCategory.get(instanceName);
				
				if (category == null)
					throw new Exception("Instance '" + instanceName + "' not found.");
				
				Instance.addInstancesFromCategory(category, instances);
			}
			else
			{
				instances.add(instance);
			}
		}
	}

	/**
	 * Parse the parameter related to budget
	 */
	private void parseBudgetParameter(String[] parameters) throws Exception
	{
		String[] values = getParameterValues(parameters, "-b");
		this.budgets = asIntegerArray(values);
		
		if (this.budgets.length == 0)
			throw new Exception("No budget percentile was found.");
	}

	/**
	 * Parse the parameter related to risk levels
	 */
	private void parseRiskLevelParameter(String[] parameters) throws Exception
	{
		String[] values = getParameterValues(parameters, "-r");
		this.riskLevels = asIntegerArray(values);
		
		if (this.riskLevels.length == 0)
			throw new Exception("No risk level was found.");
	}

	/**
	 * Runs the command
	 */
	@Override
	public boolean run() throws Exception
	{
		new RiskLandscapeReport().executeCostRisk(instances, budgets, riskLevels, outputFilename);
		return false;
	}

	/**
	 * Creates a new instance for the command
	 */
	@Override
	public Command clone()
	{
		return new CommandCostRiskLandscape();
	}
}