package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.service.ImageStorageService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileSystemImageStorageService implements ImageStorageService, InitializingBean {

    @Value("${spring.web.resources.static-locations}")
    private String[] staticLocations;

    private Path rootLocation;

    @Override
    public void afterPropertiesSet() throws Exception {
        String loc = staticLocations[0].replaceFirst("^file:", "");
        this.rootLocation = Paths.get(loc).toAbsolutePath().normalize();
        Files.createDirectories(rootLocation);
    }


    @Override
    public String store(MultipartFile file) {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int i = original.lastIndexOf('.');
        if (i > 0) ext = original.substring(i);
        String filename = UUID.randomUUID() + ext;

        try {
            Path target = this.rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), target);

            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(filename)
                    .toUriString();

            return url;
        } catch (IOException e) {
            throw new RuntimeException("Error guardando archivo " + original, e);
        }
    }
}