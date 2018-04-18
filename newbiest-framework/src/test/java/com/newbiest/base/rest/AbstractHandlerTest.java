package com.newbiest.base.rest;

import com.newbiest.msg.DefaultParser;
import com.newbiest.msg.RequestHeader;
import com.newbiest.msg.base.entity.EntityManagerRequest;
import com.newbiest.msg.base.entity.EntityManagerRequestBody;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by guoxunbo on 2018/1/26.
 */

public class AbstractHandlerTest {

    protected final Long DEFAULT_ORG_RRN = 1L;
    protected final String MOCK_USERNAME = "mockTest";

    protected MockMvc mockMvc;

    @Autowired
    private FrameworkService frameworkService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(frameworkService).build();
    }

    public RequestHeader buildRequestHeader(String messageName) {
        return buildRequestHeader(messageName, DEFAULT_ORG_RRN);
    }

    public RequestHeader buildRequestHeader(String messageName, long orgRrn) {
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setOrgRrn(orgRrn);
        requestHeader.setMessageName(messageName);
        requestHeader.setTransactionId(UUID.randomUUID().toString());
        requestHeader.setUsername(MOCK_USERNAME);
        return requestHeader;
    }

    public void deleteEntity(String entityName, String entityString) throws Exception{
        EntityManagerRequest entityManagerRequest = new EntityManagerRequest();
        entityManagerRequest.setHeader(buildRequestHeader(EntityManagerRequest.MESSAGE_NAME));

        EntityManagerRequestBody entityManagerRequestBody = new EntityManagerRequestBody();
        entityManagerRequestBody.setActionType(EntityManagerRequest.ACTION_DELETE);
        entityManagerRequestBody.setEntityName(entityName);
        entityManagerRequestBody.setEntityString(entityString);
        entityManagerRequest.setBody(entityManagerRequestBody);

        String jsonString = DefaultParser.writerJson(entityManagerRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }

    public MockHttpServletRequestBuilder buildRequestBuilder() {
        return post("/framework/execute/");
    }

}
