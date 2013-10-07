package edu.cmu.socialgen.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cmu.socialgen.R;
import edu.cmu.socialgen.model.User;

public class UsersAdapter extends BaseAdapter {

	private int layoutRes;
	private ArrayList<User> users;
	private LayoutInflater inflater;
	
	public UsersAdapter(Context mContext, int layoutRes, ArrayList<User> users) {
		this.layoutRes = layoutRes;
		this.users = users;
		inflater = LayoutInflater.from(mContext);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		UsersViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(layoutRes, parent, false);
			holder = new UsersViewHolder();
			holder.setImgView((ImageView) convertView.findViewById(R.id.user_image));
			holder.setNameView((TextView) convertView.findViewById(R.id.user_name));
			convertView.setTag(holder);
		} else {
			holder = (UsersViewHolder) convertView.getTag();
		}
		User user = users.get(position);
		holder.getImgView().setImageResource(user.getImgRes());
		holder.getNameView().setText(user.getUsername());
		return convertView;
	}

	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class UsersViewHolder {
		private ImageView imgView;
		private TextView nameView;
		public ImageView getImgView() {
			return imgView;
		}
		public void setImgView(ImageView imgView) {
			this.imgView = imgView;
		}
		public TextView getNameView() {
			return nameView;
		}
		public void setNameView(TextView nameView) {
			this.nameView = nameView;
		}
	}
	
}
