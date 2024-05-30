package org.stianloader.micromixin.transform.internal.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.InjectionPointSelector;
import org.stianloader.micromixin.transform.api.InjectionPointSelectorFactory;
import org.stianloader.micromixin.transform.api.InjectionPointTargetConstraint;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.selectors.DescSelector;
import org.stianloader.micromixin.transform.internal.selectors.StringSelector;
import org.stianloader.micromixin.transform.internal.selectors.constant.ClassConstantSelector;
import org.stianloader.micromixin.transform.internal.selectors.constant.DoubleConstantSelector;
import org.stianloader.micromixin.transform.internal.selectors.constant.FloatConstantSelector;
import org.stianloader.micromixin.transform.internal.selectors.constant.IntConstantSelector;
import org.stianloader.micromixin.transform.internal.selectors.constant.LongConstantSelector;
import org.stianloader.micromixin.transform.internal.selectors.constant.NullConstantSelector;
import org.stianloader.micromixin.transform.internal.selectors.constant.StringConstantSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.ConstantInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.HeadInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.TailInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class MixinAtAnnotation {

    @NotNull
    public static final MixinAtAnnotation HEAD = new MixinAtAnnotation("HEAD", HeadInjectionPointSelector.INSTANCE, "", 0, false);

    @NotNull
    public static final MixinAtAnnotation TAIL = new MixinAtAnnotation("TAIL", TailInjectionPointSelector.INSTANCE, "", 0, true);

    @NotNull
    public static List<SlicedInjectionPointSelector> bake(@NotNull List<MixinAtAnnotation> ats, @NotNull List<MixinSliceAnnotation> slices) throws MixinParseException {
        List<SlicedInjectionPointSelector> baked = new ArrayList<SlicedInjectionPointSelector>(ats.size());
        Map<String, List<MixinAtAnnotation>> dependents = new HashMap<String, List<MixinAtAnnotation>>();
        Map<String, MixinSliceAnnotation> name2slice = new HashMap<String, MixinSliceAnnotation>();

        for (MixinAtAnnotation at : ats) {
            List<MixinAtAnnotation> dependencies = dependents.get(at.slice);
            if (dependencies == null) {
                dependencies = new ArrayList<MixinAtAnnotation>();
                dependents.put(at.slice, dependencies);
            }
            dependencies.add(at);
        }

        // dependent -> dependencies
        Map<String, Set<String>> sliceDependenciesShallow = new HashMap<String, Set<String>>();

        // Check for duplicate slices
        for (MixinSliceAnnotation slice : slices) {
            if (name2slice.put(slice.id, slice) != null) {
                throw new MixinParseException("At least two slices with the id '" + slice.id + "' have been defined, but duplicate slice IDs are not permitted.");
            }

            Set<String> deps = new LinkedHashSet<String>();
            sliceDependenciesShallow.put(slice.id, deps);

            if (!slice.id.equals("") || !slice.from.slice.equals("")) {
                List<MixinAtAnnotation> dependencies = dependents.get(slice.from.slice);
                if (dependencies == null) {
                    dependencies = new ArrayList<MixinAtAnnotation>();
                    dependents.put(slice.from.slice, dependencies);
                }
                ats.add(slice.from);
                deps.add(slice.from.slice);
            }
            if (!slice.id.equals("") || !slice.to.slice.equals("")) {
                List<MixinAtAnnotation> dependencies = dependents.get(slice.to.slice);
                if (dependencies == null) {
                    dependencies = new ArrayList<MixinAtAnnotation>();
                    dependents.put(slice.to.slice, dependencies);
                }
                ats.add(slice.to);
                deps.add(slice.to.slice);
            }
        }

        if (!name2slice.containsKey("")) {
            name2slice.put("", new MixinSliceAnnotation("", MixinAtAnnotation.HEAD, MixinAtAnnotation.TAIL));
        }

        // Check for undefined slices
        for (Map.Entry<String, List<MixinAtAnnotation>> entry : dependents.entrySet()) {
            if (!name2slice.containsKey(entry.getKey())) {
                throw new MixinParseException("A slice with the id of '" + entry.getKey() + "' is requested but never defined. Is there a typo in your slice IDs?");
            }
        }

        // Check for circular references
        final Map<String, Map<String, String>> sliceDependenciesDeep = new HashMap<String, Map<String, String>>();
        {
            Set<String> stale = new HashSet<String>(sliceDependenciesShallow.keySet());
            Set<String> stale2 = new HashSet<String>();
            Map<String, String> subdeps2 = new HashMap<String, String>();
            while (!stale.isEmpty()) {

                for (String sliceId : stale) {
                    Map<String, String> subdeps = sliceDependenciesDeep.get(sliceId);

                    if (subdeps == null) {
                        subdeps = new HashMap<String, String>();
                        sliceDependenciesDeep.put(sliceId, subdeps);
                    }

                    for (String dep : subdeps.keySet()) {
                        Set<String> subsubdep = sliceDependenciesShallow.get(dep);
                        if (subsubdep.contains(sliceId)) {
                            String path = " '" + dep + "' -> '" + sliceId + "'";

                            String parent = subdeps.get(dep);
                            while (parent != null) {
                                path = " '" + parent + "' ->" + path;
                                parent = subdeps.get(dep);
                            }

                            throw new MixinParseException("Slice dependency circularity: Slice id '" + sliceId + "' directly or indirectly depends on itself. Dependency chain path:" + path + ". Please verify that all @Slice and @At annotations are properly set up. Note that multiple paths may exist, this noted path just happens to be one.");
                        }
                        for (String d : subsubdep) {
                            if (!subdeps.containsKey(d)) {
                                subdeps2.put(d, dep);
                            }
                        }
                    }

                    if (!subdeps2.isEmpty()) {
                        subdeps.putAll(subdeps2);
                        subdeps2.clear();
                        stale2.add(sliceId);
                    }
                }

                stale.clear();
                stale.addAll(stale2);
                stale2.clear();
            }
        }

        TreeSet<String> sliceEvaluationOrder = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // Evaluate shortest chains first, then go with lexicographical order
                int s1 = sliceDependenciesDeep.get(o1).size();
                int s2 = sliceDependenciesDeep.get(o2).size();
                if (s1 != s2) {
                    return s2 - s1;
                }
                return o1.compareTo(o2);
            }
        });
        sliceEvaluationOrder.addAll(sliceDependenciesDeep.keySet());

        Set<String> evaluatedSlices = new HashSet<String>();
        Map<String, SlicedInjectionPointSelector> sliceFrom = new HashMap<String, SlicedInjectionPointSelector>();
        Map<String, SlicedInjectionPointSelector> sliceTo = new HashMap<String, SlicedInjectionPointSelector>();

        for (String slice : sliceEvaluationOrder) {
            evaluatedSlices.add(slice);
            // Sanity test, can be freely committed out in prod (though I'll probably won't for sanity reasons)
            if (!evaluatedSlices.containsAll(sliceDependenciesDeep.get(slice).keySet())) {
                throw new IllegalStateException("Slice " + slice + " depends on non-evaluated slices. This is an internal algorithmic error - please report this issue to the micromixin maintainers.");
            }

            MixinSliceAnnotation sliceAnnot = name2slice.get(slice);
            SlicedInjectionPointSelector from = null;
            SlicedInjectionPointSelector to = null;
            if (sliceAnnot.from != MixinAtAnnotation.HEAD) {
                SlicedInjectionPointSelector fromFrom = sliceFrom.get(sliceAnnot.from.slice);
                SlicedInjectionPointSelector fromTo = sliceTo.get(sliceAnnot.from.slice);
                from = new SlicedInjectionPointSelector(sliceAnnot.from.injectionPointSelector, fromFrom, fromTo, sliceAnnot.from.shift, sliceAnnot.from.unsafe);
            }
            if (sliceAnnot.to != MixinAtAnnotation.TAIL) {
                SlicedInjectionPointSelector toFrom = sliceFrom.get(sliceAnnot.to.slice);
                SlicedInjectionPointSelector toTo = sliceTo.get(sliceAnnot.to.slice);
                to = new SlicedInjectionPointSelector(sliceAnnot.to.injectionPointSelector, toFrom, toTo, sliceAnnot.to.shift, sliceAnnot.to.unsafe);
            }
            sliceFrom.put(slice, from);
            sliceTo.put(slice, to);
        }

        for (MixinAtAnnotation annot : ats) {
            SlicedInjectionPointSelector from = sliceFrom.get(annot.slice);
            SlicedInjectionPointSelector to = sliceTo.get(annot.slice);
            baked.add(new SlicedInjectionPointSelector(annot.injectionPointSelector, from, to, annot.shift, annot.unsafe));
        }

        return baked;
    }

    @NotNull
    public static SlicedInjectionPointSelector bake(@NotNull MixinAtAnnotation at, @Nullable MixinSliceAnnotation slice) {
        if (slice == null) {
            return new SlicedInjectionPointSelector(at.injectionPointSelector, null, null, at.shift, at.unsafe);
        }

        SlicedInjectionPointSelector from = null;
        SlicedInjectionPointSelector to = null;

        if (slice.from != MixinAtAnnotation.HEAD) {
            from = new SlicedInjectionPointSelector(slice.from.injectionPointSelector, null, null, at.shift, at.unsafe);
        }

        if (slice.to != MixinAtAnnotation.TAIL) {
            to = new SlicedInjectionPointSelector(slice.to.injectionPointSelector, null, null, at.shift, at.unsafe);
        }

        return new SlicedInjectionPointSelector(at.injectionPointSelector, from, to, at.shift, at.unsafe);
    }

    @NotNull
    public static MixinAtAnnotation parse(@NotNull ClassNode mixinSource, @NotNull AnnotationNode atValue, @NotNull InjectionPointSelectorFactory factory) throws MixinParseException {
        String slice = "";
        String value = null;
        List<String> args = null;
        InjectionPointTargetConstraint constraint = null;
        int shift = 0;

        for (int i = 0; i < atValue.values.size(); i += 2) {
            String name = (String) atValue.values.get(i);
            Object val = atValue.values.get(i + 1);
            if (name.equals("value")) {
                value = ((String) val).toUpperCase(Locale.ROOT);
            } else if (name.equals("args")) {
                @SuppressWarnings("all")
                List<String> temp = (List<String>) val; // Temporary variable required to suppress all warnings caused by this "dangerous" cast
                args = temp;
            } else if (name.equals("target")) {
                constraint = new StringSelector(((String) Objects.requireNonNull(val)));
            } else if (name.equals("slice")) {
                slice = (String) Objects.requireNonNull(val);
            } else if (name.equals("desc")) {
                constraint = new DescSelector(MixinDescAnnotation.parse(mixinSource, Objects.requireNonNull((AnnotationNode) val)));
            } else if (name.equals("shift")) {
                String[] enumValue = (String[]) val;
                if (!enumValue[0].equals("Lorg/spongepowered/asm/mixin/injection/At$Shift;")) {
                    throw new MixinParseException("Unimplemented enum type for @At.shift: " + Arrays.toString(enumValue));
                }
                if (enumValue[1].equals("NONE")) {
                    shift = 0;
                } else if (enumValue[1].equals("BEFORE")){
                    shift = -1;
                } else if (enumValue[2].equals("AFTER")) {
                    shift = 1;
                } else {
                    throw new MixinParseException("Unimplemented enum value for @At.shift: " + Arrays.toString(enumValue));
                }
            } else {
                throw new MixinParseException("Unimplemented key in @At: " + name);
            }
        }
        if (value == null) {
            throw new MixinParseException("The required field \"value\" is missing.");
        }

        // TODO implement unsafe
        return new MixinAtAnnotation(value, factory.get(value).create(args, constraint), slice, shift, false);
    }

    @NotNull
    public static MixinAtAnnotation parseConstantAnnotation(@NotNull ClassNode mixinSource, @NotNull AnnotationNode constantValue) throws MixinParseException {
        // @Constant is quite similar to @At(value = "CONSTANT", ...) in structure and functionality,
        // so in order to prevent ourselves to duplicate the logic for slices, we plainly interpret any @Constant
        // annotations as @At(value = "CONSTANT", ...) expressions.
        // This approach doesn't actually completely work once we expand 0 condition constants,
        // but it is likely that the constant selector could be wrapped or expanded upon in order to support
        // these extensions.

        String slice = "";
        ConstantSelector selector = null;

        for (int i = 0; i < constantValue.values.size(); i += 2) {
            String name = (String) constantValue.values.get(i);
            Object val = constantValue.values.get(i + 1);
            if (name.equals("nullValue")) {
                if (selector != null) {
                    throw new MixinParseException("@Constant may not have multiple value selectors (this is likely a flaw of micromxin).");
                }
                if (((Boolean) val).booleanValue()) {
                    selector = NullConstantSelector.INSTANCE;
                } else {
                    throw new MixinParseException("@Constant may not have nullValue=false within the micromixin implementation (this is likely a flaw of micromxin).");
                }
            } else if (name.equals("intValue")) {
                if (selector != null) {
                    throw new MixinParseException("@Constant may not have multiple value selectors (this is likely a flaw of micromxin).");
                }
                selector = new IntConstantSelector(((Integer) val).intValue());
            } else if (name.equals("stringValue")) {
                if (selector != null) {
                    throw new MixinParseException("@Constant may not have multiple value selectors (this is likely a flaw of micromxin).");
                }
                selector = new StringConstantSelector(Objects.requireNonNull((String) val));
            } else if (name.equals("classValue")) {
                if (selector != null) {
                    throw new MixinParseException("@Constant may not have multiple value selectors (this is likely a flaw of micromxin).");
                }
                selector = new ClassConstantSelector(Objects.requireNonNull(((Type) val)).getDescriptor());
            } else if (name.equals("doubleValue")) {
                if (selector != null) {
                    throw new MixinParseException("@Constant may not have multiple value selectors (this is likely a flaw of micromxin).");
                }
                selector = new DoubleConstantSelector(((Double) val).doubleValue());
            } else if (name.equals("floatValue")) {
                if (selector != null) {
                    throw new MixinParseException("@Constant may not have multiple value selectors (this is likely a flaw of micromxin).");
                }
                selector = new FloatConstantSelector(((Float) val).floatValue());
            } else if (name.equals("longValue")) {
                if (selector != null) {
                    throw new MixinParseException("@Constant may not have multiple value selectors (this is likely a flaw of micromxin).");
                }
                selector = new LongConstantSelector((Long) val);
            } else if (name.equals("slice")) {
                slice = (String) Objects.requireNonNull(val);
            } else {
                throw new MixinParseException("Unimplemented key in @Constant: " + name);
            }
        }

        if (selector == null) {
            throw new MixinParseException("The @Constant annotation requires any of the following required fields: [nullValue, stringValue, intValue, classValue, doubleValue].");
        }

        // TODO @Constant, like @At has an ordinal.
        // However, this key and the underlying functionality has not yet been implemented in micromixin-transformer.
        return new MixinAtAnnotation("CONSTANT", new ConstantInjectionPointSelector(selector), slice, 0, false);
    }

    @NotNull
    public final InjectionPointSelector injectionPointSelector;
    private final int shift;
    @NotNull
    public final String slice;
    private final boolean unsafe;
    @NotNull
    public final String value;

    public MixinAtAnnotation(@NotNull String value, @NotNull InjectionPointSelector selector, @NotNull String slice, int shift, boolean unsafe) {
        this.value = value;
        this.injectionPointSelector = selector;
        this.slice = slice;
        this.shift = shift;
        this.unsafe = unsafe;
    }
}
