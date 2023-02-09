package de.geolykt.starloader.micromixin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.TraceClassVisitor;

import de.geolykt.micromixin.BytecodeProvider;
import de.geolykt.micromixin.MixinConfig;
import de.geolykt.micromixin.MixinConfig.InvalidMixinConfigException;
import de.geolykt.micromixin.MixinTransformer;

public class MixinMerger {

    public static byte[] readAllBytesEx(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] store = new byte[4096];
        for (int amount = in.read(store); amount != -1; amount = in.read(store)) {
            out.write(store, 0, amount);
        }
        return out.toByteArray();
    }

    public static byte[] readAllBytes(InputStream in) {
        Objects.requireNonNull(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] store = new byte[4096];
        try {
            for (int amount = in.read(store); amount != -1; amount = in.read(store)) {
                out.write(store, 0, amount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static byte[] readClass(String name) {
        InputStream in = MixinMerger.class.getClassLoader().getResourceAsStream(name.replace('.', '/') + ".class");
        if (in == null) {
            try {
                readAllBytes(Files.newInputStream(Paths.get("..", "micromixin-test-j8", "target", "classes", name.replace('.', '/') + ".class")));
            } catch (IOException e) {
                throw new IllegalStateException("Class does not exist: " + name + " in classloader " + MixinMerger.class.getClassLoader() + ". Furthermore a manual lookup was not possible.", e);
            }
        }
        return readAllBytes(in);
    }

    @SuppressWarnings("null")
    public static void tryTransform(MixinTransformer<Void> micromixinTransformer, String name) {
        byte[] raw = readClass(name);
        byte[] reimpl;
        try {
            ClassNode cnmm = new ClassNode();
            new ClassReader(raw).accept(cnmm, 0);
            micromixinTransformer.transform(cnmm);
            for (MethodNode method : cnmm.methods) {
                Objects.requireNonNull(method, "Null method node: " + cnmm.name + ".?:?");
                Objects.requireNonNull(method.name, "Null method name: " + cnmm.name + ".?:" + method.desc);
                Objects.requireNonNull(method.desc, "Null method desc: " + cnmm.name + "." + method.name + ":?");
            }
            ClassWriter cw = new ClassWriter(0);
            cnmm.accept(cw);
            reimpl = cw.toByteArray();
        } catch (Exception e1) {
            try {
                Files.createDirectories(Paths.get("generated", "raw", name.replace('.', '/')).getParent());
                Files.createDirectories(Paths.get("generated", "raw-asm", name.replace('.', '/')).getParent());
                Files.write(Paths.get("generated", "raw", name.replace('.', '/') + ".class"), raw);
                TraceClassVisitor rawVisit = new TraceClassVisitor(new PrintWriter(Files.newOutputStream(Paths.get("generated", "raw-asm", name.replace('.', '/') + ".dis"))));
                new ClassReader(raw).accept(rawVisit, 0);
            } catch (IOException e) {
                IllegalStateException t = new IllegalStateException(e);
                t.addSuppressed(e1);
                throw t;
            }
            throw new IllegalStateException(e1);
        }
        try {
            Files.createDirectories(Paths.get("generated", "raw", name.replace('.', '/')).getParent());
            Files.createDirectories(Paths.get("generated", "reimpl", name.replace('.', '/')).getParent());
            Files.createDirectories(Paths.get("generated", "raw-asm", name.replace('.', '/')).getParent());
            Files.createDirectories(Paths.get("generated", "reimpl-asm", name.replace('.', '/')).getParent());
            Files.write(Paths.get("generated", "raw", name.replace('.', '/') + ".class"), raw);
            Files.write(Paths.get("generated", "reimpl", name.replace('.', '/') + ".class"), reimpl);
            TraceClassVisitor rawVisit = new TraceClassVisitor(new PrintWriter(Files.newOutputStream(Paths.get("generated", "raw-asm", name.replace('.', '/') + ".dis"))));
            TraceClassVisitor reimplVsisit = new TraceClassVisitor(new PrintWriter(Files.newOutputStream(Paths.get("generated", "reimpl-asm", name.replace('.', '/') + ".dis"))));
            new ClassReader(raw).accept(rawVisit, 0);
            new ClassReader(reimpl).accept(reimplVsisit, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MixinTransformer<Void> micromixinTransformer = new MixinTransformer<>(new BytecodeProvider<Void>() {
            @SuppressWarnings("null")
            @Override
            @NotNull
            public ClassNode getClassNode(Void modularityAttachment, @NotNull String internalName)
                    throws ClassNotFoundException {
                try {
                    ClassNode node = new ClassNode();
                    new ClassReader(readAllBytesEx(MixinMerger.class.getClassLoader().getResourceAsStream(internalName + ".class"))).accept(node, 0);
                    return node;
                } catch (IOException e) {
                    throw new ClassNotFoundException("Unable to find class " + internalName + " due to an IO Exception (is it present on the classpath?)", e);
                } catch (Exception e) {
                    throw new ClassNotFoundException("Unable to find class " + internalName, e);
                }
            }
        });
        try {
            micromixinTransformer.addMixin(null, MixinConfig.fromJson(new JSONObject(new String(readAllBytes(MixinMerger.class.getClassLoader().getResourceAsStream("j8mixinconfig.json"))))));
        } catch (JSONException | InvalidMixinConfigException e) {
            e.printStackTrace();
        }

        tryTransform(micromixinTransformer, "de.geolykt.starloader.micromixin.test.j8.InjectTarget");
        tryTransform(micromixinTransformer, "de.geolykt.starloader.micromixin.test.j8.OverwriteTarget");
        tryTransform(micromixinTransformer, "de.geolykt.starloader.micromixin.test.j8.FieldInjectTarget");
        tryTransform(micromixinTransformer, "de.geolykt.starloader.micromixin.test.j8.erronous.TooManyAtReturn");
        tryTransform(micromixinTransformer, "de.geolykt.starloader.micromixin.test.j8.selector.SelectorTestA");
        tryTransform(micromixinTransformer, "de.geolykt.starloader.micromixin.test.j8.selector.NumberSelectorTest");
    }
}
