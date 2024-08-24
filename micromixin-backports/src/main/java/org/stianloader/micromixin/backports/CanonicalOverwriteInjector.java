package org.stianloader.micromixin.backports;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes.InjectionNode;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.util.Bytecode;

public class CanonicalOverwriteInjector extends Injector {

    public CanonicalOverwriteInjector(CanonicalOverwriteInjectionInfo info) {
        super(info, "@CanonicalOverwrite");
    }

    @Override
    protected void inject(Target target, InjectionNode node) {
        if (this.isStatic) {
            throw new IllegalStateException("The handler method of a @CanonicalOverwrite may never be static, however in this case the handler method is static. This is not permissible as per the documentation of @CanonicalOverwrite.");
        } else if (target.isStatic) {
            throw new IllegalStateException("The target method of a @CanonicalOverwrite may not be static, however in this case the target is static. This is not permissible.");
        } else if (!this.methodNode.desc.equals(target.method.desc)) {
            throw new IllegalStateException("Mismatch between descriptors of handler method ('" + this.methodNode.desc + "') versus target method ('" + target.method.desc + "')");
        }

        int retType = this.methodNode.desc.codePointBefore(this.methodNode.desc.length());
        int retOpcode;
        if (retType == 'V') {
            // no return
            retOpcode = Opcodes.RETURN;
        } else if (retType == ';') {
            // reference
            retOpcode = Opcodes.ARETURN;
        } else if (retType == 'Z' || retType == 'I' || retType == 'C' || retType == 'S') {
            // int-like
            retOpcode = Opcodes.IRETURN;
        } else if (retType == 'F') {
            // float
            retOpcode = Opcodes.FRETURN;
        } else if (retType == 'J') {
            // long
            retOpcode = Opcodes.LRETURN;
        } else {
            throw new IllegalStateException("Unknown return type: " + new String(new int[] {retType}, 0 , 1) + " (" + retType + " / 0x" + Integer.toHexString(retType) + ")");
        }

        Type[] args = Type.getArgumentTypes(this.methodNode.desc);

        target.method.instructions.clear();

        int localIdx;
        int invokeOpcode;
        if ((target.method.access & Opcodes.ACC_STATIC) == 0) {
            localIdx = 0;
        } else {
            localIdx = 1;
        }

        if (this.isStatic) {
            invokeOpcode = Opcodes.INVOKESTATIC;
        } else {
            target.method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            invokeOpcode = Opcodes.INVOKEVIRTUAL;
        }

        for (Type arg : args) {
            target.method.instructions.add(new VarInsnNode(arg.getOpcode(Opcodes.ILOAD), localIdx));
            localIdx += arg.getSize();
        }

        target.method.instructions.add(new MethodInsnNode(invokeOpcode, target.classNode.name, this.methodNode.name, this.methodNode.desc));
        target.method.instructions.add(new InsnNode(retOpcode));
        this.info.addCallbackInvocation(this.methodNode);

        boolean shadowedMethodAlreadyPresent = false;
        for (MethodNode targetSiblingMethod : target.classNode.methods) {
            if (targetSiblingMethod.name.equals(this.info.getMethodName()) && this.info.getMethod().desc.equals(targetSiblingMethod.desc)) {
                shadowedMethodAlreadyPresent = true;
                break;
            }
        }

        if (!shadowedMethodAlreadyPresent) {
            MethodNode shadowedMethod = new MethodNode(Opcodes.ACC_PUBLIC, this.info.getMethodName(), this.info.getMethod().desc, null, this.info.getMethod().exceptions.toArray(new String[0]));
            Map<LabelNode, LabelNode> labelClones = Bytecode.cloneLabels(target.method.instructions);
            AbstractInsnNode insn = target.method.instructions.getFirst();
            while (insn != null) {
                shadowedMethod.instructions.add(insn.clone(labelClones));
                insn = insn.getNext();
            }
            target.classNode.methods.add(shadowedMethod);
        }
    }
}
