package com.rag.foodMeMiaAdmin.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StringUtils {

    public static String getImageNameFromUrl(String imageUrl) {
        AtomicReference<String> imageName = new AtomicReference<>("");
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();
            path = URLDecoder.decode(path, "UTF-8");
            String[] parts = path.split("/");
            List<String> list = Arrays.asList(parts);
            imageName.set(list.get(list.size() - 1));

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return imageName.get();
    }
}
