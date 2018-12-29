export default class EntityListManagerRequestBody {

    entityModel;
    whereClause;
    orderBy;
    maxResult;
    firstResult;

    constructor(entityModel, whereClause, orderBy, maxResult, firstResult) {
        this.entityModel = entityModel;
        this.whereClause = whereClause;
        this.orderBy = orderBy;
        this.maxResult = maxResult;
        this.firstResult = firstResult;
    }
    
    static buildGetEntityListBody = (entityModel, whereClause, orderBy, maxResult, firstResult) => {
        return new EntityListManagerRequestBody(entityModel, whereClause, orderBy, maxResult, firstResult);
    }
    
}