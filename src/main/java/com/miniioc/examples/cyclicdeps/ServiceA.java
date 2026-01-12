package com.miniioc.examples.cyclicdeps;

import com.miniioc.core.annotation.injection.Service;

@Service
public class ServiceA {

    private final ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}
