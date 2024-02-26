package org.stianloader.micromixin.transform.internal.util.smap;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;

// Giant hack, because I can't be bothered to write proper code.
// If you are willing to remove all references to this class we can get rid of this one though
public class NOPMultiplexLineNumberAllocator extends MultiplexLineNumberAllocator {

    @NotNull
    public static final NOPMultiplexLineNumberAllocator INSTANCE = new NOPMultiplexLineNumberAllocator();

    private NOPMultiplexLineNumberAllocator() {
        super(new ClassNode());
    }

    @Override
    @NotNull
    public SMAPRoot exportToSMAP(@NotNull String stratum) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public LineNumberNode reserve(@NotNull ClassNode allocator, @NotNull LineNumberNode originNode,
            @NotNull LabelNode copiedStart) {
        return new LineNumberNode(originNode.line, copiedStart);
    }
}
