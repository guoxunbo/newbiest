import StatusModel from "../dto/common/status-machine/StatusModel";
import Event from "../dto/common/status-machine/Event";

const ActionType = {
    GetByRrn: "GetByRrn",
    GetById: "GetById",
    DispatchEvent: "DispatchEvent"
}

export default class StatusModelRequestBody {

    actionType;
    statusModel;

    constructor(actionType, statusModel){
        this.actionType = actionType;
        this.statusModel = statusModel;
    }

    static buildDispatchEventBody = (value) => {
        let statusModel = new StatusModel();
        statusModel.setObjectRrn(value.statusModelRrn);
        
        let eventKeys = value.eventKeys;
        let dispatchEvents = [];
        if (eventKeys) {
            eventKeys.forEach((key) => {
                let dispatchEvent = new Event();
                dispatchEvent.setObjectRrn(key);
                dispatchEvents.push(dispatchEvent);
            });
        }
        statusModel.setEvents(dispatchEvents);
        return new StatusModelRequestBody(ActionType.DispatchEvent, statusModel);
    }

    static buildGetByRrnBody = (statusModelRrn) => {
        let statusModel = new StatusModel();
        statusModel.setObjectRrn(statusModelRrn);
        return new StatusModelRequestBody(ActionType.GetByRrn, statusModel);
    }

}

