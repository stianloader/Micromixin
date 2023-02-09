package de.geolykt.starloader.micromixin.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinServiceAbstract;

public class MixinServiceImpl extends MixinServiceAbstract {

    private static final IContainerHandle PRIMARY_CONTAINER = new ContainerHandleVirtual("PRIMARY_CONTAINER");
    public static IMixinTransformerFactory factory;

    public MixinServiceImpl() {
    }

    @Override
    public String getName() {
        return "MixinService";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public IClassProvider getClassProvider() {
        Thread.dumpStack();
        return null;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return new IClassBytecodeProvider() {

            @Override
            public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
                ClassNode cn = new ClassNode();
                ClassReader reader = new ClassReader(name);
                reader.accept(cn, 0);
                return cn;
            }

            @Override
            public ClassNode getClassNode(String name, boolean runTransformers)
                    throws ClassNotFoundException, IOException {
                ClassNode cn = new ClassNode();
                ClassReader reader = new ClassReader(name);
                reader.accept(cn, 0);
                return cn;
            }
        };
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        Thread.dumpStack();
        return null;
    }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.emptyList();
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return PRIMARY_CONTAINER;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    @Override
    public void offer(IMixinInternal internal) {
        if (internal instanceof IMixinTransformerFactory) {
            factory = (IMixinTransformerFactory) internal;
        }
        super.offer(internal);
    }
}
