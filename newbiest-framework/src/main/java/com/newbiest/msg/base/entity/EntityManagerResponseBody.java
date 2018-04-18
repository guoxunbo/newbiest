package com.newbiest.msg.base.entity;

import com.newbiest.base.model.NBBase;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class EntityManagerResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private NBBase data;

}
