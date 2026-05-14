package org.stianloader.micromixin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.api.InjectionPointConstraint;
import org.stianloader.micromixin.transform.api.InjectionPointSelector;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.selectors.StringSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.InvokeInjectionPointSelector;

public class SelectorTest {

    @Test
    public void testDotStringSelector() {
        StringSelector selector = new StringSelector("java.util.Arrays.equals([SII[SII)Z");

        assertEquals("([SII[SII)Z", selector.getDesc());
        assertEquals("equals", selector.getName());
        assertEquals("java/util/Arrays", selector.getOwner());
        MethodInsnNode mInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Arrays", "equals", "([SII[SII)Z");
        assertTrue(selector.isValid(mInsn, new SimpleRemapper(), new StringBuilder()));

        InjectionPointSelector invokeSelector = InvokeInjectionPointSelector.PROVIDER.create(null, new InjectionPointConstraint[] { selector });

        MethodNode mnode = new MethodNode(0, "m", "([SII[SII)Z", null, null);
        MethodNode enode = new MethodNode(0, "empty", "()V", null, null);

        mnode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mnode.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "worldsink", "sink", "(Ljava/lang/Object;)V"));
        mnode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        mnode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        mnode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
        mnode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
        mnode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
        mnode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
        mnode.instructions.add(mInsn);
        mnode.instructions.add(new InsnNode(Opcodes.IRETURN));

        enode.instructions.add(new InsnNode(Opcodes.RETURN));

        assertEquals(mInsn, invokeSelector.getFirstInsn(mnode, null, null, new SimpleRemapper(), new StringBuilder()));
        assertNull(invokeSelector.getFirstInsn(enode, null, null, new SimpleRemapper(), new StringBuilder()));
        assertThrows(IllegalStateException.class, () -> invokeSelector.getFirstInsn(new MethodNode(), null, null, new SimpleRemapper(), new StringBuilder()));
    }
}
