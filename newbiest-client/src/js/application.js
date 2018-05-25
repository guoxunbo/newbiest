const Application = {
    name: '智行管理系统',
    version: "0.0.1",
    notice: {
        delay: 5000,
        // 构建button组件是否支持关闭 支持点击锁定
        button: {
            closer: true,
            sticker: true,
            labels: {
                close: 'Close', stick: 'Stick', unstick: 'Unstick'
            }
        },
        mobile: {
            swipeDismiss: true,
            styling: true
        }
    }

};

export {Application}
