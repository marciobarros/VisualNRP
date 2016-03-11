package sobol.problems.requirements.hc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import jmetal.util.PseudoRandom;

import org.junit.Test;

import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.RandomConstructor;
import sobol.problems.requirements.model.Project;

public class RandomConstructorTest {

    @Test(expected=RuntimeException.class)
    public void generateSolutionWithoutRandomGenerator() {
        Project project = mock(Project.class);        
        Constructor constr = new RandomConstructor(project);
        constr.generateSolution();
    }
    
    @Test
    public void generateRandomSolution() {
        Project project = mock(Project.class);
        Constructor constr = new RandomConstructor(project);
        
        when(project.getCustomerCount()).thenReturn(5);
        when(PseudoRandom.randDouble()).thenReturn(0.6);
        when(PseudoRandom.randDouble()).thenReturn(0.1);
        when(PseudoRandom.randDouble()).thenReturn(0.2);
        when(PseudoRandom.randDouble()).thenReturn(0.7);
        when(PseudoRandom.randDouble()).thenReturn(0.8);
        boolean[] sol = constr.generateSolution();
        
        assertEquals(5, sol.length);
        assertTrue(Arrays.equals(new boolean[] {true,false,false,true,true}, sol));
    }
    
    @Test
    public void generateSolutionWith3Customers() {
        Project project = mock(Project.class);
        Constructor constr = new RandomConstructor(project);
        
        when(project.getCustomerCount()).thenReturn(5);
//        when(PseudoRandom.singleInt(anyInt(), anyInt())).thenReturn(4, 2, 2, 1, 0);
        boolean[] sol = constr.generateSolutionWith(3);
        
        assertEquals(5, sol.length);
        int count = 0;
        for(int i=0; i < sol.length; i++) {
            if(sol[i] == true) {
                count++;
            }
        }
        assertEquals(3, count);
    }    
    
    @Test
    public void generateSolutionWithAtLeast2AndAtMost4Customers() {
        Project project = mock(Project.class);
        Constructor constr = new RandomConstructor(project);
        
        when(project.getCustomerCount()).thenReturn(5);
//        when(PseudoRandom.singleInt(anyInt(), anyInt())).thenReturn(3, 4, 2, 2, 1, 0);
        boolean[] sol = constr.generateSolutionInInterval(2, 4);
        
        assertEquals(5, sol.length);
        int count = 0;
        for(int i=0; i < sol.length; i++) {
            if(sol[i] == true) {
                count++;
            }
        }
        assertTrue(count >= 2 && count <=4);
    }     
}
