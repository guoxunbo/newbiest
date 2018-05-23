class EntityListRequestBody {

    entityModel;
    whereClause;
    orderBy;
    maxResult;
    firstResult;
    fields;

    constructor(entityModel, whereClause, orderBy, maxResult, firstResult, fields) {
        this.entityModel = entityModel;
        this.whereClause = whereClause;
        this.orderBy = orderBy;
        this.maxResult = maxResult;
        this.firstResult = firstResult;
        this.fields = fields;
    }

    setEntityModel(entityModel) {
        this.entityModel = entityModel;
    }
}

export {EntityListRequestBody}