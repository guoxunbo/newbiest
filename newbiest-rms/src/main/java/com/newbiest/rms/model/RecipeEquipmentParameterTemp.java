package com.newbiest.rms.model;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.threadlocal.SessionContext;
import com.newbiest.rms.exception.RmsException;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.temporal.ChronoUnit;

/**
 * 临时修改RecipeEquipment的Parameter值
 * @author guoxunbo
 *
 */
@Entity
@Table(name="RMS_RECIPE_EQUIPMENT_PARAM_TMP")
@Data
@NoArgsConstructor
public class RecipeEquipmentParameterTemp extends NBUpdatable {

	private static final long serialVersionUID = 1L;

	/**
	 * Count类型的失效 比如用几次了就失效了
	 */
	public static final String EXPIRED_POLICY_COUNT = "Count";

	/**
	 * 根据时间进行失效
	 */
	public static final String EXPIRED_POLICY_TIME = "Time";
	
	@Column(name="ECN_ID")
	private String ecnId;
	
	@Column(name="RECIPE_EQUIPMENT_RRN")
	private Long recipeEquipmentRrn;

	@Column(name="PARAMETER_NAME")
	private String parameterName;

	@Column(name="PARAMETER_GROUP")
	private String parameterGroup = RecipeEquipmentParameter.GROUP_DEFAULT;
	
	@Column(name="PARAMETER_VALUE")
	private String parameterValue;

	@Column(name="MAX_VALUE")
	private String maxValue;
	
	@Column(name="MIN_VALUE")
	private String minValue;

	@Column(name="STATUS")
	private String status;
	
	@Column(name="EXPIRED_POLICY")
	private String expiredPolicy = EXPIRED_POLICY_COUNT;
	
	@Column(name="CURRENT_COUNT")
	private Integer currentCount = 0;
	
	@Column(name="LIFE")
	private Integer life = 1;

	public void changeStatus() throws ClientException {
		try {
			if (RecipeEquipmentParameterTemp.EXPIRED_POLICY_COUNT.equals(this.getExpiredPolicy())) {
				if (this.getCurrentCount() >= this.getLife()) {
					setStatus(AbstractRecipeEquipment.STATUS_INACTIVE);
				}
			} else if (RecipeEquipmentParameterTemp.EXPIRED_POLICY_TIME.equals(this.getExpiredPolicy())) {
				if (DateUtils.now().after(DateUtils.plus(this.getCreated(), this.getLife(), ChronoUnit.DAYS))) {
					setStatus(AbstractRecipeEquipment.STATUS_INACTIVE);
				}
			} else {
				throw new ClientParameterException(RmsException.NONSUPPORT_EXPIRED_POLICY, this.getExpiredPolicy());
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
}
