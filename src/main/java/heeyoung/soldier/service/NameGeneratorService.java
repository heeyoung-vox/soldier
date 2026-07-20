package heeyoung.soldier.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class NameGeneratorService {
    private List<String> names;
    ResourceLoader resourceLoader;

    public NameGeneratorService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:names.txt");
        if (!resource.exists()) {
            throw new FileNotFoundException("names.txt not found in classpath");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {

            names = new ArrayList<>(reader.lines().toList());
        }
        if (names.isEmpty()) {
            throw new IllegalStateException("names.txt is empty");
        }
    }

    public String generateRandomName() {
        int index = ThreadLocalRandom.current().nextInt(names.size());
        int rand = ThreadLocalRandom.current().nextInt(100);
        return names.get(index) + rand;
    }
}
