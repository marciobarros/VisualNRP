package sobol.problems.requirements;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.GreedyConstructor;
import sobol.problems.requirements.algorithm.ils.VisIteratedLocalSearch;
import sobol.problems.requirements.model.Project;
import sobol.problems.requirements.reader.RequirementReader;

public class MainProgram {

    private static int CICLOS = 30;
    private static String[] instanceFilenamesClassic = {
        "data/requirements/classic/nrp1.txt",
//        "data/requirements/classic/nrp2.txt",
//        "data/requirements/classic/nrp3.txt",
//        "data/requirements/classic/nrp4.txt",
//        "data/requirements/classic/nrp5.txt",
        ""
    };
    private static String[] instanceFilenamesRealistic = {
        /*"data/requirements/realistic/nrp-e1.txt",
         "data/requirements/realistic/nrp-e2.txt",
         "data/requirements/realistic/nrp-e3.txt",
         "data/requirements/realistic/nrp-e4.txt",
         "data/requirements/realistic/nrp-g1.txt",
         "data/requirements/realistic/nrp-g2.txt",
         "data/requirements/realistic/nrp-g3.txt",
         "data/requirements/realistic/nrp-g4.txt",
         "data/requirements/realistic/nrp-m1.txt",
         "data/requirements/realistic/nrp-m2.txt",
         "data/requirements/realistic/nrp-m3.txt",
         "data/requirements/realistic/nrp-m4.txt",*/
        ""
    };

    private List<Project> readInstances(String[] filenames) throws Exception {
        List<Project> instances = new ArrayList<Project>();

        for (String filename : filenames) {
            if (filename.length() > 0) {
                System.out.print(filename + " ");
                RequirementReader reader = new RequirementReader(filename);
                Project project = reader.execute();
                System.out.println(project.getRequirementCount() + " reqs " + project.getCustomerCount() + " cust");
                instances.add(project);
            }
        }

        return instances;
    }

    private void runInstance(PrintWriter out, PrintWriter details, String tipo, Project instance, int cycles, double budgetFactor, float intervalSize) throws Exception {
        Constructor constructor = new GreedyConstructor(instance);
        int budget = (int) (budgetFactor * instance.getTotalCost());

        for (int i = 0; i < cycles; i++) {
            VisIteratedLocalSearch hcr = new VisIteratedLocalSearch(details, instance, budget, 10000000, 10, intervalSize, constructor);
//            IteratedLocalSearch hcr = new IteratedLocalSearch(details, instance, budget, 10000000, constructor);
//            HillClimbing hcr = new HillClimbing(details, instance, budget, 10000000, constructor);

            long initTime = System.currentTimeMillis();
            details.println(tipo + " " + instance.getName() + " #" + cycles);
            boolean[] solution = hcr.execute();
            details.println();
            long executionTime = (System.currentTimeMillis() - initTime);

            String s = tipo + "; " + instance.getName() + " #" + i + "; " + executionTime + "; " + hcr.getFitness() + "; " + hcr.getIterations() + "; " + hcr.getIterationBestFound() + "; " + hcr.printSolution(solution);
            System.out.println(s);
            out.println(s);
        }
    }

    public static void main(String[] args) throws Exception {
        MainProgram mp = new MainProgram();

        Vector<Project> instances = new Vector<Project>();
        instances.addAll(mp.readInstances(instanceFilenamesClassic));
        instances.addAll(mp.readInstances(instanceFilenamesRealistic));

        FileWriter outFile = new FileWriter("saida.txt");
        PrintWriter out = new PrintWriter(outFile);

        FileWriter detailsFile = new FileWriter("saida details.txt");
        PrintWriter details = new PrintWriter(detailsFile);

        for (Project instance : instances) {
            mp.runInstance(out, details, "VISILS", instance, CICLOS, 0.3, 1.0f);
        }

        out.close();
        details.close();
    }

    public static int countCustomersInSolution(boolean[] solution) {
        int count = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == true) {
                count++;
            }
        }
        return count;
    }
}
