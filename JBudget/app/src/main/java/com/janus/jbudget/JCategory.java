package com.janus.jbudget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janus on 2014-12-17.
 * For JBudget Android App
 */
public class JCategory {

    public String heading;
    public float amount;
    public List<JCategory> subCategories;

    public JCategory(String heading, float amount)
    {
        this.heading = heading;
        this.amount = amount;
        subCategories = new ArrayList<>();
    }

    public boolean hasSubCategories(){
        return !subCategories.isEmpty();
    }

}
