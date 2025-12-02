package org.stianloader.micromixin.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.stianloader.micromixin.test.util.MapBytecodeProvider;
import org.stianloader.micromixin.transform.api.MixinConfig;
import org.stianloader.micromixin.transform.api.MixinConfig.InvalidMixinConfigException;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;

// Dummy class. Not of importance really; Used to debug when micromixin breaks
public class IntegrationTest1 {
    public void perform() throws IOException, InvalidMixinConfigException {
        InputStream is = IntegrationTest1.class.getResourceAsStream("/TestClass.class_");
        InputStream is2 = IntegrationTest1.class.getResourceAsStream("/TestMixins.class_");
        ClassReader cr = new ClassReader(is);
        ClassReader cr2 = new ClassReader(is2);
        ClassNode node = new ClassNode();
        ClassNode node2 = new ClassNode();
        cr.accept(node, 0);
        cr2.accept(node2, 0);
        String originName = node2.name;
        node2.name = "pkg/" + originName;

        Map<String, ClassNode> nodes = new HashMap<String, ClassNode>();
        nodes.put(node2.name, node2);
        MapBytecodeProvider<Void> bytecodeProvider = new MapBytecodeProvider<Void>(nodes);
        ClassWrapperPool pool = new ClassWrapperPool().addProvider(bytecodeProvider);
        MixinTransformer<Void> transformer = new MixinTransformer<Void>(bytecodeProvider, pool);

        JSONObject config = new JSONObject();
        config.put("required", true);
        config.put("package", "pkg");
        config.put("mixins", new JSONArray().put(originName));

        transformer.addMixin(null, MixinConfig.fromJson(config));

        transformer.transform(node, null);

        CheckClassAdapter cca = new CheckClassAdapter(null);
        node.accept(cca);

        TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.out));
        node.accept(tcv);
    }

    public static void main(String[] args) throws Throwable {
        new IntegrationTest1().perform();
    }
}
