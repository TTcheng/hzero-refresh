package me.wcc.util;

import java.io.*;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/5 下午2:00
 */
@Slf4j
public class RefreshProperties {
    private RefreshProperties() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    private static final String FILE_PATH = "refresh.properties";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_TOKEN_URL = "tokenUrl";

    public static Map<String, String> read() {
        InputStream in = null;
        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (created) {
                    log.warn("{} not found, it was created just now!", FILE_PATH);
                } else {
                    log.error("{} not found, and also failed to create!", FILE_PATH);
                }
                return Collections.emptyMap();
            }
            in = new BufferedInputStream(new FileInputStream(file));
            Properties properties = new Properties();
            properties.load(in);
            Set<String> propertyNames = properties.stringPropertyNames();
            if (properties.size() > 0) {
                Map<String, String> map = new HashMap<>(properties.size());
                propertyNames.forEach(name -> map.put(name, properties.getProperty(name)));
                return map;
            }
        } catch (IOException e) {
            log.error("Failed to read properties file:{}", FILE_PATH);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("Failed to close file: {}", FILE_PATH);
                }
            }
        }
        return Collections.emptyMap();
    }

    public static void update(Map<String, String> propertiesMap) {
        if (propertiesMap.size() > 0) {
            update(propertiesMap.get(KEY_TOKEN_URL), propertiesMap.get(KEY_USERNAME), propertiesMap.get(KEY_PASSWORD));
        }
    }

    public static void update(String authUrl, String username, String password) {
        if (StringUtils.isAllBlank(authUrl, username, password)) {
            return;
        }
        Map<String, String> old = new HashMap<>(read());
        if (StringUtils.isNotBlank(authUrl)) {
            old.put(KEY_TOKEN_URL, authUrl);
        }
        if (StringUtils.isNotBlank(username)) {
            old.put(KEY_USERNAME, username);
        }
        if (StringUtils.isNotBlank(password)) {
            old.put(KEY_PASSWORD, password);
        }
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(FILE_PATH))) {
            Properties properties = new Properties();
            old.forEach(properties::setProperty);
            properties.store(outputStream, "Update refresh properties");
        } catch (FileNotFoundException e) {
            log.error("File '{}' not found in classpath", FILE_PATH);
        } catch (IOException e) {
            log.error("Failed to read properties file:{}", FILE_PATH);
        }
    }
}
