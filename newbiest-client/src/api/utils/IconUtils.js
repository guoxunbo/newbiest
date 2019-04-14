import { Icon } from "antd";

const CutsomIcon = Icon.createFromIconfontCN({
    scriptUrl: require('./iconfont.js'),
});

const CustomIconPrefix = "icon-";
/**
 * Icon工具类
 */
export default class IconUtils {

    /**
     * 创建icon 当iconName为icon-开头的时候。表示为定制化icon。
     * @param iconName icon的名称
     * @param theme 'filled' 实心 | 'outlined' 空心 | 'twoTone' 双色
     * 
     */
    static buildIcon = (iconName, theme, style) => {
        if (iconName.startsWith(CustomIconPrefix)) {
            return <CutsomIcon style={style} theme={theme} type={iconName}></CutsomIcon>
        }
        return <Icon style={style} theme={theme} type={iconName}/>
    }


}