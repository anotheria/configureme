package org.configureme.moskitocontroltest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                Assertions.assertNotEquals(0, line.getComponent().length());
                Assertions.assertNotEquals(0, line.getAccumulator().length());

            }
        }


    }
}
