package com.example.bilibilivideostream.model.server;

import com.example.bilibilivideostream.model.javabean.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class CookieService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Cookie findOneCookieByMid(Cookie cookie){
        Query query = new Query(Criteria.where("_id").is(cookie.getMid()));
        return mongoTemplate.findOne(query, Cookie.class);
    }

    public void updateCookie(Cookie cookie) {
        Field[] fields = Cookie.class.getDeclaredFields();
        Update update = new Update();
        for(Field field : fields) {
            field.setAccessible(true);
            Object val = null;
            try {
                val = field.get(cookie);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            String key = field.getName();
            if(val != null && !"mid".equals(key)) {
                update.set(key, val);
            }
        }
        Query query = new Query(Criteria.where("_id").is(cookie.getMid()));
        mongoTemplate.updateFirst(query, update, Cookie.class);
    }

    public int insertCookie(Cookie cookie){
        mongoTemplate.insert(cookie);
        return 1;
    }
}
