package br.unirio.visualnrp.command.tunning;

import java.util.ArrayList;
import java.util.List;

import br.unirio.visualnrp.calc.tunning.InitialSeedTunning;
import br.unirio.visualnrp.command.Command;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.InstanceCategory;
import br.unirio.visualnrp.support.Conversion;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Class that represents the command to run the profit optimization report
 * 
 * @author Marcio
 */
public class CommandInitialSeedTunning extends Command
{
	private List<Instance> instances;
	private int[] budgets;
	private String outputFilename;
	private long seed;
	
	/**
	 * Initializes the command
	 */
	public CommandInitialSeedTunning()
	{
		super("TIS", "Optimization report for tunning the initial seed parameter");
		
		this.instances = new ArrayList<Instance>();
		this.budgets = null;
		this.outputFilename = "";
		
		addParameterHelp("-i", "List of instances, separated by whitespaces, or category");
		addParameterHelp("-b", "Budget percentiles, separated by whitespaces (0 to 100)");
		addParameterHelp("-o", "Output filename");
		addParameterHelp("-s", "Fixed random number generator seed (optional)");
	}

	/**
	 * Parses the parameters used by the command
	 */
	@Override
	public void parseParameters(String[] parameters) throws Exception
	{
		parseInstanceParameter(parameters);
		parseBudgetParameter(parameters);
		
		this.outputFilename = getParameterValue(parameters, "-o");
		
		String sSeedValue = getOptionalParameterValue(parameters, "-s");
		this.seed = Conversion.safeParseLong(sSeedValue, System.nanoTime());
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
	 * Runs the command
	 */
	@Override
	public boolean run() throws Exception
	{
		PseudoRandom.init(seed);
		new InitialSeedTunning().execute(instances, budgets, outputFilename);
		return false;
	}

	/**
	 * Creates a new instance for the command
	 */
	@Override
	public Command clone()
	{
		return new CommandInitialSeedTunning();
	}
}