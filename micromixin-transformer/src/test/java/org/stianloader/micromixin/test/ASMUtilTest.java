package org.stianloader.micromixin.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;

public class ASMUtilTest {

//    @Test
//    public void testMoveStackHeadCat2() {
//        MethodNode method = new MethodNode(0, "test", "()V", null, null);
//        AbstractInsnNode magicSink = new MethodInsnNode(Opcodes.INVOKESTATIC, "Magic", "sink", "(DDLjava/lang/Object;)V");
//        method.instructions.add(new InsnNode(Opcodes.DCONST_0));
//        method.instructions.add(new InsnNode(Opcodes.DCONST_1));
//        method.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
//        method.instructions.add(magicSink);
//        method.instructions.add(new InsnNode(Opcodes.RETURN));
//        method.maxLocals = 2;
//        method.maxStack = 5;
//
//        List<String> headTypes = new ArrayList<String>();
//        headTypes.add("Ljava/lang/Object;");
//        headTypes.add("I");
//        headTypes.add("D");
//        headTypes.add("D");
//        headTypes.add("Ljava/lang/Object;");
//        headTypes.add("Ljava/lang/Object;");
//
//        InsnList preRollbackInsn = new InsnList();
//        InsnList rollbackInsn = new InsnList();
//
//        ASMUtil.moveStackHead(method, magicSink, magicSink, headTypes, 6, preRollbackInsn, rollbackInsn);
//
//        method.instructions.insertBefore(magicSink, preRollbackInsn);
//        method.instructions.insertBefore(magicSink, rollbackInsn);
//
//        TraceMethodVisitor tmv = new TraceMethodVisitor(new Textifier());
//        method.accept(tmv);
//        while (!tmv.p.text.isEmpty()) {
//            System.out.print(tmv.p.text.remove(0));
//        }
//    }

    @Test
    public void testShallowStashConfig() {
        // 0b01_01 = 5
        assertEquals(5, ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "I"), 1));
        assertEquals(5, ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "I", "D"), 2));
        assertEquals(5, ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "Ljava/lang/Object"), 1));
        assertEquals(5, ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "I", "Ljava/lang/Object"), 1));
        // 0b01_10 = 6
        assertEquals(6, ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "D"), 1));
        assertEquals(6, ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "D", "D"), 2));
        assertEquals(6, ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "Ljava/lang/Object", "Ljava/lang/Object"), 2));
        assertEquals(6, ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "I", "Ljava/lang/Object", "Ljava/lang/Object"), 2));
        // 0b10_01 = 9
        assertEquals(9, ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "I"), 1));
        assertEquals(9, ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "I", "D"), 2));
        assertEquals(9, ASMUtil.getShallowStashConfiguration(Arrays.asList("D", "Ljava/lang/Object"), 1));
        assertEquals(9, ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "I", "Ljava/lang/Object"), 1));
        assertEquals(9, ASMUtil.getShallowStashConfiguration(Arrays.asList("I", "D", "Ljava/lang/Object"), 1));
    }
}
