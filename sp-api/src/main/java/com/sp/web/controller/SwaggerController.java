package com.sp.web.controller;

import com.sp.common.annotation.Anonymous;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sp.common.core.controller.BaseController;

/**
 * swagger 接口
 *
 */
@Controller
@RequestMapping("/tool/swagger")
public class SwaggerController extends BaseController
{
    @Anonymous
    @GetMapping()
    public String index()
    {
        return redirect("/swagger-ui/index.html");
    }
}