package com.miniioc.examples.cyclicdeps;

import com.miniioc.framework.annotation.stereotype.Service;

@Service
public class ServiceB {

    private final ServiceA serviceA;

    public ServiceB(ServiceA serviceA) {
        this.serviceA = serviceA;
    }
}
