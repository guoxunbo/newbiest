import * as PropTypes from 'prop-types';

export default class Table{

    objectRrn;
    name;
    whereClause;
    fields;

    constructor() {
    }

    setObjectRrn(objectRrn) {
        this.objectRrn = objectRrn;
    }

    setName(name) {
        this.name = name;
    }

    setWhereClause(whereClause) {
        this.whereClause = whereClause;
    }
    
    /**
     * 在新建的时候创建默认值
     */
    static buildDefaultModel(fields) {
        let object = {};
        if (Array.isArray(fields)) {
            fields.map((field) => {
                let value = field.defaultValue;
                if ("radio" == field.displayType) {
                    value = false;
                    if (field.defaultValue && "Y" == field.defaultValue.toUpperCase()) {
                        value = true;
                    } 
                }
                object[field.name] = value;
            })
        }
        return object;
    }

}
Table.prototypes = {
    objectRrn: PropTypes.number.isRequired,
    fields: PropTypes.array,
    whereClause: PropTypes.string
}