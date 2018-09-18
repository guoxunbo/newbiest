import PNotify from 'pnotify/dist/es/PNotify';
import 'pnotify/dist/PNotifyBrightTheme.css';
import PNotifyButtons from 'pnotify/dist/es/PNotifyButtons';
import PNotifyMobile from 'pnotify/dist/es/PNotifyMobile';
import PNotifyConfirm from 'pnotify/dist/es/PNotifyConfirm';

import {Application} from '../../api/Application';

class Notification {

    static showSuccess(message) {
        PNotify.success({
            text: message,
            delay: Application.notice.delay,
            modules: {
                Buttons: buildButton(),
                Mobile: buildMobile()
            }
        });
    }

    static showError(errorCode, message) {
        PNotify.error({
            title: errorCode,
            text: message,
            delay: Application.notice.delay,
            modules: {
                Buttons: buildButton(),
                Mobile: buildMobile()
            }
        });
    }

    /**
     * notice的信息不支持消息自动消失，需要人为去点击
     */
    static showNotice(message) {
        let notice = PNotify.notice({
            text: message,
            hide: false,
            modules: {
                Buttons: {
                    closer: false,
                    sticker: false
                },
                Mobile: buildMobile()
            }
        });
        notice.on('click', () => {
            notice.close();
        });
    }

    static showInfo(message) {
        PNotify.info({
            text: message,
            delay: Application.notice.delay,
            modules: {
                Buttons: buildButton(),
                Mobile: buildMobile()
            }
        });
    }

    static showConfirm(confirmObject) {
        let self = this;
        let notice = PNotify.notice({
            title: confirmObject.title,
            text: confirmObject.text,
            icon: 'fas fa-question-circle',
            hide: false,
            stack: confirmObject.buildConfirmStack(),
            modules: {
              Confirm: {
                confirm: true
              },
              Buttons: {
                closer: false,
                sticker: false
              }
            }
          });
        notice.on('pnotify.confirm', () => {
        confirmObject.okAdaptor();
        self.showSuccess("操作成功");
            notice.close();
        });
        notice.on('pnotify.cancel', () => {
            notice.close();
        });
    }
}

// 写在组件外面表示私有，外部不可调用。
/**
 * 构建button组件
 */
function buildButton() {
    return {
        closer: Application.notice.button.closer,
        sticker: Application.notice.button.sticker,
        labels: Application.notice.button.labels
    }
};

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
 