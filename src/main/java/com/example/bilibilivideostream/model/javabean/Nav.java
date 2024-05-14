package com.example.bilibilivideostream.model.javabean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nav {
    private ResponseData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseData {
        @JsonProperty("wbi_img")
        private WbiImg wbiImg;
        @JsonProperty("face")
        private String faceUrl;
        @JsonProperty("mid")
        private String mid;
        private String uname;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class WbiImg {
            @JsonProperty("img_url")
            private String imgUrl;
            @JsonProperty("sub_url")
            private String subUrl;
        }
    }
}
