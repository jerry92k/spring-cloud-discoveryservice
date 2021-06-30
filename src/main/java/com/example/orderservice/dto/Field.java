package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Field {
    private String type;
    private boolean optinal;
    private String field;
}
