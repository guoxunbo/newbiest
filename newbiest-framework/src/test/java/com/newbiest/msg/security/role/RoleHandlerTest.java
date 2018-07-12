package com.newbiest.msg.security.role;

import com.google.common.collect.Lists;
import com.newbiest.base.rest.AbstractHandlerTest;
import com.newbiest.main.FrameworkApplication;
import com.newbiest.msg.*;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBUser;
import org.junit.Before;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by guoxunbo on 2018/2/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
@WebAppConfiguration
public class RoleHandlerTest extends AbstractHandlerTest {

    private RoleRequest roleRequest;

    private static final String MOCK_ROLE_NAME = "MockTestRole";

    @Before
    public void init() {
        roleRequest = new RoleRequest();
        RequestHeader requestHeader = buildRequestHeader(RoleRequest.MESSAGE_NAME);
        roleRequest.setHeader(requestHeader);
    }

    @Test
    public void create() throws Exception {
        RoleRequestBody requestBody = new RoleRequestBody();
        requestBody.setActionType(RoleRequest.ACTION_CREATE);

        NBRole nbRole = new NBRole();
        nbRole.setRoleId(MOCK_ROLE_NAME);
        nbRole.setDescription("MockTest用户组");

        List<NBUser> nbUsers = Lists.newArrayList();
        NBUser nbUser = new NBUser();
        nbUser.setObjectRrn(75L);
        nbUsers.add(nbUser);
        nbRole.setUsers(nbUsers);

        requestBody.setNbRole(nbRole);
        roleRequest.setBody(requestBody);
        String jsonString = DefaultParser.writerJson(roleRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        RoleResponse roleResponse = DefaultResponseParser.getObjectMapper().readerFor(RoleResponse.class).readValue(responseString);
        assert responseString.contains(ResponseHeader.RESULT_SUCCESS);
        NBRole role = roleResponse.getBody().getNbRole();
        assert role.getObjectRrn() != null;
    }

    @Test
    public void update() throws Exception{
        NBRole nbRole = getRoleByName(MOCK_ROLE_NAME);
        assert nbRole != null;
        assert nbRole.getUsers() != null && nbRole.getUsers().size() == 1;
        nbRole.setCreatedBy("admin");

        RoleRequestBody requestBody = new RoleRequestBody();
        requestBody.setActionType(RoleRequest.ACTION_UPDATE);
        requestBody.setNbRole(nbRole);
        roleRequest.setBody(requestBody);

        String jsonString = DefaultParser.writerJson(roleRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        RoleResponse roleResponse = DefaultResponseParser.getObjectMapper().readerFor(RoleResponse.class).readValue(responseString);
        assert responseString.contains(ResponseHeader.RESULT_SUCCESS);
        NBRole role = roleResponse.getBody().getNbRole();
        assert nbRole.getUsers() != null && nbRole.getUsers().size() == 1;

    }

    @Test
    public void dispatchAuthority() throws Exception{
        NBRole nbRole = getRoleByName(MOCK_ROLE_NAME);
        assert nbRole != null;
        assert nbRole.getUsers() != null && nbRole.getUsers().size() == 1;

        RoleRequestBody requestBody = new RoleRequestBody();
        requestBody.setActionType(RoleRequest.DISPATCH_AUTHORITY);

        List<NBAuthority> nbAuthorities = Lists.newArrayList();
        NBAuthority nbAuthority = new NBAuthority();
        nbAuthority.setObjectRrn(3L);
        nbAuthorities.add(nbAuthority);

        nbAuthority = new NBAuthority();
        nbAuthority.setObjectRrn(4L);
        nbAuthorities.add(nbAuthority);

        nbRole.setAuthorities(nbAuthorities);
        requestBody.setNbRole(nbRole);
        roleRequest.setBody(requestBody);

        String jsonString = DefaultParser.writerJson(roleRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        RoleResponse roleResponse = DefaultResponseParser.getObjectMapper().readerFor(RoleResponse.class).readValue(responseString);
        assert responseString.contains(ResponseHeader.RESULT_SUCCESS);
        NBRole role = roleResponse.getBody().getNbRole();
        assert nbRole.getUsers() != null && nbRole.getUsers().size() == 1;
        assert nbRole.getAuthorities() != null && nbRole.getAuthorities().size() == 2;
    }

    @Test
    public void dispatchUser() throws Exception{
        NBRole nbRole = getRoleByName(MOCK_ROLE_NAME);
        assert nbRole != null;
        assert nbRole.getUsers() != null && nbRole.getUsers().size() == 1;
        assert nbRole.getAuthorities() != null && nbRole.getAuthorities().size() == 2;

        RoleRequestBody requestBody = new RoleRequestBody();
        requestBody.setActionType(RoleRequest.DISPATCH_USER);

        List<NBUser> nbUsers = Lists.newArrayList();
        NBUser nbUser = new NBUser();
        nbUser.setObjectRrn(32L);
        nbUsers.add(nbUser);

        nbUser = new NBUser();
        nbUser.setObjectRrn(75L);
        nbUsers.add(nbUser);

        nbRole.setUsers(nbUsers);
        requestBody.setNbRole(nbRole);
        roleRequest.setBody(requestBody);

        String jsonString = DefaultParser.writerJson(roleRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        RoleResponse roleResponse = DefaultResponseParser.getObjectMapper().readerFor(RoleResponse.class).readValue(responseString);
        assert responseString.contains(ResponseHeader.RESULT_SUCCESS);
        NBRole role = roleResponse.getBody().getNbRole();
        assert nbRole.getUsers() != null && nbRole.getUsers().size() == 2;
        assert nbRole.getAuthorities() != null && nbRole.getAuthorities().size() == 2;
    }


    public NBRole getRoleByName(String roleName) throws Exception {
        RoleRequestBody requestBody = new RoleRequestBody();
        requestBody.setActionType(RoleRequest.ACTION_GET_BY_ID);

        NBRole nbRole = new NBRole();
        nbRole.setRoleId(roleName);
        requestBody.setNbRole(nbRole);

        roleRequest.setBody(requestBody);
        String jsonString = DefaultParser.writerJson(roleRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        RoleResponse roleResponse = DefaultRequestParser.getObjectMapper().readerFor(RoleResponse.class).readValue(responseString);
        return roleResponse.getBody().getNbRole();
    }

}