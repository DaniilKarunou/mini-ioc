package com.miniioc.examples.cyclicdeps;

import com.miniioc.framework.annotation.stereotype.Service;

@Service
public class ServiceA {

    private final ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}
