package com.newbiest.calendar.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 交接班->现场安全
 * Created by guoxunbo on 2019/4/19.
 */
@Entity
@Table(name="DMS_CHANGE_SHIFT_SECURITY_INFO")
@Data
public class ChangeShiftSecurityInfo extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="CHANGE_SHIFT_RRN")
    private Long changeShiftRrn;

    /**
     * 现场6S检查
     */
    @Column(name="ON_SITE_6S_FLAG")
    private String onSite6sFlag;

    /**
     * 是否发生工伤
     */
    @Column(name="INJURY_FLAG")
    private String injuryFlag;

    /**
     * 是否穿戴劳保用品
     */
    @Column(name="WARE_FLAG")
    private String wareFlag;

    /**
     * 是否领用消耗品
     */
    @Column(name="PICK_CONSUMABLE_FLAG")
    private String pickConsumableFlag;

    /**
     * 是否完成领导指示
     */
    @Column(name="LEADER_SHIP_FINISH_FLAG")
    private String leaderShipFinishFlag;

    /**
     * 是否跨部门
     */
    @Column(name="INTERDEPARTMENTAL_FLAG")
    private String interdepartmentalFlag;

    /**
     * 是否交接
     */
    @Column(name="HANDOVER_FLAG")
    private String handoverFlag;

    @Column(name="COMMENT")
    private String comment;

    @Column(name="RESERVED1")
    private String reserved1;

    @Column(name="RESERVED2")
    private String reserved2;

    @Column(name="RESERVED3")
    private String reserved3;

    @Column(name="RESERVED4")
    private String reserved4;

    @Column(name="RESERVED5")
    private String reserved5;

    @Column(name="RESERVED6")
    private String reserved6;

    @Column(name="RESERVED7")
    private String reserved7;

    @Column(name="RESERVED8")
    private String reserved8;

    @Column(name="RESERVED9")
    private String reserved9;

    @Column(name="RESERVED10")
    private String reserved10;

    public Boolean getOnSite6sFlag() {
        return StringUtils.YES.equalsIgnoreCase(onSite6sFlag);
    }

    public void setOnSite6sFlag(Boolean onSite6sFlag) {
        this.onSite6sFlag = onSite6sFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getInjuryFlag() {
        return StringUtils.YES.equalsIgnoreCase(injuryFlag);
    }

    public void setInjuryFlag(Boolean injuryFlag) {
        this.injuryFlag = injuryFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getWareFlag() {
        return StringUtils.YES.equalsIgnoreCase(wareFlag);
    }

    public void setWareFlag(Boolean wareFlag) {
        this.wareFlag = wareFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getPickConsumableFlag() {
        return StringUtils.YES.equalsIgnoreCase(pickConsumableFlag);
    }

    public void setPickConsumableFlag(Boolean pickConsumableFlag) {
        this.pickConsumableFlag = pickConsumableFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getLeaderShipFinishFlag() {
        return StringUtils.YES.equalsIgnoreCase(leaderShipFinishFlag);
    }

    public void setLeaderShipFinishFlag(Boolean leaderShipFinishFlag) {
        this.leaderShipFinishFlag = leaderShipFinishFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getInterdepartmentalFlag() {
        return StringUtils.YES.equalsIgnoreCase(interdepartmentalFlag);
    }

    public void setInterdepartmentalFlag(Boolean interdepartmentalFlag) {
        this.interdepartmentalFlag = interdepartmentalFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getHandoverFlag() {
        return StringUtils.YES.equalsIgnoreCase(handoverFlag);
    }

    public void setHandoverFlag(Boolean handoverFlag) {
        this.handoverFlag = handoverFlag ? StringUtils.YES : StringUtils.NO;
    }
}
