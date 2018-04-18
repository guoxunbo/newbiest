package com.newbiest.base.repository.custom;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.MethodMonitor;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.*;
import com.newbiest.main.MailService;
import com.newbiest.security.model.*;
import com.newbiest.security.repository.RoleRepository;
import com.newbiest.security.repository.custom.UserRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NBTABLE操作相关类
 * Created by guoxunbo on 2017/9/27.
 */
public interface TableRepositoryCustom {

   NBTable getDeepTable(long tableRrn) throws ClientException;

}
