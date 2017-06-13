package com.kevinjan.learningSpring.learning.spring.boot;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by kevinjanvier on 12/06/2017.
 */
public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    public Image findByname(String name);
}
