import {EventEmitter} from 'events';

/**
 * 前端主要事件处理 包括事件的常量定义
 *  使用EventEmitter
 */
const EventModel = new EventEmitter();

const EventName = {
    ComboxValueChanged : "ComboxValueChanged"
}

export default class EventUtils {
    static displayName = 'EventUtils';

    static getEventEmitter() {
        return EventModel;
    }

    static getEventNames() {
        return EventName;
    }
}
