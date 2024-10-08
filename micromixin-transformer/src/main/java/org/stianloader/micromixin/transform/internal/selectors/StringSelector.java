package org.stianloader.micromixin.transform.internal.selectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.stianloader.micromixin.transform.api.InjectionPointConstraint;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.MixinStub;

public class StringSelector implements MixinTargetSelector, InjectionPointConstraint {

    @Nullable
    private final String owner;
    @Nullable
    private final String name;
    @Nullable
    private final String desc;

    public StringSelector(@NotNull String text) {
        // Explicit target selectors can contain whitespace characters (such as space or tab)
        // which are completely ignored. While we probably could use a regular expressions for that, I don't
        // think that performance would be much better due to the associated overhead.
        {
            StringBuilder purged = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                int codepoint = text.codePointAt(i);
                if (!Character.isWhitespace(codepoint)) {
                    purged.appendCodePoint(codepoint);
                }
            }
            text = purged.toString();
        }

        int colonIndex = text.indexOf(':');
        int semicolonIndex = text.indexOf(';');
        int descStartIndex = text.indexOf('(');
        int endName;
        int startName;

        if (colonIndex >= 0) {
            if (descStartIndex >= 0) {
                throw new IllegalArgumentException("The usage of the colon (':') indicates a field string, but the target selector contains a '(', which is an illegal character within field selectors.");
            }
            descStartIndex = colonIndex + 1;
        }

        if (semicolonIndex != -1 && (descStartIndex == -1 || semicolonIndex < descStartIndex)) {
            this.owner = text.substring(1, semicolonIndex);
            startName = semicolonIndex + 1;
        } else {
            this.owner = null;
            startName = 0;
        }
        if (descStartIndex == -1) {
            this.desc = null;
            endName = text.length();
        } else {
            this.desc = text.substring(descStartIndex);
            endName = descStartIndex;
            if (colonIndex >= 0) {
                endName--;
            }
        }
        if (endName > startName) {
            this.name = text.substring(startName, endName);
        } else {
            this.name = null;
        }
    }

    public StringSelector(@Nullable String owner, @Nullable String name, @Nullable String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    @Override
    @Nullable
    public MethodNode selectMethod(@NotNull ClassNode within, @NotNull MixinStub source) {
        for (MethodNode method : within.methods) {
            if (this.name != null && !method.name.equals(this.name)) {
                continue;
            }
            if (this.desc != null && !method.desc.equals(this.desc)) {
                continue;
            }
            if (this.owner != null && !within.name.equals(this.owner)) {
                continue;
            }
            return method;
        }
        return null;
    }

    @Override
    public String toString() {
        return "StringSelector[owner = " + this.owner + ", name = " + name + ", desc = " + desc + "]";
    }

    @Override
    public boolean isValid(@NotNull AbstractInsnNode insn, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // My IDE is bullying me
        String name = this.name;
        String owner = this.owner;
        String desc = this.desc;
        if (insn instanceof MethodInsnNode) {
            MethodInsnNode mInsn = (MethodInsnNode) insn;
            return (name == null || name.equals(mInsn.name))
                    && (owner == null || owner.equals(mInsn.owner))
                    && (desc == null || desc.equals(mInsn.desc));
        } else if (insn instanceof TypeInsnNode) {
            // TODO We may need to RE this fully. This can't be right
            return (owner != null && owner.equals(((TypeInsnNode)insn).desc))
                    || (owner != null && owner.equals(((TypeInsnNode)insn).desc))
                    || (desc != null && desc.equals(((TypeInsnNode)insn).desc));
        } else if (insn instanceof FieldInsnNode) {
            FieldInsnNode fInsn = (FieldInsnNode) insn;
            return (name == null || name.equals(fInsn.name))
                    && (owner == null || owner.equals(fInsn.owner))
                    && (desc == null || desc.equals(fInsn.desc));
        }
        throw new IllegalArgumentException("Instructions of type " + insn.getClass().getName() + " cannot be verified by " + this.toString() + ". This indicates a bug in Micromixin or one of custom-made injection point selectors (should those be present).");
    }
}
