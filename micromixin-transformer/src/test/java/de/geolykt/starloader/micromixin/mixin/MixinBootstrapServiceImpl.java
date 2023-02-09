package de.geolykt.starloader.micromixin.mixin;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class MixinBootstrapServiceImpl implements IMixinServiceBootstrap {

    @Override
    public String getName() {
        return "MixinServiceBootstrap";
    }

    @Override
    public String getServiceClassName() {
        return "de.geolykt.starloader.micromixin.mixin.MixinBootstrapServiceImpl";
    }

    @Override
    public void bootstrap() {
    }
}
