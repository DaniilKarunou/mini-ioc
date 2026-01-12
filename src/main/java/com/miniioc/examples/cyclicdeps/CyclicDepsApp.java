package com.miniioc.examples.cyclicdeps;

import com.miniioc.boot.MiniSpringApplication;

public class CyclicDepsApp {
    public static void main(String[] args) throws Exception {
        MiniSpringApplication.run(CyclicDepsApp.class);
    }
}
