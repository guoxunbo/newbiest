package com.newbiest.base.rest.entityList;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对实体做增删改查 状态切换操作
 * Created by guoxunbo on 2018/7/26.
 */
@RestController
@RequestMapping("/framework")
@Slf4j
@Api(value="/framework", tags="FrameworkService", description = "对实体进行默认的增删改查操作")
public class EntityListController extends AbstractRestController {

    @Autowired
    private BaseService baseService;

    @ApiOperation(value = "对实体做列表查询")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "EntityListRequest")
    @RequestMapping(value = "/entityListManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public EntityListResponse execute(@RequestBody EntityListRequest request) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        EntityListResponse response = new EntityListResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        EntityListResponseBody responseBody = new EntityListResponseBody();

        EntityListRequestBody requestBody = request.getBody();

        String entityModel = requestBody.getEntityModel();
        int firstResult = requestBody.getFirstResult();
        int maxResult = requestBody.getMaxResult();
        String orderBy = requestBody.getOrderBy();
        String whereClause = requestBody.getWhereClause();

        //TODO 暂时不处理根据field查询
        List<String> properties = requestBody.getFields();

        List<? extends NBBase> dataList = baseService.findAll(entityModel, firstResult, maxResult, whereClause, orderBy, sc.getOrgRrn());

        responseBody.setDataList(dataList);
        response.setBody(responseBody);
        return response;
    }


}
