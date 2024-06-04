package org.stianloader.micromixin.transform.internal.util.locals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class LocalsCapture {

    @NotNull
    public static LocalCaptureResult captureLocals(@NotNull ClassNode owner, @NotNull MethodNode method, @NotNull AbstractInsnNode inspectionTarget, @NotNull ClassWrapperPool pool) {
        Analyzer<BasicValue> analyzer = new Analyzer<BasicValue>(new MicromixinVerifier(pool));
        try {
            Frame<BasicValue>[] frames = analyzer.analyzeAndComputeMaxs(owner.name, method);
            return new LocalCaptureResult(owner, method, getFrameAt(frames, method, inspectionTarget), frames);
        } catch (AnalyzerException e) {
            return new LocalCaptureResult(owner, method, e);
        } catch (RuntimeException e) {
            return new LocalCaptureResult(owner, method, e);
        }
    }

    @Nullable
    private static <T extends Value> Frame<T> getFrameAt(Frame<T>[] frames, @NotNull MethodNode method, @NotNull AbstractInsnNode inspectionTarget) {
        int index = 0;
        for (AbstractInsnNode insn = method.instructions.getFirst(); insn != inspectionTarget; insn = insn.getNext()) {
            if (insn instanceof FrameNode) {
                // TODO are frame nodes counted or no?
                continue;
            }
            index++;
        }
        return frames[index];
    }

    @NotNull
    public static Map<AbstractInsnNode, LocalCaptureResult> multiCaptureLocals(@NotNull ClassNode owner, @NotNull MethodNode method, @NotNull List<AbstractInsnNode> inspectionTargets, @NotNull ClassWrapperPool pool) {
        Analyzer<BasicValue> analyzer = new Analyzer<BasicValue>(new MicromixinVerifier(pool));
        Map<AbstractInsnNode, LocalCaptureResult> result = new HashMap<AbstractInsnNode, LocalCaptureResult>();

        try {
            Frame<BasicValue>[] frames = analyzer.analyzeAndComputeMaxs(owner.name, method);
            for (AbstractInsnNode target : inspectionTargets) {
                result.put(target, new LocalCaptureResult(owner, method, getFrameAt(frames, method, Objects.requireNonNull(target, "An element in the `inspectionTargets` list is null, but that is not permitted.")), frames));
            }
            return result;
        } catch (AnalyzerException e) {
            LocalCaptureResult error = new LocalCaptureResult(owner, method, e);
            for (AbstractInsnNode insn : inspectionTargets) {
                result.put(insn, error);
            }
            return result;
        } catch (RuntimeException e) {
            LocalCaptureResult error = new LocalCaptureResult(owner, method, e);
            for (AbstractInsnNode insn : inspectionTargets) {
                if (result.containsKey(insn)) {
                    continue;
                }
                result.put(insn, error);
            }
            return result;
        }
    }
}
