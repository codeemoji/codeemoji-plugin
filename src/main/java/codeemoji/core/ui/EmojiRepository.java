package codeemoji.core.ui;

import codeemoji.core.util.CEParsingUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EmojiRepository {

    private static final Cache<Boolean, List<Emoji>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)  // Expire cache after 1 minute
            .build();

    public static List<Emoji> getLocalEmojis(boolean colored) {
        cache.invalidateAll();
        //get local path
        return getOrComputeCache("emoji-repository.txt", colored);
    }

    public static List<Emoji> fetchDefaultEmojis(boolean colored) {
        return getOrComputeCache("https://unicode.org/Public/emoji/13.1/emoji-test.txt", colored);
    }

    // Fetch and parse emojis from either a URL or file path with caching based on color preference
    public static List<Emoji> getOrComputeCache(String source, boolean colored) {
        List<Emoji> emojis = cache.getIfPresent(colored);
        if (emojis == null) {
            emojis = parseEmojis(source, colored);
            cache.put(colored, emojis);  // Cache the result
        }
        return emojis;
    }

    // Parse emojis from the source (file or URL)
    private static List<Emoji> parseEmojis(String source, boolean colored) {
        List<Emoji> emojiList = new ArrayList<>();
        Set<String> seenSymbols = new HashSet<>();  // To avoid duplicates

        try (BufferedReader reader = createReader(source)) {
            String line;
            String currentCategory = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    // Extract category if present
                    if (line.startsWith("# group:")) {
                        currentCategory = line.substring("# group:".length()).trim();
                    }
                    continue;
                }

                // Process only fully-qualified emojis
                if (line.contains("; fully-qualified")) {
                    String[] parts = line.split(";");
                    String emojiCode = parts[0].trim(); // Extract the Unicode code points
                    String afterHash = line.substring(line.indexOf("#") + 1).trim();

                    // Extract the description (e.g., "neutral face")
                    String description = afterHash.substring(afterHash.indexOf(" ") + 1).trim();
                    description = removeTagsFromDescription(description);

                    // Convert Unicode code points to actual emoji symbol
                    String emojiSymbol = CEParsingUtils.parseSymbolFromCodePointString(emojiCode, colored);

                    // Skip duplicates based on symbol
                    if (!seenSymbols.contains(emojiSymbol)) {
                        seenSymbols.add(emojiSymbol);

                        // Create an Emoji object
                        if (currentCategory != null) {
                            Emoji emoji = new Emoji(emojiSymbol, currentCategory, description);
                            emojiList.add(emoji);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return emojiList;
    }

    // Helper method to create a BufferedReader from either a URL, file, or classpath resource
    private static BufferedReader createReader(String source) throws IOException {
        if (isValidUrl(source)) {
            URL url = new URL(source);
            return new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        } else if (isClasspathResource(source)) {
            // Use Class.getResourceAsStream for classpath resources
            InputStream inputStream = EmojiRepository.class.getResourceAsStream("/" + source); // Add the leading '/'
            if (inputStream != null) {
                return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            } else {
                throw new FileNotFoundException("Resource not found in classpath: " + source);
            }
        } else {
            File file = new File(source);
            if (file.exists()) {
                return new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
            } else {
                throw new FileNotFoundException("The file could not be found: " + source);
            }
        }
    }

    // Check if the string is a classpath resource (by checking for leading "/")
    private static boolean isClasspathResource(String source) {
        return !source.startsWith("/") && !source.contains(":");  // Detect path that's likely not a URL or absolute path
    }

    // Check if a string is a valid URL
    private static boolean isValidUrl(String source) {
        try {
            new URL(source);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String removeTagsFromDescription(String rawDescription) {
        // Example cleanup: remove version metadata (e.g., E5.0)
        return rawDescription.replaceAll("E\\d+\\.\\d+", "").trim();
    }

}
