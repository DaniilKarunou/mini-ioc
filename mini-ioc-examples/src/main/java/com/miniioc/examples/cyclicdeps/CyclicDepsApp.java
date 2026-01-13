package com.miniioc.examples.cyclicdeps;

import com.miniioc.framework.boot.MiniSpringApplication;

public class CyclicDepsApp {
    public static void main(String[] args) throws Exception {
        MiniSpringApplication.run(CyclicDepsApp.class);
    }
}
