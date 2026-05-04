package io.github.alineaos.librarymanager.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {
    @Autowired
    private ResourceLoader resourceLoader;

    public String readResourceFile(String fileName) throws IOException {
        File file = resourceLoader.getResource("classpath:%s".formatted(fileName)).getFile();
        return new String(Files.readAllBytes(file.toPath()));
    }
}
