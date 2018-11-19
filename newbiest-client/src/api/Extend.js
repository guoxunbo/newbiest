/**
 * format字符串 比如
 * tableRrn = :objectRrn 替换成tableRrn = object[tableRrn] => tableRrn = '1'
 * 当前就只支持一个对象对占位符进行赋值
 */
String.prototype.format = function(obj) {
    if (!obj) {
        return this;
    }
    var s = this;
    for(var key in obj) {
        s = s.replace(new RegExp("\\:" + key + "", "g"), "'" + obj[key] + "'");
    }
	return s;
};

