package org.stianloader.micromixin.transform.internal.util.smap;

import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class LineSection extends AbstractSMAPSection {

    public static class LineInfo {

        public final int inputLineCount;
        // Technically only "inputStartLine" and "outputStartLine" are required - but we are not lazy people.
        // Outside of that, the performance implications (at least memory-wise) would be significant
        public final int inputStartLine;
        public final int lineFileId;
        public final int outputLineIncrement;
        public final int outputStartLine;

        public LineInfo(int inputStartLine, int lineFileId, int inputLineCount, int outputLineIncrement, int outputStartLine) {
            this.inputStartLine = inputStartLine;
            this.lineFileId = lineFileId;
            this.inputLineCount = inputLineCount;
            this.outputLineIncrement = outputLineIncrement;
            this.outputStartLine = outputStartLine;
        }

        @NotNull
        @Contract(mutates = "param1", pure = false, value = "!null -> param1; null -> fail")
        public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
            sharedBuilder.append(this.inputStartLine).appendCodePoint('#').append(this.lineFileId).appendCodePoint(',');
            sharedBuilder.append(this.inputLineCount).appendCodePoint(':').append(this.outputStartLine).appendCodePoint(',');
            sharedBuilder.append(this.outputLineIncrement).appendCodePoint('\r');
            return sharedBuilder;
        }
    }

    @NotNull
    private final List<LineInfo> lineInfos;

    public LineSection(@NotNull List<LineInfo> lineInfos) {
        this.lineInfos = lineInfos;
    }

    @NotNull
    @Contract(pure = true)
    public List<LineInfo> getLineInfos() {
        return this.lineInfos;
    }

    @Override
    @NotNull
    public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
        sharedBuilder.append("*L\r");
        for (LineInfo info : this.lineInfos) {
            info.pushContents(sharedBuilder);
        }
        return sharedBuilder;
    }

}
