export default class StringBuffer{
    strings = new Array();

    append = (str) => {
        this.strings.push(str);
    }
    
    toString = () => {
        return this.strings.join("");
    }
}