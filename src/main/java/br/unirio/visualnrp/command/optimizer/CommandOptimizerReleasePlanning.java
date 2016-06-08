package br.unirio.visualnrp.command.optimizer;

import java.util.ArrayList;
import java.util.List;

import br.unirio.visualnrp.calc.optimizer.ReleasePlanningOptimizer;
import br.unirio.visualnrp.command.Command;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.InstanceCategory;
import br.unirio.visualnrp.support.Conversion;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Class that represents the command that optimizes the release-planning problem
 * 
 * @author Marcio
 */
public class CommandOptimizerReleasePlanning extends Command
{
	private List<Instance> instances;
	private String outputFilename;
	private double interestRate;
	private int budget;
	private int rounds;
	private long seed;

	/**
	 * Initializes the command
	 */
	public CommandOptimizerReleasePlanning()
	{
		super("ORL", "Optimizer for the release planning problem");
		
		this.instances = new ArrayList<Instance>();
		this.outputFilename = "";
		this.interestRate = 0.0;
		this.budget = 0;
		this.rounds = 0;
		this.seed = -1;

		addParameterHelp("-i", "Instance name");
		addParameterHelp("-o", "Output filename");
		addParameterHelp("-t", "Interest rate (0 to 100)");
		addParameterHelp("-b", "Budget percentile (0 to 100)");
		addParameterHelp("-r", "Number of rounds (1 or more)");
		addParameterHelp("-s", "Fixed random number generator seed (optional)");
	}

	/**
	 * Parses the parameters used by the command
	 */
	@Override
	public void parseParameters(String[] parameters) throws Exception
	{
		parseInstanceParameter(parameters);

		outputFilename = getParameterValue(parameters, "-o");
		
		String sRate = getParameterValue(parameters, "-t");
		this.interestRate = Conversion.safeParseDouble(sRate, 0.0);
		
		String sBudget = getParameterValue(parameters, "-b");
		this.budget = Conversion.safeParseInteger(sBudget, 30);
		
		String sRounds = getParameterValue(parameters, "-r");
		this.rounds = Conversion.safeParseInteger(sRounds, 100 / budget);
		
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
	 * Runs the command
	 */
	@Override
	public boolean run() throws Exception
	{
		PseudoRandom.init(seed);
		new ReleasePlanningOptimizer().execute(instances, budget, outputFilename, rounds, interestRate);
		return true;
	}

	/**
	 * Creates a new instance for the command
	 */
	@Override
	public Command clone()
	{
		return new CommandOptimizerReleasePlanning();
	}
}