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
import java.util.stream.Collectors;

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
            // 从外部class_log表获取数据
            List<ClassLog> classLogs = classLogTransferService.getAllClassLogsFromExternal();
            
            // 构造TableDataInfo返回
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(200);
            dataTable.setRows(classLogs);
            dataTable.setTotal(classLogs.size());
            
            return dataTable;
        } catch (Exception e) {
            logger.error("获取课程日志列表失败: {}", e.getMessage());
            
            // 构造TableDataInfo返回空数据
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(200);
            dataTable.setRows(java.util.Collections.emptyList());
            dataTable.setTotal(0);
            
            return dataTable;
        }
    }
    
    @Log(title = "获取课程日志详细信息", businessType = BusinessType.SELECT)
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
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
            // 从请求头中获取token
            String token = getRequest().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                // 尝试从参数中获取
                token = getRequest().getParameter("token");
            }

            if (token == null || token.isEmpty()) {
                return AjaxResult.error("缺少访问令牌");
            }

            // 通过token获取家长用户ID
            String parentUserId = tokenService.getParentUserIdByToken(token);
            if (parentUserId == null) {
                return AjaxResult.error("无效的访问令牌或用户未登录");
            }

            // 根据家长用户ID查询关联的学生
            List<ParentStudentRelation> relations = parentStudentRelationService.selectByParentId(parentUserId);
            
            // 提取学生姓名列表
            List<String> studentNames = relations.stream()
                .map(ParentStudentRelation::getStudentName)
                .distinct()
                .collect(Collectors.toList());

            return AjaxResult.success(studentNames);
        } catch (Exception e) {
            logger.error("获取关联学生列表失败: {}", e.getMessage());
            return AjaxResult.error("获取学生列表失败: " + e.getMessage());
        }
    }

    @Log(title = "切换学生", businessType = BusinessType.UPDATE)
    @PostMapping("/switch-student")
    public AjaxResult switchStudent(@RequestBody Map<String, String> requestBody) {
        try {
            String token = getRequest().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                // 尝试从参数中获取
                token = getRequest().getParameter("token");
            }

            if (token == null || token.isEmpty()) {
                return AjaxResult.error("缺少访问令牌");
            }

            String studentName = requestBody.get("studentName");
            if (studentName == null || studentName.isEmpty()) {
                return AjaxResult.error("学生姓名不能为空");
            }

            // 验证token是否有效并获取家长ID
            String parentUserId = tokenService.getParentUserIdByToken(token);
            if (parentUserId == null) {
                return AjaxResult.error("无效的访问令牌或用户未登录");
            }

            // 验证家长是否确实关联了该学生
            List<ParentStudentRelation> relations = parentStudentRelationService.selectByParentId(parentUserId);
            boolean studentExists = relations.stream()
                .anyMatch(relation -> studentName.equals(relation.getStudentName()));

            if (!studentExists) {
                return AjaxResult.error("家长未关联该学生，无法切换");
            }

            // 在这里可以添加逻辑来存储当前选择的学生（如保存到session或缓存）
            // 例如，可以使用Redis或Session来存储当前选择的学生信息
            // request.getSession().setAttribute("currentStudent", studentName);

            return AjaxResult.success("切换学生成功", studentName);
        } catch (Exception e) {
            logger.error("切换学生失败: {}", e.getMessage());
            return AjaxResult.error("切换学生失败: " + e.getMessage());
        }
    }
}