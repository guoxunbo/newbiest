/**
 * format字符串 比如
 * @param 对象 支持从obj里面取key里面取值赋值
 * tableRrn = :objectRrn 替换成tableRrn = object[tableRrn] => tableRrn = '1'
 * 当前就只支持一个对象对占位符进行赋值
 */
String.prototype.format = function(obj) {
    if (!obj) {
        return this;
    }
    let s = this;
    for(var key in obj) {
        s = s.replace(new RegExp("\\:" + key + "", "g"), "'" + obj[key] + "'");
    }
	return s;
};

/**
 * format字符串 
 * @key 要替换的值
 * @param obj 单一值，不支持从obj里面取值赋值
 */
String.prototype.formatValue = function(key, obj) {
    if (!obj) {
        return this;
    }
    let s = this;
    s = s.replace(new RegExp("\\:" + key + "", "g"), "'" + obj + "'");
	return s;
};

