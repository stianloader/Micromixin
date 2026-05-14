package org.stianloader.micromixin.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.BytecodeProvider;
import org.stianloader.micromixin.transform.api.MixinConfig;
import org.stianloader.micromixin.transform.api.MixinConfig.InvalidMixinConfigException;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;

public class SelfmodificationTest {

    @Test
    public void testForbiddenSelfmodification() throws InvalidMixinConfigException {
        BytecodeProvider<Void> bcProvider = new BytecodeProvider<Void>() {
            @Override
            @NotNull
            public ClassNode getClassNode(Void modularityAttachment, @NotNull String internalName) throws ClassNotFoundException {
                if (internalName.equals("x/MixinTestClazz")) {
                    ClassNode node = new ClassNode();
                    node.visit(Opcodes.V1_8, 0, internalName, null, "java/lang/Object", null);
                    AnnotationNode annotation = new AnnotationNode("Lorg/spongepowered/asm/mixin/Mixin;");
                    List<Object> annotValues = new ArrayList<Object>();
                    annotation.values = annotValues;
                    annotValues.add("value");
                    annotValues.add(Arrays.asList(Type.getObjectType(internalName)));
                    node.invisibleAnnotations = new ArrayList<AnnotationNode>();
                    node.invisibleAnnotations.add(annotation);

                    return node;
                }

                throw new AssertionError(internalName);
            }
        };

        ClassWrapperPool cwPool = new ClassWrapperPool();

        MixinTransformer<Void> mt = new MixinTransformer<Void>(bcProvider, cwPool);

        try {
            mt.addMixin(null, MixinConfig.fromString("{\"package\": \"x\", \"mixins\": [\"MixinTestClazz\"]}"));

            throw new AssertionError("Expected test failure");
        } catch (IllegalStateException e) {
            Throwable cause = e.getCause();

            if (cause != null && cause.getMessage().startsWith("The mixin class x/MixinTestClazz is attempting to transform itself.")) {
                return;
            }
    
            throw e;
        }
    }
}
