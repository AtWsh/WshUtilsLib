package it.wsh.cn.wshutilslib.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by angeliahuang on 2018/6/15.
 */
public class ListUtils
{
    /**
     * 新数据去除重复
     * @param oldData
     * @param insertData
     * @return 去除重复之后的List
     */
    public static List removeDuplicate(List oldData, List insertData)
    {
        HashSet oldDataSet = new HashSet(oldData);
        List returnData = new ArrayList();
        for (Object o : insertData)
        {
            if (!oldDataSet.contains(o))
            {
                returnData.add(o);
            }
        }
        return returnData;
    }
}
