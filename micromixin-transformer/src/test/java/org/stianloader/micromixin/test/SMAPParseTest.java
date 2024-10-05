package org.stianloader.micromixin.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.stianloader.micromixin.transform.internal.util.smap.SMAPRoot;

public class SMAPParseTest {
    @Test
    public void testSMAPJDT() {
        // SMAP file from eclipse.jdt.debug:
        // https://github.com/eclipse-jdt/eclipse.jdt.debug/blob/83dd8c96716d33396cf594a44ecb09940030707b/org.eclipse.jdt.launching.javaagent/src/main/java/org/eclipse/jdt/launching/internal/weaving/ClassfileTransformer.java#L45-L56
        String smapDefinition = "SMAP\nSourceFile.java\nJava\n*S jdt\n*F\n1 SourceFile.java\n2 /home/user/workspace/bin\n*L\n*E\n";
        StringBuilder builder = new StringBuilder();
        SMAPRoot.parseSMAP(smapDefinition).pushContents(builder);
        assertEquals(smapDefinition, builder.toString().replace('\r', '\n'));
    }

    @Test
    public void testSMAPSpongeMixin() {
        String smapDefinition = "SMAP\nAllowTest.java\nMixin\n*S Mixin\n*F\n+1 AllowTest.java\norg/stianloader/micromixin/test/j8/targets/AllowTest.java\n+2 AllowMixin.java\norg/stianloader/micromixin/test/j8/mixin/AllowMixin.java\n*L\n1#1,500:1\n1#2,500:501\n*E\n";
        StringBuilder builder = new StringBuilder();
        SMAPRoot.parseSMAP(smapDefinition).pushContents(builder);
        assertEquals(smapDefinition, builder.toString().replace('\r', '\n'));
    }
}
