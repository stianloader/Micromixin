package org.stianloader.micromixin.transform.api.supertypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class ClassWrapperPool {

    @NotNull
    private final List<ClassWrapperProvider> providers;
    protected final Map<String, ClassWrapper> wrappers;

    public ClassWrapperPool() {
        this(new ArrayList<ClassWrapperProvider>());
    }

    public ClassWrapperPool(@NotNull List<ClassWrapperProvider> providers) {
        this.wrappers = new HashMap<String, ClassWrapper>();
        this.wrappers.put("java/lang/Object", new ClassWrapper("java/lang/Object", null, new String[0], false, this));
        this.providers = providers;
    }

    @NotNull
    @Contract(mutates = "this", pure = false, value = "null -> fail; !null -> this")
    public ClassWrapperPool addProvider(@NotNull ClassWrapperProvider provider) {
        this.providers.add(Objects.requireNonNull(provider));
        return this;
    }

    public boolean canAssign(ClassWrapper superType, ClassWrapper subType) {
        final String name = superType.getName();
        if (superType.isInterface()) {
            return isImplementingInterface(subType, name);
        } else {
            while (subType != null) {
                if (name.equals(subType.getName()) || name.equals(subType.getSuper())) {
                    return true;
                }
                if (subType.getName().equals("java/lang/Object")) {
                    return false;
                }
                subType = subType.getSuperWrapper();
            }
        }
        return false;
    }

    @NotNull
    public ClassWrapper get(@NotNull String className) {
        ClassWrapper wrapper = optGet(className);
        if (wrapper != null) {
            return wrapper;
        }
        throw new IllegalStateException("Wrapper for class not found: " + className);
    }

    public ClassWrapper getCommonSuperClass(ClassWrapper class1, ClassWrapper class2) {
        if (class1.getName().equals("java/lang/Object")) {
            return class1;
        }
        if (class2.getName().equals("java/lang/Object")) {
            return class2;
        }
        // isAssignableFrom = class1 = class2;
        if (canAssign(class1, class2)) {
            return class1;
        }
        if (canAssign(class2, class1)) {
            return class2;
        }
        if (class1.isInterface() || class2.isInterface()) {
            return get("java/lang/Object");
        }
        return getCommonSuperClass(class1, get(Objects.requireNonNull(class2.getSuper())));
    }

    /**
     * Invalidate internal {@link ClassNode} {@link ClassNode#name name} caches.
     * Should be invoked when for example class nodes are remapped, at which point
     * internal caches are no longer valid.
     */
    public void invalidateNameCaches() {
        this.wrappers.clear();
        this.wrappers.put("java/lang/Object", new ClassWrapper("java/lang/Object", null, new String[0], false, this));
    }

    public boolean isImplementingInterface(ClassWrapper clazz, String interfaceName) {
        if (clazz.getName().equals("java/lang/Object")) {
            return false;
        }
        for (String interfaces : clazz.getSuperInterfacesName()) {
            if (interfaces.equals(interfaceName)) {
                return true;
            } else {
                if (isImplementingInterface(get(interfaces), interfaceName)) {
                    return true;
                }
            }
        }
        if (clazz.isInterface()) {
            return false;
        }
        return isImplementingInterface(clazz.getSuperWrapper(), interfaceName);
    }

    @Nullable
    public ClassWrapper optGet(@NotNull String className) {
        ClassWrapper wrapper = this.wrappers.get(className);
        if (wrapper != null) {
            return wrapper;
        }
        for (ClassWrapperProvider provider : this.providers) {
            wrapper = provider.provide(className, this);
            if (wrapper == null) {
                continue;
            }
            this.wrappers.put(className, wrapper);
            return wrapper;
        }
        return null;
    }
}
