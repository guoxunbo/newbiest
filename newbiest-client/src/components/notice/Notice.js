
import { notification } from 'antd';

import {Application} from '../../api/Application';

class Notification {

    static showSuccess(message) {
        notification["success"]({
            description: message,
            duration: Application.notice.duration,
        });
    }

    static showError(errorCode, message) {
        notification["error"]({
            message: errorCode,
            description: message,
            duration: Application.notice.duration,
        });
    }

    /**
     * notice的信息不支持消息自动消失，需要人为去点击
     */
    static showNotice(message) {
        notification["info"]({
            description: message,
            duration: Application.notice.duration,
        });
       
    }

    static showInfo(message) {
        notification["info"]({
            description: message,
            duration: Application.notice.duration,
        });
    }

}

/**
 * 构建Mobile组件
 */
function buildMobile() {
    return {
        swipeDismiss: Application.notice.mobile.swipeDismiss,
        styling: Application.notice.mobile.styling
    }
};

export {Notification};
 