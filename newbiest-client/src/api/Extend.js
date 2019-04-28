/**
 * format字符串 比如
 * @param 对象 支持从obj里面取key里面取值赋值
 * @example tableRrn = :objectRrn 替换成tableRrn = object[tableRrn] => tableRrn = '1'
 * 当前就只支持一个对象对占位符进行赋值
 */
String.prototype.format = function(obj) {
    if (!obj) {
        return this;
    }
    let s = this;
    for(var key in obj) {
        let value = obj[key] || "";
        s = s.replace(new RegExp("\\:" + key + "", "g"), "'" + value + "'");
       
    }
	return s;
};

/**
 * format字符串 
 * @key 要替换的值
 * @param obj 单一值，不支持从obj里面取值赋值
 * @example let string = "tableRrn = :objectRrn" 
 *          string.formatValue("objectRrn", 1) => tableRrn = '1'
 */
String.prototype.formatValue = function(key, obj) {
    if (!obj) {
        return this;
    }
    let s = this;
    s = s.replace(new RegExp("\\:" + key + "", "g"), "'" + obj + "'");
	return s;
};

