package org.stianloader.micromixin.transform.internal.util.smap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.internal.DefaultMixinLogger;
import org.stianloader.micromixin.transform.internal.util.Atomics;

/**
 * The root of the SMAP attribute
 */
public class SMAPRoot {

    private static AbstractSMAPSection parseSection(@NotNull String contents, int seekStart, @NotNull AtomicInteger seekEndOut) {
        if (contents.charAt(seekStart) != '*') {
            throw new IllegalStateException("Unexpected character at index " + seekStart + ". Expected an asterisk, got '" + contents.charAt(seekStart) + "' instead.");
        }

        char type = contents.charAt(++seekStart);
        if (type == 'E') {
            Atomics.setPlain(seekEndOut, SMAPRoot.seekStartOfLine(contents, seekStart));
            return EndSection.INSTANCE;
        } else if (type == 'O') {
            throw new IllegalStateException("Micromixin-transform does not allow provide support for EmbeddedSourceMaps.");
        } else if (type == 'S') {
            // Scan to next asterisk
            int asterisk = contents.indexOf('*', seekStart);
            if (asterisk < 0) {
                throw new IllegalStateException("SMAP is not properly closed - that is, a section other than EndSection was opened but isn't being followed by another section.");
            }

            Atomics.setPlain(seekEndOut, asterisk);
            int seekIndex0 = SMAPRoot.seekStartOfLine(contents, seekStart);
            if (asterisk != seekIndex0) {
                throw new IllegalStateException("Malformed SMAP: StratumSection is not followed by a CR/CRLF/LF followed by an asterisk.");
            }

            String stratumId = contents.substring(seekStart + 1, SMAPRoot.seekEndOfLine(contents, seekIndex0));
            while (Character.isWhitespace(stratumId.charAt(0))) {
                stratumId = stratumId.substring(1);
            }

            return new StratumSection(stratumId);
        } else {
            // Scan to next asterisk
            int asterisk = contents.indexOf('*', seekStart);
            if (asterisk < 0) {
                throw new IllegalStateException("SMAP is not properly closed - that is, a section other than EndSection was opened but isn't being followed by another section.");
            }

            Atomics.setPlain(seekEndOut, asterisk);
            if (contents.charAt(asterisk - 1) != '\r' && contents.charAt(asterisk - 1) != '\n') {
                throw new IllegalStateException("SMAP section was improperly started: Asterisk is not preceeded by a carriage return or a line feed character");
            }

            int seekIndex0 = SMAPRoot.seekStartOfLine(contents, seekStart);

            List<String> futureInfos = new ArrayList<String>();

            while (seekIndex0 != asterisk) {
                int seekIndex1 = SMAPRoot.seekStartOfLine(contents, seekIndex0);
                futureInfos.add(contents.substring(seekIndex0, SMAPRoot.seekEndOfLine(contents, seekIndex1)));
                seekIndex0 = seekIndex1;
            }

            return new FutureSection(type, futureInfos);
        }
    }

    @NotNull
    @Contract(pure = true, value = "!null -> new; null -> fail")
    public static SMAPRoot parseSMAP(@NotNull String contents) {
        int seekIndex0 = SMAPRoot.seekStartOfLine(contents, 0);
        String header = contents.substring(0, SMAPRoot.seekEndOfLine(contents, seekIndex0));
        if (!header.equals("SMAP")) {
            throw new IllegalArgumentException("Input argument is not a valid SMAP definition: Missing header.");
        }

        int seekIndex1 = SMAPRoot.seekStartOfLine(contents, seekIndex0);
        String outputFileName = contents.substring(seekIndex0, SMAPRoot.seekEndOfLine(contents, seekIndex1));
        seekIndex1 = SMAPRoot.seekStartOfLine(contents, (seekIndex0 = seekIndex1));
        String defaultStratumId = contents.substring(seekIndex0, SMAPRoot.seekEndOfLine(contents, seekIndex1));
        seekIndex1 = SMAPRoot.seekStartOfLine(contents, seekIndex0);
        AtomicInteger seekEndOut = new AtomicInteger();
        SMAPRoot root = new SMAPRoot(outputFileName, defaultStratumId);

        while (true) {
            AbstractSMAPSection section = SMAPRoot.parseSection(contents, seekIndex1, seekEndOut);
            root.sections.add(section);
            if (section == EndSection.INSTANCE) {
                break;
            }
            seekIndex1 = Atomics.getPlain(seekEndOut);
        }

        return root;
    }

    private static int seekStartOfLine(@NotNull String contents, int seekStart) {
        int indexCR = contents.indexOf('\r', seekStart);
        int indexLF = contents.indexOf('\n', seekStart);
        if (indexLF >= 0 && ((indexLF < indexCR || indexCR < 0) || (indexCR == indexLF - 1))) {
            return indexLF + 1;
        } else if (indexCR >= 0) {
            return indexCR + 1;
        } else {
            throw new IllegalArgumentException("Missing CR/CRLF/LF, but the SMAP definition wasn't closed.");
        }
    }

    private static int seekEndOfLine(@NotNull String contents, int seekEnd) {
        if (contents.charAt(--seekEnd) == '\r') {
            return seekEnd;
        } else if (contents.charAt(seekEnd) == '\n') {
            return contents.charAt(seekEnd - 1) == '\r' ? seekEnd - 1 : seekEnd;
        } else {
            throw new IllegalStateException("'seekEnd' wasn't located at the start of a new line.");
        }
    }

    @NotNull
    private final String defaultStratum; // "Mixin"

    @NotNull
    private final String generatedFileName; // node.debugSource


    @NotNull
    private final List<AbstractSMAPSection> sections = new ArrayList<AbstractSMAPSection>();

    public SMAPRoot(@NotNull String generatedFileName, @NotNull String defaultStratum) {
        this.generatedFileName = generatedFileName;
        this.defaultStratum = defaultStratum;
    }

    public void appendStratum(@NotNull StratumSection stratumSection, @NotNull FileSection fileSection,
            @NotNull LineSection lineSection, @Nullable VendorSection vendorSection) {
        int insertIndex;
        if (this.sections.isEmpty() || this.sections.get(this.sections.size() - 1) != EndSection.INSTANCE) {
            insertIndex = this.sections.size();
        } else {
            insertIndex = this.sections.size() - 1;
        }

        this.sections.add(insertIndex, stratumSection);
        this.sections.add(insertIndex, fileSection);
        this.sections.add(insertIndex, lineSection);
        if (vendorSection != null) {
            this.sections.add(insertIndex, vendorSection);
        }
    }

    public void appendStratum(@NotNull String stratum, @NotNull FileSection fileSection,
            @NotNull LineSection lineSection, @Nullable String vendorId) {
        VendorSection vendorSect = vendorId == null ? null : new VendorSection(vendorId);
        this.appendStratum(new StratumSection(stratum), fileSection, lineSection, vendorSect);
    }

    @NotNull
    @Contract(mutates = "param1, param2", pure = false, value = "!null, !null -> this; null, _ -> fail; _, null -> fail")
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public SMAPRoot applyTo(@NotNull ClassNode node, @NotNull StringBuilder builder) {
        return this.applyTo(node, builder, new DefaultMixinLogger(false));
    }

    @NotNull
    @Contract(mutates = "param1, param2", pure = false, value = "!null, !null, !null -> this; null, _, _ -> fail; _, null, _ -> fail; _, _, null -> fail")
    public SMAPRoot applyTo(@NotNull ClassNode node, @NotNull StringBuilder builder, @NotNull MixinLoggingFacade logger) {
        builder.setLength(0);
        String sourceDebug = node.sourceDebug;
        if (sourceDebug == null) {
            node.sourceDebug = this.pushContents(builder).toString();
            return this;
        }

        try {
            SMAPRoot merged = SMAPRoot.parseSMAP(sourceDebug);
            int insertIndex;
            if (merged.sections.isEmpty() || merged.sections.get(merged.sections.size() - 1) != EndSection.INSTANCE) {
                insertIndex = merged.sections.size();
            } else {
                insertIndex = merged.sections.size() - 1;
            }
            merged.sections.addAll(insertIndex, this.sections);
            node.sourceDebug = merged.pushContents(builder).toString();
        } catch (RuntimeException e) {
            logger.warn(SMAPRoot.class, "Unable to merge SMAP sourceDebug attributes for class '{}', overwriting instead. The debugging experience might be compromised.", node.name, e);
            node.sourceDebug = this.pushContents(builder).toString();
        }

        return this;
    }

    @NotNull
    @Contract(mutates = "param1", pure = false, value = "!null -> param1; null -> fail")
    public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
        sharedBuilder.append("SMAP\r").append(this.generatedFileName).appendCodePoint('\r').append(this.defaultStratum).appendCodePoint('\r');
        for (AbstractSMAPSection section : this.sections) {
            section.pushContents(sharedBuilder);
        }

        if (this.sections.isEmpty() || this.sections.get(this.sections.size() - 1) != EndSection.INSTANCE) {
            EndSection.INSTANCE.pushContents(sharedBuilder);
        }

        return sharedBuilder;
    }

    @Override
    public String toString() {
        return this.pushContents(new StringBuilder()).toString();
    }
}
