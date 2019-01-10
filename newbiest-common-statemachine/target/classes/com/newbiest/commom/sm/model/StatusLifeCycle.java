package com.newbiest.commom.sm.model;

/**
 * 需要状态机管理的具体获得状态模型，状态等的基类
 */
public interface StatusLifeCycle extends LifeCycle{

    Long getStatusModelRrn();

    StatusModel getStatusModel();

    void setStatusModel(StatusModel statusModel);

    /**
     * 取得状态大类
     * @return
     */
    String getStateCategory();

    /**
     * 取得状态
     * @return
     */
    String getState();

    /**
     * 子状态
     * @return
     */
    String getSubState();

    void setStateCategory(String stateCategory);

    void setState(String state);

    void setSubState(String subState);
}
