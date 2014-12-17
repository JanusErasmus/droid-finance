package com.janus.jbudget;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class JCategoryListAdapter extends BaseExpandableListAdapter {

    List<JCategory> categories;
  public LayoutInflater inflater;
  public Activity activity;

  public JCategoryListAdapter(Activity act, List<JCategory> categories) {

    activity = act;
    this.categories = categories;
    inflater = act.getLayoutInflater();
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return categories.get(groupPosition).subCategories.get(childPosition);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public View getChildView(int groupPosition, final int childPosition,
      boolean isLastChild, View convertView, ViewGroup parent) {

      JCategory cat = (JCategory)getChild(groupPosition, childPosition);

      String children = cat.heading;
      convertView = inflater.inflate(R.layout.cat_child_item, parent, false);
      TextView text = (TextView) convertView.findViewById(R.id.heading);
      text.setText(children);

      text = (TextView) convertView.findViewById(R.id.amount);

      if(cat.amount < 0)
          text.setTextColor(Color.RED);
      else if (cat.amount == 0)
          text.setTextColor(Color.GRAY);

      NumberFormat formatter = NumberFormat.getCurrencyInstance();
      String amountString = formatter.format(cat.amount);
      text.setText(amountString);


      return convertView;
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return categories.get(groupPosition).subCategories.size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return categories.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
    return categories.size();
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded,
      View convertView, ViewGroup parent) {

      convertView = inflater.inflate(R.layout.cat_parent_item, parent, false);

      JCategory category = (JCategory) getGroup(groupPosition);

      TextView text = (TextView) convertView.findViewById(R.id.heading);
      text.setText(category.heading);

      if(!category.hasSubCategories())
      {

          text = (TextView) convertView.findViewById(R.id.amount);
          //Log.d("Main", category.heading + " " + category.amount);

          if(category.amount < 0)
              text.setTextColor(Color.RED);
          else if (category.amount == 0)
              text.setTextColor(Color.GRAY);

          NumberFormat formatter = NumberFormat.getCurrencyInstance();
          String amountString = formatter.format(category.amount);
          text.setText(amountString);

      }


      return convertView;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return false;
  }
} 