package org.stianloader.micromixin.backports;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.points.MethodHead;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.util.Annotations;
import org.stianloader.micromixin.annotations.CanonicalOverwrite;

@InjectionInfo.AnnotationType(CanonicalOverwrite.class)
@InjectionInfo.HandlerPrefix("canonicalOverwriteHandler")
public class CanonicalOverwriteInjectionInfo extends InjectionInfo {

    protected CanonicalOverwriteInjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
        super(mixin, method, annotation, null);
    }

    @Override
    protected Injector parseInjector(AnnotationNode injectAnnotation) {
        return new CanonicalOverwriteInjector(this);
    }

    @Override
    protected void parseSelectors() {
        // Override to alter behaviour when not explicitly specifying a target method.

        Set<ITargetSelector> selectors = new LinkedHashSet<ITargetSelector>();
        TargetSelector.parse(Annotations.<String>getValue(this.annotation, "method", false), this, selectors);
        TargetSelector.parse(Annotations.<AnnotationNode>getValue(this.annotation, "target", false), this, selectors);

        if (selectors.size() == 0) {
            // Bit cursed for a solution, hopefully works (TM)
            selectors.add(new MemberInfo(this.method.name, "L" + this.mixin.getTargetClassNode().name + ";", this.method.desc));
        }

        this.targets.parse(selectors);
    }

    @Override
    protected void parseInjectionPoints(List<AnnotationNode> ats) {
        InjectionPointData injectData = new InjectionPointData(
                this, // context
                "CanonicalOverwrite__synthetic__at", // at
                Collections.<String>emptyList(), // arguments
                "", // target
                "", // slice
                -1, // ordinal
                0, // opcode
                "", // id
                0 // flags
        );
        this.injectionPoints.add(new MethodHead(injectData));
    }

    @Override
    protected void readInjectionPoints() {
        // Override to avoid exception
    }

    @Override
    protected void parseRequirements() {
        Annotations.setValue(this.annotation, "require", 1);
        Annotations.setValue(this.annotation, "allow", 1);
        super.parseRequirements();
    }
}
