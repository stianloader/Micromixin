package org.stianloader.micromixin.testneo.testenv;

import de.geolykt.starloader.mod.Extension;

public class TestingEnvironmentExtension extends Extension {
    @Override
    public void postInitialize() {
        System.out.println("Testing extension running with classloader " + this.getClass().getClassLoader().getName());
        System.out.println("SLL running under CL '" + Extension.class.getClassLoader() + "' (named: '" + Extension.class.getClassLoader().getName() + "')");
        System.out.println("SLL running under Module " + Extension.class.getModule().getDescriptor().toNameAndVersion());
    }
}
