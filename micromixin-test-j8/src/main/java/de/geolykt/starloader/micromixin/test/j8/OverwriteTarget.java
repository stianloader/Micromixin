package de.geolykt.starloader.micromixin.test.j8;

@SuppressWarnings("unused")
class OverwriteTarget {

    public void callOverwrittenMethod() {
        throw new AssertionError("This should be overwritten");
    }

    private void multiOverwrite0() {
        throw new AssertionError("Overwrite0");
    }

    private void multiOverwrite1() {
        throw new AssertionError("Overwrite1");
    }
}
