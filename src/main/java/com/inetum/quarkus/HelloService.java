package com.inetum.quarkus;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloService {

    @ConfigProperty(name = "buenosdias")
    String saludos;

    public String politeHello(String name){
        return "Hola " + name;
    }

    public String politeHello2(String name){
        return "Hola " + name + " " + saludos;
    }
}
