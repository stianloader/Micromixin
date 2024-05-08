package org.stianloader.micromixin.transform.api.supertypes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReflectionClassWrapperProvider implements ClassWrapperProvider {

    private final ClassLoader loader;

    public ReflectionClassWrapperProvider(final ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    @Nullable
    public ClassWrapper provide(@NotNull String className, @NotNull ClassWrapperPool pool) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className.replace('/', '.'), false, loader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to resolve class: " + className, e);
        }
        boolean itf = clazz.isInterface();
        String superName;
        if (itf) {
            superName = "java/lang/Object";
        } else {
            superName = clazz.getSuperclass().getName().replace('.', '/');
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        String[] superInterfaces = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            superInterfaces[i] = interfaces[i].getName().replace('.', '/');
        }
        return new ClassWrapper(className, superName, superInterfaces, itf, pool);
    }
}
