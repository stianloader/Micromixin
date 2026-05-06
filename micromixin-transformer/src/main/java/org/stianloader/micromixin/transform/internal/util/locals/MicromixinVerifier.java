package org.stianloader.micromixin.transform.internal.util.locals;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapper;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;
import org.stianloader.micromixin.transform.internal.util.Objects;

class MicromixinVerifier extends SimpleVerifier {

    @NotNull
    private final ClassWrapperPool pool;

    public MicromixinVerifier(@NotNull ClassWrapperPool pool) {
        super(Opcodes.ASM9, null, null, null, false);
        this.pool = pool;
        this.setClassLoader(null);
    }

    @Override
    protected Class<?> getClass(Type type) {
        throw new AssertionError("This method should not get called.");
    }

    @Override
    protected boolean isAssignableFrom(Type type1, Type type2) {
        if (type1.equals(type2)) {
            return true;
        }

        ClassWrapper wrapper1 = this.pool.get(type1.getInternalName());
        ClassWrapper wrapper2 = this.pool.get(type2.getInternalName());
        return this.pool.canAssign(wrapper1, wrapper2);
    }

    @Override
    protected boolean isInterface(Type type) {
        return this.pool.get(type.getInternalName()).isInterface();
    }

    @Override
    protected Type getSuperClass(Type type) {
        return Type.getType(this.pool.get(type.getInternalName()).getSuper());
    }

    @Override
    protected boolean isSubTypeOf(BasicValue value, BasicValue expected) {
        Type expectedType = expected.getType();
        Type type = value.getType();

        if (type.getSort() == Type.ARRAY) {
            if (expectedType.getSort() == Type.OBJECT) {
                // Arrays are objects, too.
                return expectedType.getInternalName().equals("java/lang/Object");
            } else if (expectedType.getDimensions() != type.getDimensions()) {
                return false; // Arrays can only be assigned to each other when they have the same dimension
            } else {
                expectedType = expectedType.getElementType();
                type = type.getElementType();
                // Go with normal compare logic now
            }
        }

        if (expectedType.getSort() == Type.OBJECT) {
            if (type.equals(BasicInterpreter.NULL_TYPE)) {
                return true;
            }

            if (type.getSort() == Type.OBJECT) {
                if (this.isAssignableFrom(expectedType, type)) {
                    return true;
                }

                if (this.isInterface(expectedType)) {
                    // edge case logic concerning interfaces is due to type merging
                    ClassWrapper wrapperJLO = this.pool.get("java/lang/Object");
                    return this.pool.canAssign(wrapperJLO, this.pool.get(Objects.requireNonNull(type.getInternalName())));
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return type.equals(expectedType);
        }
    }
}
