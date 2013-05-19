package org.webjars;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

public class WebJarAssetLocatorTest {

    @Test
    public void get_paths_of_asset_in_nested_folder() {
        final WebJarAssetLocator locator = new WebJarAssetLocator();
        final String jsPath = locator.getFullPath("bootstrap.js");
        final String cssPath = locator.getFullPath("bootstrap.css");

        assertEquals("META-INF/resources/webjars/bootstrap/2.2.2/js/bootstrap.js", jsPath);
        assertEquals("META-INF/resources/webjars/bootstrap/2.2.2/css/bootstrap.css", cssPath);
    }

    @Test
    public void get_full_path_of_asset_in_root_folder() {
        final String jsFullPath = new WebJarAssetLocator().getFullPath("jquery.js");

        assertEquals("META-INF/resources/webjars/jquery/1.8.3/jquery.js", jsFullPath);
    }

    @Test
    public void get_full_path_from_partial_path_with_folders() {
        final WebJarAssetLocator locator = new WebJarAssetLocator();
        final String jsPath1 = locator.getFullPath("js/bootstrap.js");
        final String jsPath2 = locator.getFullPath("/2.2.2/js/bootstrap.js");
        final String jsPath3 = locator.getFullPath("bootstrap/2.2.2/js/bootstrap.js");
        final String jsPath4 = locator.getFullPath("/bootstrap/2.2.2/js/bootstrap.js");

        final String expected = "META-INF/resources/webjars/bootstrap/2.2.2/js/bootstrap.js";
        assertEquals(expected, jsPath1);
        assertEquals(expected, jsPath2);
        assertEquals(expected, jsPath3);
        assertEquals(expected, jsPath4);
    }

    @Test
    public void should_throw_exception_when_asset_not_found() {
        try {
            new WebJarAssetLocator().getFullPath("asset-unknown.js");
            fail("Exception should have been thrown!");
        } catch (final IllegalArgumentException e) {
            assertEquals("asset-unknown.js could not be found. Make sure you've added the corresponding WebJar and please check for typos.", e.getMessage());
        }

        try {
            new WebJarAssetLocator().getFullPath("unknown.js");
            fail("Exception should have been thrown!");
        } catch (final IllegalArgumentException e) {
            assertEquals("unknown.js could not be found. Make sure you've added the corresponding WebJar and please check for typos.", e.getMessage());
        }
    }

    @Test
    public void should_distinguish_between_multiple_versions() {
        final WebJarAssetLocator locator = new WebJarAssetLocator();
        final String v1Path = locator.getFullPath("1.0.0/multiple.js");
        final String v2Path = locator.getFullPath("2.0.0/multiple.js");
        final String moduleV2Path = locator.getFullPath("2.0.0/module/multiple_module.js");

        assertEquals("META-INF/resources/webjars/multiple/1.0.0/multiple.js", v1Path);
        assertEquals("META-INF/resources/webjars/multiple/2.0.0/multiple.js", v2Path);
        assertEquals("META-INF/resources/webjars/multiple/2.0.0/module/multiple_module.js", moduleV2Path);
    }

    @Test
    public void should_throw_exceptions_when_several_matches_found() {
        try {
            new WebJarAssetLocator().getFullPath("multiple.js");
            fail("Exception should have been thrown!");
        } catch (final IllegalArgumentException e) {
            assertEquals("Multiple matches found for multiple.js. Please provide a more specific path, for example by including a version number.", e.getMessage());
        }
    }

    @Test
    public void should_throw_exceptions_when_several_matches_found_with_folder_in_path() {
        try {
            new WebJarAssetLocator().getFullPath("module/multiple_module.js");
            fail("Exception should have been thrown!");
        } catch (final IllegalArgumentException e) {
            assertEquals("Multiple matches found for module/multiple_module.js. Please provide a more specific path, for example by including a version number.", e.getMessage());
        }
    }

    @Test
    public void should_list_assets_in_folder() {
        final String fullPathPrefix = "META-INF/resources/webjars/multiple/1.0.0/";
        final Set<String> assets = new WebJarAssetLocator().listAssets("/multiple/1.0.0");

        assertThat(assets, hasItems(fullPathPrefix + "multiple.js", fullPathPrefix + "module/multiple_module.js"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannot_create_instance_using_null_classloader() {
        WebJarAssetLocator.create(null);
    }

    @Test
    public void should_find_valid_path_when_created_using_context_classLoader() {
        shouldFindValidPathWhenUsingClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void should_find_valid_path_when_created_using_webJarAssetLocator_classLoader() {
        shouldFindValidPathWhenUsingClassLoader(WebJarAssetLocator.class.getClassLoader());
    }

    private void shouldFindValidPathWhenUsingClassLoader(final ClassLoader classLoader) {
        final WebJarAssetLocator locator = WebJarAssetLocator.create(classLoader);
        final String jsPath = locator.getFullPath("js/bootstrap.js");

        final String expected = "META-INF/resources/webjars/bootstrap/2.2.2/js/bootstrap.js";
        assertEquals(expected, jsPath);
    }
}
