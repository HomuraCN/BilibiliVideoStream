package com.example.bilibilivideostream.utils;


import com.example.bilibilivideostream.model.javabean.Result;
import com.example.bilibilivideostream.model.javabean.ResultEnum;

public class ResultUtils {
    /*有参有返回数据*/
    public static Result success(Object object){
        Result result = new Result();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        result.setData(object);
        return result;
    }
    /*空参无返回数据*/
    public static Result success(){

        return success(null);
    }
    /*失败*/
    public static Result error(Integer code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
