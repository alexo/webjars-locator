package org.webjars;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of a webjar resource. Wraps all operations which can be applied on a
 *
 * @author Alex Objelean
 */
public class WebJar {
    private static final Logger LOG = LoggerFactory.getLogger(WebJar.class);
    private final String webJarUri;
    private final WebJarAssetLocator assetLocator;
    public WebJar(final String webjarUri) {
        notNull(webjarUri);
        LOG.debug("webjarUri: {}", webjarUri);
        this.webJarUri = webjarUri;
        assetLocator = new WebJarAssetLocator();
    }

    /**
     * @return true if this webJar is valid and wraps a valid resource.
     */
    public boolean isValid() {
        boolean valid = true;
        try {
            getFullPath();
        } catch(final IllegalArgumentException e) {
            valid = false;
        }
        return valid;
    }

    /**
     * @return a full path of the webJar resource.
     */
    public String getFullPath() {
        return assetLocator.getFullPath(webJarUri);
    }

    /**
     * @param relativePath a uri which is relative to the uri of this webJar.
     * @return a {@link WebJar} object for the relative resource.
     */
    public WebJar relative(final String relativePath) {
        notNull(relativePath);
        final String parentFolder = FilenameUtils.getFullPath(getFullPath());
        final String newPath = removeWebjarPrefix(FilenameUtils.normalize(parentFolder + relativePath));
        LOG.debug("relativePath: {}", newPath);
        return new WebJar(newPath);
    }

    private String removeWebjarPrefix(final String path) {
        return path.replace(WebJarAssetLocator.WEBJARS_PATH_PREFIX, "");
    }

    private void notNull(final String argument) {
        if (argument == null) {
            throw new IllegalArgumentException("argument uri cannot be null");
        }
    }
}
