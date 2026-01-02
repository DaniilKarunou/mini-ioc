package com.miniioc.examples;

import com.miniioc.core.annotation.Service;

@Service
public class UserService {
    public String getUser() {
        return "Jan Kowalski";
    }
}
