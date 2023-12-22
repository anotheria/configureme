package org.configureme.moskitocontroltest;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MoskitoControlTest {
    @Test
    public void testCharts(){
        MoskitoControlTestConfiguration configuration = MoskitoControlTestConfiguration.loadConfiguration();
        ChartConfig[] charts = configuration.getCharts();
        assertNotNull(charts);
        for (ChartConfig chart : charts) {
            System.out.println(chart.getName());
            ChartLineConfig[] lines = chart.getLines();
            for (ChartLineConfig line : lines) {
                System.out.println(line.getComponent());
                System.out.println(line.getAccumulator());
                assertNotNull(line.getComponent());
                assertNotNull(line.getAccumulator());
                Assert.assertNotEquals(0, line.getComponent().length());
                Assert.assertNotEquals(0, line.getAccumulator().length());

            }
        }


    }
}
