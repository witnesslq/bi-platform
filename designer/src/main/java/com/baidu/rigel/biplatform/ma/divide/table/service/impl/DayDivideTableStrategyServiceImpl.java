

/**
 * Copyright (c) 2014 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.rigel.biplatform.ma.divide.table.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.baidu.rigel.biplatform.ac.minicube.DivideTableStrategyVo;
import com.baidu.rigel.biplatform.ac.util.TimeRangeDetail;
import com.baidu.rigel.biplatform.ma.comm.util.ParamValidateUtils;
import com.baidu.rigel.biplatform.ma.divide.table.service.DivideTableService;
import com.baidu.rigel.biplatform.ma.divide.table.service.TimeDivideTableUtils;
import com.google.common.collect.Sets;

/** 
 *  按日分表策略
 * @author yichao.jiang 
 * @version  2015年6月17日 
 * @since jdk 1.8 or after
 */
public class DayDivideTableStrategyServiceImpl implements DivideTableService {
    
    /**
     * 按日分表策略返回所有事实表名称<br>
     * {@inheritDoc}
     */
    @Override
    public String getAllFactTableName(DivideTableStrategyVo divideTableStrategy, Map<String, Object> context) {
        // 校验参数是否合理
        if (!ParamValidateUtils.check("divideTableStrategy", divideTableStrategy)) {
            return null;
        }
        if (!ParamValidateUtils.check("context", context)) {
            return null;
        }       
        String timeJson = TimeDivideTableUtils.getTimeJsonFromContext(context);
        if (timeJson != null) {
            return this.getFactTableName(divideTableStrategy, timeJson);            
        } 
        return null;
    }
    
    /**
     * 根据时间参数，取得事实表构成的字符串
     * getFactTableName
     * @param startegy
     * @param timeJson
     * @return
     */
    private String getFactTableName(DivideTableStrategyVo strategy, String timeJson) {
        String start;
        String end;
        String result = null;
        try {
            JSONObject json = new JSONObject(timeJson);
            start = json.getString("start").replace("-", "");
            end = json.getString("end").replace("-", "");
            TimeRangeDetail range = new TimeRangeDetail(start, end);
            String[] days = range.getDays();
            Set<String> tableNames = Sets.newTreeSet();
            for (int i = 0; i < days.length; i++) {
                tableNames.add(strategy.getPrefix() + days[i]);
            }
            result = tableNames.stream().collect(Collectors.joining(","));
        } catch (Exception e) {
            throw new RuntimeException("exception happend when get detail time!");
        }
        return result;
    }
}
