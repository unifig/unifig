package etl.dispatch.util.convert.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;

public class TreeStructNodeBean {

    private String id;

    private String txt;

    private String pid;

    private Map<String, Object> propMap = null;

    private List<TreeStructNodeBean> children;

    public TreeStructNodeBean() {
    }

    public TreeStructNodeBean(String id, String txt, String pid) {
        this.id = id;
        this.txt = txt;
        this.pid = pid;
    }

    public void addChild(TreeStructNodeBean treeNode) {
        if (this.children == null) {
            this.children = new ArrayList<TreeStructNodeBean>();
        }
        this.children.add(treeNode);
    }

    public List<TreeStructNodeBean> getChildren() {
        return children;
    }

    public String getId() {
        return id;
    }

    public String getPid() {
        return pid;
    }

    public String getTxt() {
        if (txt == null && id.equals("-1")) {
            return "全部";
        } else {
            return txt;
        }
    }

    public void setChildren(List<TreeStructNodeBean> children) {
        this.children = children;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void addProperties(String name, Object value) {
        if (this.propMap == null) {
            this.propMap = new HashMap<String, Object>();
        }
        this.propMap.put(name, value);
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public Map<String, Object> getPropMap() {
        return propMap;
    }

    public void sortChildNodes(final String orderField) {
        if (CollectionUtils.isNotEmpty(this.children)) {
            Collections.sort(this.children, new Comparator<TreeStructNodeBean>() {

                public int compare(TreeStructNodeBean treeNode1, TreeStructNodeBean treeNode2) {
                    if (orderField.equals("id")) {
                        return treeNode1.getId().compareTo(treeNode2.getId());
                    } else if (orderField.equals("txt")) {
                        return treeNode1.getTxt().compareTo(treeNode2.getTxt());
                    } else if (orderField.equals("pid")) {
                        return treeNode1.getPid().compareTo(treeNode2.getPid());
                    } else {
                        Object obj1 = treeNode1.getPropMap().get(orderField);
                        Object obj2 = treeNode2.getPropMap().get(orderField);
                        if (obj1 != null && obj2 != null) {
                            if (obj1 instanceof Integer) {
                                return ((Integer)obj1).compareTo((Integer)obj2);
                            } else {
                                return String.valueOf(treeNode1.getPropMap().get(orderField)).compareTo(
                                    String.valueOf(treeNode2.getPropMap().get(orderField)));
                            }
                        }
                        return 0;
                    }
                }
            });
            for (TreeStructNodeBean treeNode : this.children) {
                treeNode.sortChildNodes(orderField);
            }
        }
    }

    public void toJsonString(StringBuilder buffer, boolean showLeaf, boolean showLevel) {
        toJsonString(buffer, showLeaf, showLevel, 1);
    }

	public void toDomULString(StringBuilder buffer) {
        if("root".equals(this.getPid())){
            if(this.getTxt() == null){
                if(this.children.size() == 1){
                    TreeStructNodeBean treeNode = this.children.get(0);
                    treeNode.toDomULString(buffer);
                }else{
                    buffer.append("<li val=\"").append(this.children.get(0).getId()).append("\">").append(this.children.get(0).getTxt());
                    toDomULStr(buffer);
                }
            }else{
                buffer.append("<li val=\"").append(this.getId()).append("\">").append(this.getTxt());
                toDomULStr(buffer);
            }
        }else{
            buffer.append("<li val=\"").append(this.getId()).append("\">").append(this.getTxt());
            toDomULStr(buffer);
        }
    }

    private void toDomULStr(StringBuilder buffer) {
        if ( !CollectionUtils.isEmpty(this.children)) {
            buffer.append("<ul>");
            for (TreeStructNodeBean treeNode : this.children) {
                treeNode.toDomULString(buffer);
            }
            buffer.append("</ul>");
        }
        buffer.append("</li>");
    }

    private void toJsonString(StringBuilder buffer, boolean showLeaf, boolean showLevel, int level) {
        buffer.append("{").append("\"id\":").append("\"" + this.getId() + "\"").append(",\"txt\":").append("\"" + this.getTxt() + "\"");
        if (showLevel) {
            buffer.append(",\"level\":").append(level);
        }
        // 添加其它需在JSON串中输出的属性
        if (null != propMap) {
            Set<Entry<String, Object>> propSet = propMap.entrySet();
            for (Entry<String, Object> prop : propSet) {
                buffer.append(",\"").append(prop.getKey()).append("\"");
                Object val = prop.getValue();
                if (val instanceof Boolean || val instanceof Number) {
                    buffer.append(val);
                } else {
                    buffer.append("\"").append(String.valueOf(val)).append("\"");
                }
            }
        }
        if (CollectionUtils.isEmpty(this.children)) {
            if (showLeaf) {
                buffer.append(",\"leaf\":").append("true");
            }
        } else {
            buffer.append(",\"children\":[");
            int i = 0;
            for (TreeStructNodeBean treeNode : this.children) {
                treeNode.toJsonString(buffer, showLeaf, showLevel, level++ );
                if (i < this.children.size() - 1) {
                    buffer.append(",");
                }
                i++ ;
            }
            buffer.append("]");
        }
        buffer.append("}");
    }
}