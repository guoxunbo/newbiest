
class JsonUtils {

    /**
     * json字符串转对象
     */
    static json2Object(jsonStr){
        return JSON.parse(jsonStr);
    }

    /**
     * 对象转json字符创
     */
    static object2Json(obj){
        return JSON.stringify(obj);
    }

    
}

export {JsonUtils}