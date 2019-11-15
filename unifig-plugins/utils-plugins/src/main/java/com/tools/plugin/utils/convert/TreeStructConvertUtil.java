package com.tools.plugin.utils.convert;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang3.ArrayUtils;
import com.tools.plugin.utils.AssertUtil;
import com.tools.plugin.utils.convert.bean.TreeStructNodeBean;

/**
 * @Title:TreeStructToJSONUtil
 * @Description: 将树形结构的对象转换为JSON格式，树形结构对象为：ID、PID、TXT ;转换后格式为：["id":"",txt:"",chidren:[{......}]]
 *
 */
public class TreeStructConvertUtil {

    private static final String DEFAULT_ID_FIELD = "ID";

    private static final String DEFAULT_TXT_FIELD = "TXT";

    private static final String DEFAULT_PID_FIELD = "PID";


    /**
     * @Title: buildJson
     * @Description:根据树状结构数据集合,输出JSON，可指定跟节点值
     * @param dataList
     * @return
     */
    public static String buildJson(List<?> dataList) {
        return buildJson(dataList, DEFAULT_ID_FIELD, DEFAULT_TXT_FIELD, DEFAULT_PID_FIELD);
    }

    /**
     * @Title: buildJson
     * @Description:根据树状结构数据集合,输出JSON，可指定树状结构对象中的ID字段、txt字段、pid字段名称
     * @param dataList
     * @return
     */
    public static String buildJson(List<?> dataList, String idField, String txtField, String pidField) {
        return buildJson(dataList, idField, txtField, pidField, null);
    }

    /**
     * @Title: buildJson
     * @Description:根据树状结构数据集合,输出JSON，可指定树状结构对象中的ID字段、txt字段、pid字段名称,输出JSON中包含的其它字段
     * @param dataList
     * @return
     */
    public static String buildJson(List<?> dataList, String idField, String txtField, String pidField,
                                   String[] includeField) {
        return buildJson(dataList, idField, txtField, pidField, includeField, null, false, false);
    }

    /**
     * @Title: buildJson
     * @Description:根据树状结构数据集合,输出DOM UL结构
     * @return
     */
    public static String buildDomUL(List<?> dataList, String idField, String txtField, String pidField) {
        TreeStructNodeBean rootNode = buildTreeNode(dataList, idField, txtField, pidField, new String[] {"SORTINDEX"},
            "SORTINDEX", false,
            false);
        StringBuilder buffer = new StringBuilder();
        rootNode.toDomULString(buffer);
        return buffer.toString();
    }

    public static String buildDomUL(List<?> dataList) {
        String str = buildDomUL(dataList, DEFAULT_ID_FIELD, DEFAULT_TXT_FIELD, DEFAULT_PID_FIELD);
        return str;
    }

    /**
     * @Title: buildJson
     * @Description: 根据树状结构数据集合,输出JSON
     * @param dataList 数据集合
     * @param idField 树形结构ID字段名
     * @param txtField 树形结构txt文本字段名
     * @param pidField 树形结构父ID字段名
     * @param includeField 输出JSON中包含的其它字段
     * @param showLeaf 输出JSON中是否包含isleaf属性
     * @param showLevel 输出JSON中是否包含层级level属性
     * @return
     */
    public static <T> String buildJson(List<T> dataList, String idField, String txtField,
                                       String pidField, String[] includeField, String orderField, boolean showLeaf,
                                       boolean showLevel) {
        TreeStructNodeBean rootNode = buildTreeNode(dataList, idField, txtField, pidField, includeField,
            idField, false, false);
        StringBuilder buffer = new StringBuilder();
        rootNode.toJsonString(buffer, showLeaf, showLevel);
        return buffer.toString();
    }

    /**
     * @Title: buildJson
     * @Description: 根据树状结构数据集合,创建树状结构节点对象
     * @param dataList 数据集合
     * @param idField 树形结构ID字段名
     * @param txtField 树形结构txt文本字段名
     * @param pidField 树形结构父ID字段名
     * @param includeField 输出JSON中包含的其它字段
     * @param showLeaf 输出JSON中是否包含isleaf属性
     * @param showLevel 输出JSON中是否包含层级level属性
     * @return
     */
    public static <T> TreeStructNodeBean buildTreeNode(List<T> dataList, String idField,
                                                       String txtField, String pidField, String[] includeField,
                                                       String orderField, boolean showLeaf, boolean showLevel) {
        // 验证参数
        if ( !validateList(dataList, idField, txtField, pidField)) {
            throw new IllegalArgumentException("The object in the collection does not contain the necessary field：["
                                               + idField + "," + txtField + "," + pidField + "]");
        }
        TreeStructNodeBean rootNode = null;
        Map<String, TreeStructNodeBean> treeNodeMap = new TreeMap<String, TreeStructNodeBean>();
        Map<String, Field> fieldMap = null;
        Set<String> pidSet = new HashSet<String>();
        for (Iterator<T> it = dataList.iterator(); it.hasNext();) {
            T t = it.next();
            TreeStructNodeBean treeNode = new TreeStructNodeBean();
            // 如果传入树状结构对象为Map对象
            if (t instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>)t;
                treeNode.setId(String.valueOf(map.get(idField)));
                treeNode.setPid(String.valueOf(map.get(pidField)));
                treeNode.setTxt(String.valueOf(map.get(txtField)));
                if (null != includeField) {
                    for (String prop : includeField) {
                        if (prop.equalsIgnoreCase(idField) || prop.equalsIgnoreCase(txtField)
                            || prop.equalsIgnoreCase(pidField)) {
                            continue;
                        }
                        treeNode.addProperties(prop, map.get(prop));
                    }
                }
            } else {
                // 如果传入树状对象为自定义javaBean
                if (fieldMap == null) {
                    Field[] fields = t.getClass().getDeclaredFields();
                    fieldMap = new HashMap<String, Field>();
                    for (Field field : fields) {
                        fieldMap.put(field.getName().toUpperCase(), field);
                    }
                }
                if (null != fieldMap) {
                    try {
                        treeNode.setId(String.valueOf(fieldMap.get(idField).get(t)));
                        treeNode.setPid(String.valueOf(fieldMap.get(pidField).get(t)));
                        treeNode.setTxt(String.valueOf(fieldMap.get(txtField).get(t)));
                        if (ArrayUtils.isNotEmpty(includeField)) {
                            for (String prop : includeField) {
                                if (prop.equalsIgnoreCase(idField) || prop.equalsIgnoreCase(txtField)
                                    || prop.equalsIgnoreCase(pidField)) {
                                    continue;
                                }
                                treeNode.addProperties(prop, fieldMap.get(prop).get(t));
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("build Tree Struct JSON Fail ", e);
                    }
                }
            }
            if (treeNode.getPid() != null) {
                pidSet.add(treeNode.getPid());
            }
            treeNodeMap.put(treeNode.getId(), treeNode);
        }
        Set<String> keys = treeNodeMap.keySet();
        Set<String> existKeys = new HashSet<String>();
        for (String key : keys) {
            TreeStructNodeBean nodeBean = treeNodeMap.get(key);
            if (pidSet.contains(key) && !nodeBean.getId().equalsIgnoreCase(nodeBean.getPid())) {
                existKeys.add(key);
            }
        }
        pidSet.removeAll(existKeys);
        if (pidSet.size() > 1) {
            rootNode = new TreeStructNodeBean("-1", null, "root");
        } else if (pidSet.size() == 1) {
            String pid = pidSet.toArray(new String[pidSet.size()])[0];
            TreeStructNodeBean treeStructNodeBean = treeNodeMap.get(pid);
            if (treeStructNodeBean != null) {
                treeNodeMap.remove(pid);
                rootNode = new TreeStructNodeBean(pid, treeStructNodeBean.getTxt(), "root");
            } else {
                rootNode = new TreeStructNodeBean(pid, null, "root");
            }
        } else {
            throw new RuntimeException("在数据中未查找到PID");
        }
        dataList = null;
        Collection<TreeStructNodeBean> vals = treeNodeMap.values();
        for (TreeStructNodeBean treeNode : vals) {
            if (treeNode.getPid().equals(rootNode.getId())) {
                rootNode.addChild(treeNode);
            } else {
            	if(treeNodeMap.get(treeNode.getPid())!=null){
            		 treeNodeMap.get(treeNode.getPid()).addChild(treeNode);
            	}
            }
        }
        if (orderField != null) {
            if (orderField.equalsIgnoreCase(idField)) {
                orderField = "id";
            }
            if (orderField.equalsIgnoreCase(txtField)) {
                orderField = "txt";
            }
            if (orderField.equalsIgnoreCase(pidField)) {
                orderField = "pid";
            }
            rootNode.sortChildNodes(orderField);
        }
        return rootNode;
    }

    /**
     * @Title: validateList
     * @Description: 验证数据集合中的对象是否包含idField、txtField、pidField字段
     * @param dataList
     * @param idField
     * @param txtField
     * @param pidField
     * @return
     */
    private static <T> boolean validateList(List<T> dataList, String idField, String txtField, String pidField) {
        AssertUtil.notEmpty(dataList, "dataList is null");
        AssertUtil.notNull(idField, "idField is null");
        AssertUtil.notNull(txtField, "txtField is null");
        AssertUtil.notNull(pidField, "pidField is null");
        T t = dataList.get(0);
        if (t instanceof Map) {
            if ( ! ((Map<?, ?>)t).containsKey(idField) || ! ((Map<?, ?>)t).containsKey(txtField)
                || ! ((Map<?, ?>)t).containsKey(pidField)) {
                return false;
            }
        } else {
            Field[] fields = t.getClass().getFields();
            int count = 0;
            for (int i = 0; i < fields.length; i++ ) {
                Field field = fields[i];
                if (field.getName().equalsIgnoreCase(idField) || field.getName().equalsIgnoreCase(txtField)
                    || field.getName().equalsIgnoreCase(pidField)) {
                    count++ ;
                }
            }
            if (count != 3)
                return false;
        }
        return true;
    }
}