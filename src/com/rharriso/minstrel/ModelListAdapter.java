package com.rharriso.minstrel;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rharriso.minstrel.models.ModelListItem;

/**
 * put those artists in a list
 */
public class ModelListAdapter extends ArrayAdapter<ModelListItem>{
	private Context mContext;
	private List<ModelListItem> mModelList;
	
	public ModelListAdapter(Context context, List<ModelListItem> models) {
		super(context, R.layout.list_row, models);
		
		mContext 			= context;
		mModelList			= models;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		
		if (row == null){
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(R.layout.list_row, parent, false);
		}
		
		ModelListItem model = mModelList.get(position);
		
		if(model != null){
			TextView titleTxt = (TextView)row.findViewById(R.id.row_title);
			if(titleTxt != null) titleTxt.setText(model.getListTitle());
		}
		
		return row;
	}
}
