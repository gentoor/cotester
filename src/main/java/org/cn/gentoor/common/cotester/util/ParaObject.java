package org.cn.gentoor.common.cotester.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gentoor on 2015/11/24.
 */
public class ParaObject {
    private Class paraClass;
    // 用于 map
    private Class keyClass;
    // 用于map和collection
    private Class valueClass;
    private String paraName;
    private List<ParaObject> subParaObjs = new ArrayList<ParaObject>();
    // col range in excel sheet
    private int beginCol;
    private int endCol;

    public Class getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(Class keyClass) {
        this.keyClass = keyClass;
    }

    public Class getValueClass() {
        return valueClass;
    }

    public void setValueClass(Class valueClass) {
        this.valueClass = valueClass;
    }

    public Class getParaClass() {
        return paraClass;
    }

    public void setParaClass(Class paraClass) {
        this.paraClass = paraClass;
    }

    public String getParaName() {
        return paraName;
    }

    public void setParaName(String paraName) {
        this.paraName = paraName;
    }

    public List<ParaObject> getSubParaObjs() {
        return subParaObjs;
    }

    public void setSubParaObjs(List<ParaObject> subParaObjs) {
        this.subParaObjs = subParaObjs;
    }

    public int getBeginCol() {
        return beginCol;
    }

    public void setBeginCol(int beginCol) {
        this.beginCol = beginCol;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    @Override
    public String toString() {
        return "ParaObject{" +
                "paraClass=" + paraClass +
                ", keyClass=" + keyClass +
                ", valueClass=" + valueClass +
                ", paraName='" + paraName + '\'' +
                ", subParaObjs=" + subParaObjs +
                ", beginCol=" + beginCol +
                ", endCol=" + endCol +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParaObject)) return false;

        ParaObject that = (ParaObject) o;

        return !(paraName != null ? !paraName.equals(that.paraName) : that.paraName != null);

    }

    @Override
    public int hashCode() {
        return paraName != null ? paraName.hashCode() : 0;
    }
}
