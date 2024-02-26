package org.stianloader.micromixin.transform.internal;

public class MixinParseException extends RuntimeException {

    private static final long serialVersionUID = 2603892437930485989L;

    public MixinParseException(String message) {
        super(message);
    }

    public MixinParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
