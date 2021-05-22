package com.xlg.component;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-03
 */
@SpringBootTest
public class XlgTaskTest {

//    @org.junit.jupiter.api.Test
//    void generator() {
//        Map<String, List<String>> map = Maps.newHashMap();
//        map.put("com",
//                Lists.newArrayList("operation_task_group_user_show", "operation_task_user_show"));
//        /*map.put("com.kuaishou.operation.light.component.light",
//                Lists.newArrayList("operation_light_config_formula_factor",
//                        "operation_light_grade"
//                ));*/
//
//        /*map.put("com.kuaishou.operation.activity.component.activity",
//                Lists.newArrayList("operation_condition", "operation_reward", "operation_condition_item"));*/
//
//        CodeGenerator codeGenerator = new CodeGenerator("bjpg-d191.yz02", 15007,
//                "test_rw", "54rltyi5BCdcm06wu22A0brvvzU5uDgB");
//        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//            codeGenerator.generator(entry.getKey(), "gifshow",
//                    entry.getValue().toArray(new String[0]), "/Users/easy/autocode");
//        }
//    }

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @Test
    public void redisTest() {

    }

}
