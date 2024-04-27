package org.stianloader.micromixin.test.j8.targets;

public class UniqueTest {

    public boolean isUniqueAbsorbed() {
        return true;
    }

    private boolean isUniqueRenamedPrivate() {
        return true;
    }

    public boolean isUniqueRenamedPrivatePublicTarget() {
        return true;
    }

    public void assertUniqueStays() {
        try {
            UniqueTest.class.getMethod("uniqueStays").invoke(this);
        } catch (Exception e) {
            throw new AssertionError("Exception raised. Likely reflection-related. @Unique likely renamed", e);
        }
    }

    public void assertUniqueRenamedPrivate() {
        if (!this.isUniqueRenamedPrivate()) {
            throw new AssertionError("isUniqueRenamedPrivate == false; expected opposite");
        }
        try {
            if (((Boolean)UniqueTest.class.getMethod("forwardIsUniqueRenamedPrivate").invoke(this)).booleanValue()) {
                throw new AssertionError("forwardIsUniqueRenamedPrivate == true; expected opposite");
            }
        } catch (Exception e) {
            throw new AssertionError("Exception raised. Likely reflection-related. That shouldn't happen", e);
        }
    }

    public void assertUniqueRenamedPrivatePublicTarget() {
        if (!this.isUniqueRenamedPrivatePublicTarget()) {
            throw new AssertionError("isUniqueRenamedPrivatePublicTarget == false; expected opposite");
        }
        try {
            if (((Boolean)UniqueTest.class.getMethod("forwardIsUniqueRenamedPrivatePublicTarget").invoke(this)).booleanValue()) {
                throw new AssertionError("forwardIsUniqueRenamedPrivatePublicTarget == true; expected opposite");
            }
        } catch (Exception e) {
            throw new AssertionError("Exception raised. Likely reflection-related. That shouldn't happen", e);
        }
    }
}
