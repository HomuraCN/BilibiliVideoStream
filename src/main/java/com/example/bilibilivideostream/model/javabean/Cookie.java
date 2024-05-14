package com.example.bilibilivideostream.model.javabean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Cookie {

    private int mid;
    private String sessData;
    private String biliJct;
}
