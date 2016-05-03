package br.unirio.visualnrp;

import br.unirio.visualnrp.command.Command;
import br.unirio.visualnrp.command.CommandFactory;

/**
 * Programa principal
 * 
 * @author Marcio
 */
public class MainProgram
{
	// LCRR -i classic -b 30 -r 20 50 80 -o results/landscape/cost-risk/%s.txt -s 253958446296927
	// LPRR -i classic -b 30 -r 20 50 80 -o results/landscape/profit-risk/%s.txt
	public static void main(String[] args) throws Exception
	{
//		System.in.read();

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