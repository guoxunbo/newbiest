export default class PropertyUtils {

    /**
     * 将target对象中存在但source对象不存在的属性值进行赋值
     * @param target 
     */
    static copyNoIncludePropertyValue = function(source, target) {
        for (let propertyName in target) {
            if (!source.hasOwnProperty(propertyName)) {
                source[propertyName] = target[propertyName];
            }
        }
    }

    /**
     * 将source对象中的值copy到target对象中
     */
    static copyProperties = function(source, target) {
        for (let propertyName in source) {
            target[propertyName] = source[propertyName];
        }
    }
}