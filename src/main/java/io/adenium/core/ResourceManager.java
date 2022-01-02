package io.adenium.core;

import java.io.InputStream;

public class ResourceManager {
    public InputStream get(String path) {
        return getClass().getResourceAsStream(path);
    }
}
