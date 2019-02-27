package com.newbiest.commom.sm.model;

/**
 * 需要状态机管理的具体获得状态模型，状态等的基类
 */
public interface StatusLifeCycle extends LifeCycle{

    Long getStatusModelRrn();

    /**
     * 取得状态大类
     * @return
     */
    String getStatusCategory();

    /**
     * 取得状态
     * @return
     */
    String getStatus();

    /**
     * 子状态
     * @return
     */
    String getSubStatus();

    void setStatusCategory(String stateCategory);

    void setStatus(String state);

    void setSubStatus(String subState);
}
