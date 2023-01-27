package org.configureme.configwithmap;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

import java.util.Arrays;

@ConfigureMe(allfields = true)
public class ParentConfiguration {
    private ChildConfiguration[] children;

    public ChildConfiguration[] getChildren() {
        return children;
    }

    public void setChildren(ChildConfiguration[] children) {
        this.children = children;
    }

    public String toString(){
        return Arrays.toString(children);
    }
}
