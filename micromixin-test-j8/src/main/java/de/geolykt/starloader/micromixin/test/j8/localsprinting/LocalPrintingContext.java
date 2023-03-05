package de.geolykt.starloader.micromixin.test.j8.localsprinting;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

public class LocalPrintingContext {

    @NotNull
    public final String targetClass;

    @NotNull
    public final String targetMethod;

    @NotNull
    public final String targetMaxLocals;

    @NotNull
    public final String initialFrameSize;

    @NotNull
    public final String callbackName;

    @NotNull
    public final String instruction;

    public final String[][] localsTable;

    public LocalPrintingContext(@NotNull String targetClass, @NotNull String targetMethod, @NotNull String targetMaxLocals,
            @NotNull String initialFrameSize, @NotNull String callbackName, @NotNull String instruction,
            String[][] localsTable) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.targetMaxLocals = targetMaxLocals;
        this.initialFrameSize = initialFrameSize;
        this.callbackName = callbackName;
        this.instruction = instruction;
        this.localsTable = localsTable;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LocalPrintingContext)) {
            return false;
        }
        LocalPrintingContext other = (LocalPrintingContext) obj;
        return this.callbackName.equals(other.callbackName)
                && this.initialFrameSize.equals(other.initialFrameSize)
                && this.instruction.equals(other.instruction)
                && Arrays.deepEquals(this.localsTable, other.localsTable)
                && this.targetClass.equals(other.targetClass)
                && this.targetMaxLocals.equals(other.targetMaxLocals)
                && this.targetMethod.equals(other.targetMethod);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new String[] {
                this.callbackName,
                this.initialFrameSize,
                this.instruction,
                this.targetClass,
                this.targetMaxLocals,
                this.targetMethod
        }) ^ Arrays.deepHashCode(this.localsTable);
    }
}
