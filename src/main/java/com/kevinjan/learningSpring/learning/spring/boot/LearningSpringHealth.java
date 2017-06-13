package com.kevinjan.learningSpring.learning.spring.boot;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kevinjanvier on 13/06/2017.
 */
@Component
public class LearningSpringHealth implements HealthIndicator {

    @Override
    public Health health() {

        try {
            int resultCode = ((HttpURLConnection) new URL("http://greglturnquist.com/learning-spring-boot").openConnection()).getResponseCode();

            if (resultCode >= 200 && resultCode < 300){
                return Health.up().build();
            }else {
                return Health.down().withDetail("Http Status Code" , resultCode).build();
            }
        } catch (IOException e) {
          //  e.printStackTrace();
            return Health.down(e).build();
        }

    }
}
