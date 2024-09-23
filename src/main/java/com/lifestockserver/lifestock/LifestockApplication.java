package com.lifestockserver.lifestock;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.lang.NonNull;

import com.lifestockserver.lifestock.file.domain.File;
@SpringBootApplication
public class LifestockApplication  implements ApplicationContextAware {

	@Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        File.setApplicationContext(applicationContext);
    }

	public static void main(String[] args) {
		SpringApplication.run(LifestockApplication.class, args);
	}
}
