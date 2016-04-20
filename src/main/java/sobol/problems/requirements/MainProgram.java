package sobol.problems.requirements;

import sobol.problems.requirements.calc.Optimizer;
import sobol.problems.requirements.calc.ReleaseLandscapeReport;
import sobol.problems.requirements.command.Command;
import sobol.problems.requirements.command.CommandFactory;

/**
 * Programa principal
 * 
 * @author Marcio
 */
@SuppressWarnings("unused")
public class MainProgram
{
	private void runOptimizer() throws Exception
	{
//		new Optimizer()
//			.clean()
//			.execute(classicInstanceFilenames, new double[] {0.3, 0.5, 0.7})
//			.execute(realisticInstanceFilenames, new double[] {0.3, 0.5});
	}

	// LCRR -i classic -b 30 -r 20 50 80 -o results/landscape/cost-risk/%s.txt
	// LPRR -i classic -b 30 -r 20 50 80 -o results/landscape/profit-risk/%s.txt
	public static void main(String[] args) throws Exception
	{
		if (args.length == 0)
		{
			System.out.println("Command missing. " + CommandFactory.getInstance().generalHelp());
			return;
		}
		
		Command command = CommandFactory.getInstance().createCommand(args[0]);
		
		if (command == null)
		{
			System.out.println("Invalid command. " + CommandFactory.getInstance().generalHelp());
			return;
		}

		try
		{
			command.parseParameters(args);
			command.run();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage() + " " + CommandFactory.getInstance().commandHelp(command));
		}
	}
}