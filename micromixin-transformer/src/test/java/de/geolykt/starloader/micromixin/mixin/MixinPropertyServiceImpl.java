package de.geolykt.starloader.micromixin.mixin;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

public class MixinPropertyServiceImpl implements IGlobalPropertyService {

    public final Map<String, IPropertyKey> keys = new HashMap<>();
    public final Map<IPropertyKey, Object> map = new IdentityHashMap<>();

    @Override
    public IPropertyKey resolveKey(String name) {
        IPropertyKey key = keys.get(name);
        if (key != null) {
            return key;
        }
        key = new IPropertyKey() {};
        keys.put(name, key);
        return key;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) map.get(key);
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        map.put(key, value);
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        throw new UnsupportedOperationException();
    }
}
