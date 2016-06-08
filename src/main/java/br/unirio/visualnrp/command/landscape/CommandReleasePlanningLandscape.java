package br.unirio.visualnrp.command.landscape;

import br.unirio.visualnrp.calc.landscape.ReleaseLandscapeReport;
import br.unirio.visualnrp.command.Command;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.support.Conversion;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Class that represents the command that generates the release-planning report
 * 
 * @author Marcio
 */
public class CommandReleasePlanningLandscape extends Command
{
	private String instanceName = "";
	private String outputFilename = "";
	private int budget = -1;
	private int rounds = -1;
	private long seed = -1;

	/**
	 * Initializes the command
	 */
	public CommandReleasePlanningLandscape()
	{
		super("LRPR", "Landscape report for the release planning problem");
		addParameterHelp("-i", "Instance name");
		addParameterHelp("-o", "Output filename");
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
		instanceName = getParameterValue(parameters, "-i");
		outputFilename = getParameterValue(parameters, "-o");
		
		String sBudget = getParameterValue(parameters, "-b");
		this.budget = Conversion.safeParseInteger(sBudget, 30);
		
		String sRounds = getParameterValue(parameters, "-r");
		this.rounds = Conversion.safeParseInteger(sRounds, 100 / budget);
		
		String sSeedValue = getOptionalParameterValue(parameters, "-s");
		this.seed = Conversion.safeParseLong(sSeedValue, System.nanoTime());
	}

	/**
	 * Runs the command
	 */
	@Override
	public boolean run() throws Exception
	{
		Instance instance = Instance.get(instanceName);
		
		if (instance == null)
			throw new Exception("Instance '" + instanceName + "' not found.");
		
		PseudoRandom.init(seed);
		new ReleaseLandscapeReport().execute(instance, budget, outputFilename, rounds);
		return true;
	}

	/**
	 * Creates a new instance for the command
	 */
	@Override
	public Command clone()
	{
		return new CommandReleasePlanningLandscape();
	}
}