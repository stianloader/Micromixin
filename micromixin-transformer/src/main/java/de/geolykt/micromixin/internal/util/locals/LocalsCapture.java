package de.geolykt.micromixin.internal.util.locals;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

import de.geolykt.micromixin.supertypes.ClassWrapperPool;

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
            index++;
        }
        return frames[index];
    }
}
