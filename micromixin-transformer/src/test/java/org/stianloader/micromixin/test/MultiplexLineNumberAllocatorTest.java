package org.stianloader.micromixin.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.stianloader.micromixin.transform.internal.util.smap.MultiplexLineNumberAllocator;
import org.stianloader.micromixin.transform.internal.util.smap.SMAPRoot;

public class MultiplexLineNumberAllocatorTest {
    @Test
    public void testMutliAllocation() throws IOException {
        ClassNode node0 = new ClassNode();
        ClassNode node1 = new ClassNode();
        ClassNode node2 = new ClassNode();
        new ClassReader(MultiplexLineNumberAllocatorTest.class.getName()).accept(node0, 0);
        new ClassReader(ClassNode.class.getName()).accept(node1, 0);
        new ClassReader(MultiplexLineNumberAllocator.class.getName()).accept(node2, 0);
        MultiplexLineNumberAllocator allocator = new MultiplexLineNumberAllocator(node0);
        allocator.reserve(node1, new LineNumberNode(10, new LabelNode()), new LabelNode());
        allocator.reserve(node1, new LineNumberNode(12, new LabelNode()), new LabelNode());
        allocator.reserve(node1, new LineNumberNode(13, new LabelNode()), new LabelNode());
        allocator.reserve(node1, new LineNumberNode(15, new LabelNode()), new LabelNode());
        allocator.reserve(node2, new LineNumberNode(5, new LabelNode()), new LabelNode());
        allocator.reserve(node2, new LineNumberNode(6, new LabelNode()), new LabelNode());
        allocator.reserve(node2, new LineNumberNode(7, new LabelNode()), new LabelNode());

        String exportedSMAP = allocator.exportToSMAP(SMAPRoot.MIXIN_STRATUM).pushContents(new StringBuilder()).toString().replace('\r', '\n');
        assertEquals("SMAP\n"
                + "MultiplexLineNumberAllocatorTest.java\n"
                + "Mixin\n"
                + "*S Mixin\n"
                + "*F\n"
                + "+ 1 MultiplexLineNumberAllocatorTest.java\n"
                + "org/stianloader/micromixin/test/MultiplexLineNumberAllocatorTest.java\n"
                + "+ 2 ClassNode.java\n"
                + "org/objectweb/asm/tree/ClassNode.java\n"
                + "+ 3 MultiplexLineNumberAllocator.java\n"
                + "org/stianloader/micromixin/transform/internal/util/smap/MultiplexLineNumberAllocator.java\n"
                + "*L\n"
                + "1#1,53:1,1\n"
                + "10#2,5:55,1\n"
                + "5#3,2:61,1\n"
                + "*V\n"
                + "org.stianloader.micromixin.transform\n"
                + "*E\n"
                + "", exportedSMAP);
    }
}
