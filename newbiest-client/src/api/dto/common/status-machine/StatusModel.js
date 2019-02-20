export default class StatusModel {
    objectRrn;
    name;
    description;
    events;

    setObjectRrn(objectRrn) {
        this.objectRrn = objectRrn;
    }
    
    setEvents(events) {
        this.events = events;
    }
}