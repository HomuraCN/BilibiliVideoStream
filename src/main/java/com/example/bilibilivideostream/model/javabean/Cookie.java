package com.example.bilibilivideostream.model.javabean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Document("cookie")
public class Cookie {
    @Id
    private int mid;
    private String sessData;
    private String biliJct;
}
