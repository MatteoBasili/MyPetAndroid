package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DotenvReader {
    private final String directory;
    private final String filename;

    public DotenvReader(String directory2, String filename2) {
        this.directory = directory2;
        this.filename = filename2;
    }

    public List<String> read() throws DotenvException, IOException {
        Path path;
        String cwdMessage = "";
        String location = this.directory.replaceAll("\\\\", "/").replaceFirst("\\.env$", cwdMessage).replaceFirst("/$", cwdMessage) + "/" + this.filename;
        String lowerLocation = location.toLowerCase();
        if (lowerLocation.startsWith("file:") || lowerLocation.startsWith("android.resource:")) {
            path = Paths.get(URI.create(location));
        } else {
            path = Paths.get(location, new String[0]);
        }
        if (Files.exists(path, new LinkOption[0])) {
            return (List) Files.lines(path).collect(Collectors.toList());
        }
        try {
            return (List) ClasspathHelper.loadFileFromClasspath(location.replaceFirst("./", "/")).collect(Collectors.toList());
        } catch (DotenvException e) {
            Path cwd = FileSystems.getDefault().getPath(".", new String[0]).toAbsolutePath().normalize();
            if (!path.isAbsolute()) {
                cwdMessage = "(working directory: " + cwd + ")";
            }
            e.addSuppressed(new DotenvException("Could not find " + path + " on the file system " + cwdMessage));
            throw e;
        }
    }
}
