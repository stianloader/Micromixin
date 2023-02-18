package de.geolykt.starloader.micromixin.test.j8;

import de.geolykt.starloader.mod.Extension;

public class TestBootstrap extends Extension {

    @Override
    public void preInitialize() {
        try {
            Class.forName("de.geolykt.starloader.micromixin.test.j8.targets.MixinOverwriteTest");
        } catch (VerifyError e) {
            System.err.println("VERFICATION ERROR:");
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        super.preInitialize();
        TestHarness.runAllTests();
    }
}
