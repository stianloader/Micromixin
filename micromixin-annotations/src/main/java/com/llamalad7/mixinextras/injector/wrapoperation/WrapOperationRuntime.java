package com.llamalad7.mixinextras.injector.wrapoperation;

import java.util.Arrays;

public class WrapOperationRuntime {
    public static void checkArgumentCount(Object[] argv, int argc, String debug) {
        if (argv.length != argc) {
            throw new LinkageError(String.format("Expected %d arguments of type %s, got %d arguments instead. Actual arguments: %s", argc, debug, argv.length, Arrays.toString(argv)));
        }
    }
}
