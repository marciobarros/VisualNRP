package sobol.problems.requirements;

import sobol.problems.requirements.calc.LandscapeReleasePlanning;
import sobol.problems.requirements.calc.Optimizer;
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

	private void runLandscapeReport() throws Exception
	{
//		new LandscapeReport()
//			.execute(classicInstanceFilenames, new int[] {30}, new int[] {20, 50, 80});
		
//		new LandscapeReport()
//			.execute(classicInstanceFilenames, new int[] {30, 50, 70}, new int[] {0})
//			.execute(realisticInstanceFilenames, new int[] {30, 50}, new int[] {0});
	}

	public static void main(String[] args) throws Exception
	{
		if (args.length == 0)
		{
			System.out.println("Parameter missing. " + CommandFactory.getInstance().generalHelp());
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