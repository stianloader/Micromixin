package org.stianloader.micromixin.transform.internal.util;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.LabelNode;

public interface LabelNodeMapper {

    public static class LazyDuplicateLabelNodeMapper implements LabelNodeMapper {
        @NotNull
        private final Map<LabelNode, LabelNode> labelMap;

        public LazyDuplicateLabelNodeMapper() {
            this(new HashMap<LabelNode, LabelNode>());
        }

        public LazyDuplicateLabelNodeMapper(@NotNull Map<LabelNode, LabelNode> labelMap) {
            this.labelMap = labelMap;
        }

        @Override
        @NotNull
        public LabelNode apply(LabelNode label) {
            LabelNode l = labelMap.get(label);
            if (l == null) {
              l = new LabelNode();
              this.labelMap.put(label, l);
            }
            return l;
        }
    }

    @NotNull
    LabelNode apply(LabelNode source);
}
