package com.sp.web.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sp.common.utils.WeChatWorkSchoolUtils;
import com.sp.common.utils.http.HttpUtils;
import com.sp.system.entity.DepartmentParentBinding;
import com.sp.system.entity.ParentStudentRelation;
import com.sp.system.service.DepartmentParentBindingService;
import com.sp.system.service.IParentStudentRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 家长学生关系同步定时任务
 *
 */
@Component
public class ParentStudentRelationSyncTask {

    private static final Logger logger = LoggerFactory.getLogger(ParentStudentRelationSyncTask.class);

    @Autowired
    private WeChatWorkSchoolUtils weChatWorkSchoolUtils;

    @Autowired
    private IParentStudentRelationService parentStudentRelationService;

    @Autowired
    private DepartmentParentBindingService departmentParentBindingService;

    private static final AtomicBoolean isExecuting = new AtomicBoolean(false);

    /**
     * 每天凌晨0点执行部门家长数据同步
     *
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void syncParentStudentRelationData() {
        // 使用AtomicBoolean确保同一时间只有一个实例在执行
        if (!isExecuting.compareAndSet(false, true)) {
            logger.info("家长学生关系同步任务已在执行中，跳过本次执行");
            return;
        }
        
        try {
            logger.info("开始执行家长学生关系同步任务，部门ID: 16593");

            // 获取access_token并调用获取部门家长列表接口
            String accessToken = weChatWorkSchoolUtils.getAccessToken();
            String parentListUrl = "https://qyapi.weixin.qq.com/cgi-bin/school/user/list_parent?access_token=" + accessToken + "&department_id=16593";

            String parentResponse = HttpUtils.sendGet(parentListUrl);
            logger.info("获取部门家长列表结果: {}", parentResponse);

            // 解析家长列表并保存到数据库
            JSONObject parentJson = JSONObject.parseObject(parentResponse);
            if (parentJson.getInteger("errcode") != null && parentJson.getInteger("errcode") == 0) {
                JSONArray parentsArray = parentJson.getJSONArray("parents");
                if (parentsArray != null && !parentsArray.isEmpty()) {
                    logger.info("成功获取到 {} 个家长信息", parentsArray.size());
                    
                    // 清除旧的部门家长绑定数据
                    departmentParentBindingService.deleteByDepartmentId(16593L);
                    
                    // 遍历家长数据并保存到数据库
                    for (int i = 0; i < parentsArray.size(); i++) {
                        JSONObject parentObj = parentsArray.getJSONObject(i);
                        
                        String parentUserId = parentObj.getString("parent_userid");
                        String mobile = parentObj.getString("mobile");
                        String externalUserid = parentObj.getString("external_userid");
                        
                        // 创建部门家长绑定记录
                        DepartmentParentBinding binding = new DepartmentParentBinding();
                        binding.setDepartmentId(16593L);
                        binding.setParentUserId(parentUserId);
                        binding.setCreateTime(LocalDateTime.now());
                        binding.setUpdateTime(LocalDateTime.now());
                        
                        boolean bindingResult = departmentParentBindingService.insertIfNotExists(binding);
                        if (bindingResult) {
                            logger.info("  已创建/确认部门家长绑定记录: parentUserId={}, departmentId={}", parentUserId, 16593L);
                        } else {
                            logger.warn("  创建部门家长绑定记录失败: parentUserId={}, departmentId={}", parentUserId, 16593L);
                        }
                        
                        // 处理孩子信息数组
                        JSONArray childrenArray = parentObj.getJSONArray("children");
                        if (childrenArray != null && !childrenArray.isEmpty()) {
                            
                            for (int j = 0; j < childrenArray.size(); j++) {
                                JSONObject childObj = childrenArray.getJSONObject(j);
                                
                                String studentUserId = childObj.getString("student_userid");
                                String relation = childObj.getString("relation");
                                String name = childObj.getString("name");
                                
                                // 使用安全插入方法处理家长学生关系记录（如果不存在则插入，否则更新）
                                ParentStudentRelation relationEntity = new ParentStudentRelation();
                                relationEntity.setParentUserId(parentUserId);
                                relationEntity.setStudentUserId(studentUserId);
                                relationEntity.setStudentName(name);
                                relationEntity.setRelationDesc(relation);
                                relationEntity.setMobile(mobile);
                                relationEntity.setExternalUserid(externalUserid);
                                relationEntity.setCreateTime(LocalDateTime.now());
                                relationEntity.setUpdateTime(LocalDateTime.now());
                                
                                int result = parentStudentRelationService.insertIfNotExists(relationEntity);
                                if (result >= 0) {
                                    logger.info("    已创建/更新家长学生关系记录: parentUserId={}, studentUserId={}", parentUserId, studentUserId);
                                } else {
                                    logger.warn("    创建/更新家长学生关系记录失败: parentUserId={}, studentUserId={}", parentUserId, studentUserId);
                                }
                            }
                        } else {
                            logger.info("  孩子数量: 0");
                        }
                    }
                    
                    logger.info("家长数据同步完成，共处理 {} 个家长", parentsArray.size());
                } else {
                    logger.info("部门家长列表为空");
                }
            } else {
                logger.error("获取部门家长列表失败: {}", parentJson.getString("errmsg"));
            }
        } catch (Exception e) {
            logger.error("同步家长学生关系数据失败", e);
        } finally {
            // 确保执行完成后释放锁
            isExecuting.set(false);
        }

        logger.info("家长学生关系同步任务执行完成");
    }
}