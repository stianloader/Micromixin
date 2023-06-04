package de.geolykt.micromixin.internal.util.locals;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.SimpleVerifier;

import de.geolykt.micromixin.supertypes.ClassWrapper;
import de.geolykt.micromixin.supertypes.ClassWrapperPool;

class MicromixinVerifier extends SimpleVerifier {

    @NotNull
    private final ClassWrapperPool pool;

    public MicromixinVerifier(@NotNull ClassWrapperPool pool) {
        super(Opcodes.ASM9, null, null, null, false);
        this.pool = pool;
        setClassLoader(null);
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
        if (expectedType.getSort() == Type.ARRAY || expectedType.getSort() == Type.OBJECT) {
            if (type.equals(BasicInterpreter.NULL_TYPE)) {
                return true;
            }
            if (expectedType.getSort() == Type.ARRAY || expectedType.getSort() == Type.OBJECT) {
                return this.isAssignableFrom(expectedType, type)
                        || (this.pool.get(expectedType.getInternalName()).isInterface()
                                && this.pool.canAssign(this.pool.get("java/lang/Object"), this.pool.get(type.getInternalName())));
            } else {
                return false;
            }
        } else {
            return type.equals(expectedType);
        }
    }
}
