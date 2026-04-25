package com.llamalad7.mixinextras.injector.wrapoperation;

public interface Operation<T> {
    T call(Object... args);
}
