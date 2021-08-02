package com.newbiest.vanchip.rest.labmaterial.imp;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.mms.model.LabMaterial;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.repository.MaterialStatusModelRepository;
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.mms.utils.CsvUtils;
import com.newbiest.ui.model.NBTable;
import com.newbiest.vanchip.service.VanChipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="VC客制化", description = "物料管理相关")
public class LabMaterialImportController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "LabMaterialImportRequest")
    @RequestMapping(value = "/labMaterialImport", method = RequestMethod.POST)
    public LabMaterialImportResponse excute(@RequestParam MultipartFile file, @RequestParam String request)throws Exception {
        LabMaterialImportRequest labMaterialImportRequest = DefaultParser.getObjectMapper().readerFor(LabMaterialImportRequest.class).readValue(request);
        LabMaterialImportResponse response = new LabMaterialImportResponse();
        response.getHeader().setTransactionId(labMaterialImportRequest.getHeader().getTransactionId());
        LabMaterialImportResponseBody responseBody = new LabMaterialImportResponseBody();

        NBTable nbTable = uiService.getDeepTable(labMaterialImportRequest.getBody().getImportNBTableObjectRrn());
        BiMap<String, String> fieldMap = HashBiMap.create(CsvUtils.buildHeaderByTable(nbTable, labMaterialImportRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();

        CsvUtils.validateImportFile(fieldMap, file.getInputStream(), nbTable);
        List<LabMaterial> datas = (List) CsvUtils.importCsv(nbTable, getClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), StringUtils.SPLIT_COMMA);

        MaterialStatusModel statusModel = materialStatusModelRepository.findOneByName(Material.DEFAULT_STATUS_MODEL);
        if (statusModel == null) {
            throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
        }
        List<LabMaterial> LabMaterials = Lists.newArrayList();
        for (LabMaterial labMaterial : datas) {
            labMaterial.setStatusModelRrn(statusModel.getObjectRrn());
            labMaterial = vanChipService.saveLabMaterial(labMaterial);
            LabMaterials.add(labMaterial);
        }
        responseBody.setDataList(LabMaterials);
        response.setBody(responseBody);
        return response;
    }

}
