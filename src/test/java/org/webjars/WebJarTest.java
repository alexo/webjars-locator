package org.webjars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test various operations exposed by the {@link WebJar} class.
 *
 * @author Alex Objelean
 */
public class WebJarTest {
    private WebJar victim;

    @Before
    public void setUp() {
        victim = createValidWebJar();
    }

    @Test
    public void shouldIdentifyAValidWebJar() {
        assertTrue(createValidWebJar().isValid());
    }

    @Test
    public void shouldIdentifyAnInvalidWebJar() {
        assertFalse(createInvalidWebJar().isValid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAcceptNullUri() {
        new WebJar(null);
    }

    @Test
    public void shouldReturnFullPathForValidWebJar() {
        assertEquals("META-INF/resources/webjars/font-awesome/4.0.3/css/font-awesome.css", victim.getFullPath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateRelativePathFromNullRelativeUri() {
        victim.relative(null);
    }

    @Test
    public void shouldCreateRelativeWebJarFromInvalidRelativeUri() {
        final WebJar relativeWebJar = victim.relative(invalidWebJarUri());
        assertFalse(relativeWebJar.isValid());
    }

    @Test
    public void shouldCreateRelativeWebJarForResourceFromSameFolder() {
        final WebJar relativeWebJar = victim.relative("font-awesome.min.css");
        assertTrue(relativeWebJar.isValid());
        assertEquals("META-INF/resources/webjars/font-awesome/4.0.3/css/font-awesome.min.css", relativeWebJar.getFullPath());
    }

    @Test
    public void shouldCreateRelativeWebJarForResourceFromParentFolder() {
        final WebJar relativeWebJar = victim.relative("../fonts/fontawesome-webfont.eot");
        assertTrue(relativeWebJar.isValid());
        assertEquals("META-INF/resources/webjars/font-awesome/4.0.3/fonts/fontawesome-webfont.eot", relativeWebJar.getFullPath());
    }

    @Test
    public void shouldCreateRelativeWebJarForResourceFromSubFolder() {
        victim = new WebJar("nested.js");
        final WebJar relativeWebJar = victim.relative("child/nested-child.js");
        assertTrue(relativeWebJar.isValid());
        assertEquals("META-INF/resources/webjars/nested/1.0.0/child/nested-child.js", relativeWebJar.getFullPath());
    }

    @Test
    public void shouldCreateRelativeWebJarForResourceFromNestedSubFolder() {
        victim = new WebJar("nested.js");
        final WebJar relativeWebJar = victim.relative("child/grandchild/nested-grandchild.js");
        assertTrue(relativeWebJar.isValid());
        assertEquals("META-INF/resources/webjars/nested/1.0.0/child/grandchild/nested-grandchild.js", relativeWebJar.getFullPath());
    }

    private WebJar createValidWebJar() {
        return new WebJar("font-awesome.css");
    }

    private WebJar createInvalidWebJar() {
        return new WebJar(invalidWebJarUri());
    }

    private String invalidWebJarUri() {
        return "invalid.js";
    }
}
