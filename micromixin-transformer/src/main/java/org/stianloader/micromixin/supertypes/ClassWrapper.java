package org.stianloader.micromixin.supertypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClassWrapper {
    private final boolean itf;
    @NotNull
    private final String name;
    @NotNull
    private final ClassWrapperPool pool;
    @NotNull
    private final String[] superInterfaces;
    @Nullable
    private final String superName;

    private Set<String> allInterfacesCache;

    public ClassWrapper(@NotNull String name, @Nullable String superName, @NotNull String[] superInterfaces, boolean isInterface, @NotNull ClassWrapperPool pool) {
        this.name = name;
        this.pool = pool;
        if (name.equals("java/lang/Object")) {
            this.itf = false;
            this.superInterfaces = new String[0];
            this.superName = null;
        } else {
            this.superName = superName;
            this.superInterfaces = superInterfaces;
            this.itf = isInterface;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassWrapper) {
            return ((ClassWrapper) obj).getName().equals(this.getName());
        }
        return false;
    }

    /**
     * Obtains all interfaces this class implements.
     * This includes interfaces superclasses have implemented or super-interfaces of interfaces.
     *
     * <p>If this class is an interface, it also includes this class.
     *
     * @return A set of all interfaces implemented by this class or it's supers
     */
    @SuppressWarnings("null") // In Java 6 there are 
    @NotNull
    public Set<String> getAllImplementatingInterfaces() {
        Set<String> allInterfacesCache = this.allInterfacesCache;
        if (allInterfacesCache == null) {
            String superName = this.superName;
            if (superName == null) {
                // Probably java/lang/Object
                this.allInterfacesCache = allInterfacesCache = Collections.emptySet();
                return allInterfacesCache;
            }

            allInterfacesCache = new HashSet<String>();
            for (String interfaceName : getSuperInterfacesName()) {
                allInterfacesCache.addAll(pool.get(interfaceName).getAllImplementatingInterfaces());
            }

            if (itf) {
                allInterfacesCache.add(name);
            } else {
                allInterfacesCache.addAll(pool.get(superName).getAllImplementatingInterfaces());
            }
        }
        return allInterfacesCache;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getSuper() {
        return superName;
    }

    @NotNull
    public String[] getSuperInterfacesName() {
        return superInterfaces;
    }

    public ClassWrapper getSuperWrapper() {
        String superName = getSuper();
        if (superName == null) {
            throw new IllegalStateException(name + " does not have a super type.");
        }
        return pool.get(superName);
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public boolean isInterface() {
        return itf;
    }

    @Override
    public String toString() {
        return String.format("ClassWrapper[name=%s, itf=%b, extends=%s, implements=%s]", name, itf, superName, Arrays.toString(superInterfaces));
    }
}
