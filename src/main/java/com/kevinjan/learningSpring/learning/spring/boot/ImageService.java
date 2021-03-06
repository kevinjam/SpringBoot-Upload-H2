package com.kevinjan.learningSpring.learning.spring.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by kevinjanvier on 12/06/2017.
 */
@Service
public class ImageService {

    private static String UPLOAD_ROOT ="upload-dir";

    private final ImageRepository repository;
    private final ResourceLoader resourceLoader;
    private final CounterService counterService;
    private final GaugeService gaugeService;
    private InMemoryMetricRepository inMemoryMetricRepository;

    @Autowired
    public ImageService(ImageRepository repository, ResourceLoader resourceLoader,
                        CounterService counterService, GaugeService gaugeService,
                        InMemoryMetricRepository inMemoryMetricRepository){

        this.repository = repository;
        this.resourceLoader = resourceLoader;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
        this.inMemoryMetricRepository = inMemoryMetricRepository;

        //
        this.counterService.reset("files.upload");
        this.gaugeService.submit("file.upload.Byte", 0);
        this.inMemoryMetricRepository.set(new Metric<Number>("files.upload.totalByte", 0));
    }

    //file name
    public Resource findOneImage(String filename){
        return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
    }

    public Page<Image> findPage(Pageable pageable){
        return repository.findAll(pageable);
    }


    //Creating new Images
    public void createImage(MultipartFile file) throws IOException {
        if (!file.isEmpty()){
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            repository.save(new Image(file.getOriginalFilename()));

            //gauge
            counterService.increment("files.upload");
            gaugeService.submit("file.upload.Byte" , file.getSize());
            inMemoryMetricRepository.increment(new Delta<Number>("files.upload.totalByte" , file.getSize()));

        }
    }

    public void deleteImage(String filename) throws IOException {
        final Image byName = repository.findByname(filename);
        repository.delete(byName);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
    }
    @Bean
//    @Profile("dev")
    CommandLineRunner setUp(ImageRepository imageRepository, ConditionEvaluationReport conditionEval) throws IOException{
        return (arg) -> {

            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));
            FileCopyUtils.copy("Test File", new FileWriter(UPLOAD_ROOT + "/test"));
            repository.save(new Image("test"));

            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test1"));
            repository.save(new Image("test2"));

            FileCopyUtils.copy("Test file3 ", new FileWriter(UPLOAD_ROOT + "/test2"));
            repository.save(new Image("Test3"));


            conditionEval.getConditionAndOutcomesBySource().entrySet().stream()
                    .filter(entery -> entery.getValue().isFullMatch())
                    .forEach(entry->{
                        System.out.println(entry.getKey() + " => Match?" + entry.getValue().isFullMatch());
                    });
        };

    }

}
