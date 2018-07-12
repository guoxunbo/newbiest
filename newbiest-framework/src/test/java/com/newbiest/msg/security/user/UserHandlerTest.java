package com.newbiest.msg.security.user;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractHandlerTest;
import com.newbiest.main.FrameworkApplication;
import com.newbiest.msg.*;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.rest.user.UserRequest;
import com.newbiest.security.rest.user.UserRequestBody;
import com.newbiest.security.rest.user.UserResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by guoxunbo on 2018/1/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
@WebAppConfiguration
public class UserHandlerTest extends AbstractHandlerTest {

    private UserRequest userRequest;

    @Before
    public void init() {

        userRequest = new UserRequest();
        RequestHeader requestHeader = buildRequestHeader(UserRequest.MESSAGE_NAME);
        userRequest.setHeader(requestHeader);
    }

    /**
     * 创建用户测试
     */
    @Test
    public void create() throws Exception {
        NBUser user;
        try {
            UserRequestBody requestBody = new UserRequestBody();
            requestBody.setActionType(UserRequest.ACTION_CREATE);
            NBUser nbUser = new NBUser();
            nbUser.setUsername(MOCK_USERNAME);
            nbUser.setDescription("MockTest专用");
            nbUser.setPassword(MOCK_USERNAME);
            requestBody.setUser(nbUser);
            userRequest.setBody(requestBody);

            String jsonString = DefaultParser.writerJson(userRequest);
            MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
            requestBuilder.param("request", jsonString);
            String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            UserResponse userResponse = DefaultResponseParser.getObjectMapper().readerFor(UserResponse.class).readValue(responseString);
            assert responseString.contains(ResponseHeader.RESULT_SUCCESS);
            user = userResponse.getBody().getUser();
            assert user.getObjectRrn() != null;
        } catch (Exception e) {
            if (e.getCause() instanceof ClientException) {
                Assert.assertEquals("Value is exist : '1-mockTest'", ((ClientException) e.getCause()).getErrorCode().trim());
            } else {
                throw e;
            }
        } finally {
            user = getUserByName(MOCK_USERNAME);
            assert user != null;
            Assert.assertEquals("MockTest专用", user.getDescription());
        }
    }

    @Test
    public void update() throws Exception{
        NBUser nbUser = getUserByName(MOCK_USERNAME);
        if (nbUser == null) {
           create();
        }
        nbUser = getUserByName(MOCK_USERNAME);
        nbUser.setPhone("111111111111");
        nbUser.setEmail("aguo@glorysoft.com");

        UserRequestBody requestBody = new UserRequestBody();
        requestBody.setActionType(UserRequest.ACTION_UPDATE);
        requestBody.setUser(nbUser);
        userRequest.setBody(requestBody);

        String jsonString = DefaultParser.writerJson(userRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        UserResponse userResponse = DefaultRequestParser.getObjectMapper().readerFor(UserResponse.class).readValue(responseString);
        NBUser user = userResponse.getBody().getUser();

        assert user != null;
        Assert.assertEquals("111111111111", user.getPhone());
        Assert.assertEquals("aguo@glorysoft.com", user.getEmail());
    }

    @Test
    public void changePassword() throws Exception {
        String newPassword = "test22";
        NBUser nbUser = getUserByName(MOCK_USERNAME);
        nbUser.setNewPassword(newPassword);
        UserRequestBody requestBody = new UserRequestBody();
        requestBody.setActionType(UserRequest.ACTION_CHANGE_PASSWORD);
        requestBody.setUser(nbUser);
        userRequest.setBody(requestBody);

        String jsonString = DefaultParser.writerJson(userRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        UserResponse userResponse = DefaultRequestParser.getObjectMapper().readerFor(UserResponse.class).readValue(responseString);
        NBUser user = userResponse.getBody().getUser();

        assert user != null;
//        assert EncryptionUtils.matches(newPassword, user.getPassword());
    }

    @Test
    public void resetPassword() throws Exception {
        NBUser nbUser = getUserByName(MOCK_USERNAME);

        UserRequestBody requestBody = new UserRequestBody();
        requestBody.setActionType(UserRequest.ACTION_RESET_PASSWORD);
        requestBody.setUser(nbUser);
        userRequest.setBody(requestBody);

        String jsonString = DefaultParser.writerJson(userRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        UserResponse userResponse = DefaultRequestParser.getObjectMapper().readerFor(UserResponse.class).readValue(responseString);
        NBUser user = userResponse.getBody().getUser();

        assert user != null;
//        assert EncryptionUtils.matches("111111", user.getPassword());
    }

    @Test
    public void deleteUser() throws Exception {
        NBUser nbUser = getUserByName(MOCK_USERNAME);
        deleteEntity(NBUser.class.getName(), DefaultParser.getObjectMapper().writeValueAsString(nbUser));
    }

    public NBUser getUserByName(String userName) throws Exception {
        UserRequestBody requestBody = new UserRequestBody();
        requestBody.setActionType(UserRequest.ACTION_GET_BY_RRN);
        NBUser nbUser = new NBUser();
        nbUser.setUsername(userName);
        requestBody.setUser(nbUser);

        userRequest.setBody(requestBody);
        String jsonString = DefaultParser.writerJson(userRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);
        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        UserResponse userResponse = DefaultRequestParser.getObjectMapper().readerFor(UserResponse.class).readValue(responseString);
        return userResponse.getBody().getUser();
    }

    @Test
    public void getAuthorities() throws Exception {
        UserRequestBody requestBody = new UserRequestBody();
        requestBody.setActionType(UserRequest.ACTION_GET_AUTHORITY);
        NBUser nbUser = new NBUser();
        nbUser.setUsername(MOCK_USERNAME);
        requestBody.setUser(nbUser);

        userRequest.setBody(requestBody);
        String jsonString = DefaultParser.writerJson(userRequest);
        MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder();
        requestBuilder.param("request", jsonString);

        String responseString = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        UserResponse userResponse = DefaultRequestParser.getObjectMapper().readerFor(UserResponse.class).readValue(responseString);
        NBUser user = userResponse.getBody().getUser();
        assert user.getAuthorities() != null && user.getAuthorities().size() == 1 ;
        Assert.assertEquals("UserManager", user.getAuthorities().get(0).getSubAuthorities().get(0).getName());
    }

}