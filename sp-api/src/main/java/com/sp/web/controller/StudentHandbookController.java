package com.sp.web.controller;

import com.sp.common.annotation.Log;
import com.sp.common.core.controller.BaseController;
import com.sp.common.core.domain.AjaxResult;
import com.sp.common.core.page.TableDataInfo;
import com.sp.common.enums.BusinessType;
import com.sp.system.entity.ClassLog;
import com.sp.system.entity.ParentStudentRelation;
import com.sp.system.service.IParentStudentRelationService;
import com.sp.system.service.TokenService;
import com.sp.web.service.ClassLogTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生手册Controller
 *
 */
@RestController
@RequestMapping("/system/handbook")
public class StudentHandbookController extends BaseController {

    @Autowired
    private ClassLogTransferService classLogTransferService;

    @Autowired
    private IParentStudentRelationService parentStudentRelationService;

    @Autowired
    private TokenService tokenService;

    @Log(title = "查询课程日志列表", businessType = BusinessType.SELECT)
    @GetMapping("/list")
    public TableDataInfo list() {
        try {
            // 验证token
            String parentUserId = validateToken();
            if (parentUserId == null) {
                return createUnauthorizedResponse();
            }
            
            // 从外部class_log表获取数据
            List<ClassLog> classLogs = classLogTransferService.getAllClassLogsFromExternal();

            return createSuccessResponse(classLogs);
        } catch (Exception e) {
            logger.error("获取课程日志列表失败: {}", e.getMessage());
            return createErrorResponse();
        }
    }

    @Log(title = "查询过去一个月课程日志列表", businessType = BusinessType.SELECT)
    @GetMapping("/pastMonth")
    public TableDataInfo listPastMonth() {
        try {
            // 验证token
            String parentUserId = validateToken();
            if (parentUserId == null) {
                return createUnauthorizedResponse();
            }
            
            // 从外部class_log表获取过去一个月的数据
            List<ClassLog> classLogs = classLogTransferService.getPastMonthClassLogs();

            return createSuccessResponse(classLogs);
        } catch (Exception e) {
            logger.error("获取过去一个月课程日志列表失败: {}", e.getMessage());
            return createErrorResponse();
        }
    }

    @Log(title = "获取课程日志详细信息", businessType = BusinessType.SELECT)
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        // 验证token
        String parentUserId = validateToken();
        if (parentUserId == null) {
            return AjaxResult.error("无效的访问令牌或用户未登录");
        }
        
        // 从外部class_log表获取指定ID的数据
        List<ClassLog> classLogs = classLogTransferService.getAllClassLogsFromExternal();
        ClassLog classLog = classLogs.stream()
                .filter(log -> id.equals(log.getId()))
                .findFirst()
                .orElse(null);

        return AjaxResult.success(classLog);
    }

    @Log(title = "获取当前家长关联的学生列表", businessType = BusinessType.SELECT)
    @GetMapping("/students")
    public AjaxResult getRelatedStudents() {
        try {
            // 验证token
            String parentUserId = validateToken();
            if (parentUserId == null) {
                return AjaxResult.error("无效的访问令牌或用户未登录");
            }

            // 根据家长用户ID查询关联的学生
            List<ParentStudentRelation> relations = parentStudentRelationService.selectByParentId(parentUserId);

            // 返回完整的relations数据，前端可以从里面获取studentName
            return AjaxResult.success(relations);
        } catch (Exception e) {
            logger.error("获取关联学生列表失败: {}", e.getMessage());
            return AjaxResult.error("获取学生列表失败: " + e.getMessage());
        }
    }

    @Log(title = "切换学生", businessType = BusinessType.UPDATE)
    @PostMapping("/switchStudent")
    public AjaxResult switchStudent(@RequestBody Map<String, String> requestBody) {
        try {
            // 验证token
            String parentUserId = validateToken();
            if (parentUserId == null) {
                return AjaxResult.error("无效的访问令牌或用户未登录");
            }

            String studentName = requestBody.get("studentName");
            String studentUserId = requestBody.get("studentUserId");
            if (studentName == null || studentName.isEmpty()) {
                return AjaxResult.error("学生姓名不能为空");
            }
            if (studentUserId == null || studentUserId.isEmpty()) {
                return AjaxResult.error("学生用户ID不能为空");
            }

            // 验证家长是否确实关联了该学生
            List<ParentStudentRelation> relations = parentStudentRelationService.selectByParentId(parentUserId);
            boolean studentExists = relations.stream()
                    .anyMatch(relation -> studentName.equals(relation.getStudentName()) && 
                           studentUserId.equals(relation.getStudentUserId()));

            if (!studentExists) {
                return AjaxResult.error("家长未关联该学生，无法切换");
            }

            return AjaxResult.success("切换学生成功", studentName);
        } catch (Exception e) {
            logger.error("切换学生失败: {}", e.getMessage());
            return AjaxResult.error("切换学生失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证请求中的token
     * @return 验证成功返回家长用户ID，验证失败返回null
     */
    protected String validateToken() {
        try {
            // 从请求头中获取token
            String token = getRequest().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                // 尝试从参数中获取
                token = getRequest().getParameter("token");
            }

            if (token == null || token.isEmpty()) {
                logger.error("验证token失败: 缺少访问令牌");
                return null;
            }

            // 验证token是否有效并获取家长ID
            String parentUserId = tokenService.getParentUserIdByToken(token);
            if (parentUserId == null) {
                logger.error("验证token失败: 无效的访问令牌或用户未登录");
                return null;
            }
            
            return parentUserId;
        } catch (Exception e) {
            logger.error("验证token时发生异常: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 创建成功响应
     * @param classLogs 课程日志列表
     * @return TableDataInfo
     */
    private TableDataInfo createSuccessResponse(List<ClassLog> classLogs) {
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setCode(200);
        dataTable.setRows(classLogs);
        dataTable.setTotal(classLogs.size());
        return dataTable;
    }
    
    /**
     * 创建未授权响应
     * @return TableDataInfo
     */
    private TableDataInfo createUnauthorizedResponse() {
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setCode(401); // 未授权
        dataTable.setRows(java.util.Collections.emptyList());
        dataTable.setTotal(0);
        return dataTable;
    }
    
    /**
     * 创建错误响应
     * @return TableDataInfo
     */
    private TableDataInfo createErrorResponse() {
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setCode(200);
        dataTable.setRows(java.util.Collections.emptyList());
        dataTable.setTotal(0);
        return dataTable;
    }
}