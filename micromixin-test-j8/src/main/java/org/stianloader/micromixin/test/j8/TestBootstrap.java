package org.stianloader.micromixin.test.j8;

import de.geolykt.starloader.mod.Extension;

public class TestBootstrap extends Extension {

    @Override
    public void preInitialize() {
        super.preInitialize();
        TestHarness.runAllTests();
    }
}
