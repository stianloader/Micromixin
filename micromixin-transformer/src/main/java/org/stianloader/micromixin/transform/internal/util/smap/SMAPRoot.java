package org.stianloader.micromixin.transform.internal.util.smap;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

/**
 * The root of the SMAP attribute
 */
public class SMAPRoot {

    @NotNull
    private final String generatedFileName; // node.debugSource

    @NotNull
    private final String defaultStratum; // "Mixin"

    @NotNull
    private final List<AbstractSMAPSection> sections = new ArrayList<AbstractSMAPSection>();

    public SMAPRoot(@NotNull String generatedFileName, @NotNull String defaultStratum) {
        this.generatedFileName = generatedFileName;
        this.defaultStratum = defaultStratum;
    }


    @Override
    public String toString() {
        return this.pushContents(new StringBuilder()).toString();
    }

    public void appendStratum(@NotNull String stratum, @NotNull FileSection fileSection,
            @NotNull LineSection lineSection, @Nullable String vendorId) {
        VendorSection vendorSect = vendorId == null ? null : new VendorSection(vendorId);
        this.appendStratum(new StratumSection(stratum), fileSection, lineSection, vendorSect);
    }

    public void appendStratum(@NotNull StratumSection stratumSection, @NotNull FileSection fileSection,
            @NotNull LineSection lineSection, @Nullable VendorSection vendorSection) {
        this.sections.add(stratumSection);
        this.sections.add(fileSection);
        this.sections.add(lineSection);
        if (vendorSection != null) {
            this.sections.add(vendorSection);
        }
    }

    @NotNull
    @Contract(mutates = "param1", pure = false, value = "!null -> param1; null -> fail")
    public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
        sharedBuilder.append("SMAP\r").append(this.generatedFileName).appendCodePoint('\r').append(this.defaultStratum).appendCodePoint('\r');
        for (AbstractSMAPSection section : this.sections) {
            section.pushContents(sharedBuilder);
        }
        sharedBuilder.append("*E\r");
        return sharedBuilder;
    }

    @NotNull
    @Contract(mutates = "param1, param2", pure = false, value = "!null, !null -> this; null, _ -> fail; _, null -> fail")
    public SMAPRoot applyTo(@NotNull ClassNode node, @NotNull StringBuilder builder) {
        builder.setLength(0);
        // FIXME do not overwrite the `sourceDebug` attribute
        node.sourceDebug = this.pushContents(builder).toString();
        return this;
    }
}
