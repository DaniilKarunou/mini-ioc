package com.miniioc.examples.cyclicdeps;

import com.miniioc.core.annotation.injection.Service;

@Service
public class ServiceB {

    private final ServiceA serviceA;

    public ServiceB(ServiceA serviceA) {
        this.serviceA = serviceA;
    }
}
