import {i18Messages} from '../const/i18n';
import {Language} from '../const/ConstDefine';

import { SessionContext } from '../Application';


/**
 * 国际化获取信息
 */
export default class I18NUtils {

    /**
     * 根据messageCode获取前端定义的一些国际化信息
     * @param messageCode 消息码
     */
    static getClientMessage(messageCode) {
        let message = i18Messages[messageCode];
        if (message) {
            let language = SessionContext.getLanguage();
            if (!language) {
                language = Language.Chinese;
            }
            return message[language];
        } else {
            return messageCode;
        }
    }

    /**
     * 根据messageCode去请求Server定义的一些国际化信息
     * @param {消息码} messageCode 
     */
    static getServerMessgae(messageCode) {

    }
}