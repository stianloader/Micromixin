package org.stianloader.micromixin.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.stianloader.micromixin.test.util.MapBytecodeProvider;
import org.stianloader.micromixin.transform.api.MixinConfig;
import org.stianloader.micromixin.transform.api.MixinConfig.InvalidMixinConfigException;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class RedirectTest {

    private static final String AT_ANNOT = "Lorg/spongepowered/asm/mixin/injection/At;";
    private static final String GENERATED_PACKAGE_PATH_MIXINS = "org/stianloader/micromixin/transformer/test/generated/mixins";
    private static final String GENERATED_PACKAGE_PATH_TARGET = "org/stianloader/micromixin/transformer/test/generated/target";
    private static final String MIXIN_ANNOT = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String REDIRECT_ANNOT = "Lorg/spongepowered/asm/mixin/injection/Redirect;";

    @Test
    public void testArgumentCapture() {
        Map<String, ClassNode> nodes = new HashMap<String, ClassNode>();
        MapBytecodeProvider<Void> bytecodeProvider = new MapBytecodeProvider<Void>(nodes);
        ClassWrapperPool pool = new ClassWrapperPool().addProvider(bytecodeProvider);
        MixinTransformer<Void> transformer = new MixinTransformer<Void>(bytecodeProvider, pool);

        JSONObject config = new JSONObject();
        config.put("required", true);
        config.put("package", RedirectTest.GENERATED_PACKAGE_PATH_MIXINS);
        config.put("mixins", new JSONArray().put("RedirectArgumentCaptureMixin"));

        {
            ClassNode node = new ClassNode();
            node.name = RedirectTest.GENERATED_PACKAGE_PATH_MIXINS + "/RedirectArgumentCaptureMixin";
            node.superName = "java/lang/Object";
            AnnotationNode mixinAnnot = new AnnotationNode(RedirectTest.MIXIN_ANNOT);
            List<Type> targets = Arrays.asList(Type.getType("L" + RedirectTest.GENERATED_PACKAGE_PATH_TARGET + "/RedirectArgumentCapture;"));
            mixinAnnot.values = Arrays.asList("value", targets);
            node.invisibleAnnotations = Arrays.asList(mixinAnnot);

            {
                MethodNode method = new MethodNode();
                method.name = "redirectHandler0";
                method.desc = "(II)V";
                method.instructions.add(new InsnNode(Opcodes.RETURN));
                AnnotationNode redirectAnnot = new AnnotationNode(RedirectTest.REDIRECT_ANNOT);
                AnnotationNode atValue = new AnnotationNode(RedirectTest.AT_ANNOT);
                atValue.values = Arrays.<Object>asList("value", "INVOKE", "target", "sinkValue");
                redirectAnnot.values = Arrays.asList("method", Arrays.asList("target0"), "at", atValue, "require", 1);
                method.visibleAnnotations = Arrays.asList(redirectAnnot);
                node.methods.add(method);
            }

            {
                MethodNode method = new MethodNode();
                method.name = "redirectHandler1";
                method.desc = "(IJCIJCDILjava/lang/Object;J)V";
                method.instructions.add(new InsnNode(Opcodes.RETURN));
                AnnotationNode redirectAnnot = new AnnotationNode(RedirectTest.REDIRECT_ANNOT);
                AnnotationNode atValue = new AnnotationNode(RedirectTest.AT_ANNOT);
                atValue.values = Arrays.<Object>asList("value", "INVOKE", "target", "sinkValues");
                redirectAnnot.values = Arrays.asList("method", Arrays.asList("target1"), "at", atValue, "require", 1);
                method.visibleAnnotations = Arrays.asList(redirectAnnot);
                node.methods.add(method);
            }

            nodes.put(node.name, node);
        }

        {
            ClassNode node = new ClassNode();
            node.name = RedirectTest.GENERATED_PACKAGE_PATH_TARGET + "/RedirectArgumentCapture";
            node.superName = "java/lang/Object";

            {
                MethodNode method = new MethodNode();
                method.name = "target0";
                method.desc = "(I)V";
                method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummySink", "sinkValue", "(I)V"));
                method.instructions.add(new InsnNode(Opcodes.RETURN));
                node.methods.add(method);
            }

            {
                MethodNode method = new MethodNode();
                method.name = "target1";
                method.desc = "(IJCDILjava/lang/Object;JF)V";
                method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                method.instructions.add(new VarInsnNode(Opcodes.LLOAD, 2));
                method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummySink", "sinkValues", "(IJC)V"));
                method.instructions.add(new InsnNode(Opcodes.RETURN));
                node.methods.add(method);
            }

            nodes.put(node.name, node);
        }

        for (ClassNode node : nodes.values()) {
            CheckClassAdapter checker = new CheckClassAdapter(null);
            node.accept(checker);
        }

        try {
            transformer.addMixin(null, MixinConfig.fromJson(config));
        } catch (InvalidMixinConfigException e) {
            throw new AssertionError(e);
        }

        {
            ClassNode node = nodes.get(RedirectTest.GENERATED_PACKAGE_PATH_TARGET + "/RedirectArgumentCapture");
            transformer.transform(Objects.requireNonNull(node));
            CheckClassAdapter checker = new CheckClassAdapter(null);
            node.accept(checker);
        }
    }
}
