package com.kevinjan.learningSpring.learning.spring.boot;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by kevinjanvier on 12/06/2017.
 */
@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    private Image() {
    }

    public Image(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
