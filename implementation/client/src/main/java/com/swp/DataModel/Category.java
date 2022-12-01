package com.swp.DataModel;

import java.util.HashSet;
import java.util.Set;

public class Category 
{
    private String sName;
    private final String sUUID;
    private Set<Category> stParents;
    private Set<String> stParentUUIDs;
    private Set<Category> stChildren;

    public Category(String name, String uuid, Set<Category> parents)
    {
        this.sName = name;
        this.sUUID = uuid;
        this.stParents = parents;
        this.stChildren = new HashSet<>();
    }

    void addChild(Category child)
    {
        this.stChildren.add(child);
    }


    //
    // Getter
    //
    public String getName()           { return this.sName; }
    public String getUUID()           { return this.sUUID; }
    public Set<Category> getParents() { return this.stParents; }
}
