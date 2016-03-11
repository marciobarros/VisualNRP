package sobol.problems.requirements.hc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import jmetal.util.PseudoRandom;

import org.junit.Test;

import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.GreedyConstructor;
import sobol.problems.requirements.model.Project;

public class GreedyConstructorTest {
    
    @Test(expected=RuntimeException.class)
    public void generateSolutionWithoutRandomGenerator() {
        Project project = mock(Project.class);        
        Constructor constr = new GreedyConstructor(project);

        constr.generateSolution();
    }    
    
    @Test
    public void generateSolutionWith2CustomersShouldReturnSolutionWithBiggerRatio() {
        Project project = mock(Project.class);
        when(project.getCustomerCount()).thenReturn(2);
        when(project.getCustomerProfit(anyInt())).thenReturn(100, 1);
        when(project.calculateCost(any(boolean[].class))).thenReturn(1, 10);
        when(PseudoRandom.randInt(anyInt(), anyInt())).thenReturn(1, 0);

        Constructor constr = new GreedyConstructor(project);
        
        boolean[] sol = constr.generateSolution();
        assertEquals(2, sol.length);
        assertTrue(sol[0]);
        assertFalse(sol[1]);
    }
    
    @Test
    public void generateSolutionWith2CustomersShouldReturnSolutionWithLowestRatioWithSmallProbability() {
        Project project = mock(Project.class);
        when(project.getCustomerCount()).thenReturn(2);
        when(project.getCustomerProfit(anyInt())).thenReturn(100, 1);
        when(project.calculateCost(any(boolean[].class))).thenReturn(1, 10);
        when(PseudoRandom.randInt(anyInt(), anyInt())).thenReturn(1, 100010);

        Constructor constr = new GreedyConstructor(project);        
        boolean[] sol = constr.generateSolution();
        assertEquals(2, sol.length);
        assertFalse(sol[0]);
        assertTrue(sol[1]);
    }
    
    @Test
    public void generateSolutionShouldRespectProfitLossRatio() {
        Project project = mock(Project.class);
        when(project.getCustomerCount()).thenReturn(4);
        when(project.getCustomerProfit(anyInt())).thenReturn(100, 1, 10, 10);
        when(project.calculateCost(any(boolean[].class))).thenReturn(1, 10, 10, 1);

        Constructor constr = new GreedyConstructor(project);
 
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(int i = 0; i < 4; i++) {
            map.put(i, 0);
        }
        
        for(int i = 0; i < 1000; i++) {
            boolean[] sol = constr.generateSolution();
            for(int j=0; j<sol.length; j++) {
                if(sol[j] == true) {
                    map.put(j, map.get(j)+1);
                }
            }
        }

        assertTrue(map.get(0) > map.get(1));
        assertTrue(map.get(0) > map.get(2));
    }
    
    @Test
    public void generateSolutionWith1ShouldReturnSolutionWith1Customer() {
        Project project = mock(Project.class);
        when(project.getCustomerCount()).thenReturn(4);
        when(project.getCustomerProfit(anyInt())).thenReturn(100, 1, 10, 10);
        when(project.calculateCost(any(boolean[].class))).thenReturn(1, 10, 10, 1);

        Constructor constr = new GreedyConstructor(project);
        
        boolean[] sol = constr.generateSolutionWith(1);
        int count = 0;
        for (int j = 0; j < sol.length; j++) {
            if (sol[j] == true) {
                count++;
            }
        }
        
        assertEquals(1, count);
    }
    
    @Test
    public void generateSolutionWith3ShouldReturnSolutionWith3Customers() {
        Project project = mock(Project.class);
        when(project.getCustomerCount()).thenReturn(4);
        when(project.getCustomerProfit(anyInt())).thenReturn(100, 1, 10, 10);
        when(project.calculateCost(any(boolean[].class))).thenReturn(1, 10, 10, 1);

        Constructor constr = new GreedyConstructor(project);
        
        boolean[] sol = constr.generateSolutionWith(3);
        int count = 0;
        for (int j = 0; j < sol.length; j++) {
            if (sol[j] == true) {
                count++;
            }
        }
        
        assertEquals(3, count);
    }
    
    @Test
    public void generateSolutionInInterval2_3ShouldReturnSolutionWith2or3Customers() {
        Project project = mock(Project.class);
        when(project.getCustomerCount()).thenReturn(4);
        when(project.getCustomerProfit(anyInt())).thenReturn(100, 1, 10, 10);
        when(project.calculateCost(any(boolean[].class))).thenReturn(1, 10, 10, 1);

        Constructor constr = new GreedyConstructor(project);
       
        boolean[] sol = constr.generateSolutionInInterval(2, 3);
        int count = 0;
        for (int j = 0; j < sol.length; j++) {
            if (sol[j] == true) {
                count++;
            }
        }
        
        assertTrue(count >= 2 && count <= 3);
    }
}
