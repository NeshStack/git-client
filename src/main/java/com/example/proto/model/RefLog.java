package com.example.proto.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefLog {
    private String username;
    private String body;
    private String message;
    private String newId;
    private String oldId;
}