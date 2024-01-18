package org.stianloader.micromixin.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.stianloader.micromixin.internal.util.ASMUtil;

public class ASMUtilTest {

    @Test
    public void testShallowStashConfig() {
        // 0b01_01 = 5
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "I"), 1), 5);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "I", "D"), 2), 5);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "Ljava/lang/Object"), 1), 5);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "I", "Ljava/lang/Object"), 1), 5);
        // 0b01_10 = 6
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "D"), 1), 6);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "D", "D"), 2), 6);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "Ljava/lang/Object", "Ljava/lang/Object"), 2), 6);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "I", "Ljava/lang/Object", "Ljava/lang/Object"), 2), 6);
        // 0b10_01 = 9
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "I"), 1), 9);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "I", "D"), 2), 9);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "Ljava/lang/Object"), 1), 9);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "I", "Ljava/lang/Object"), 1), 9);
        assertEquals(ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "D", "Ljava/lang/Object"), 1), 9);
    }
}
