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
import edu.cmu.socialgen.model.Chat;

public class ChatsAdapter extends BaseAdapter {

	private int layoutRes;
	private ArrayList<Chat> chats;
	private LayoutInflater inflater;
	
	public ChatsAdapter(Context mContext, int layoutRes, ArrayList<Chat> chats) {
		this.layoutRes = layoutRes;
		this.chats = chats;
		inflater = LayoutInflater.from(mContext);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatsViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(layoutRes, parent, false);
			holder = new ChatsViewHolder();
			holder.setImgView((ImageView) convertView.findViewById(R.id.chat_image));
			holder.setTitleView((TextView) convertView.findViewById(R.id.chat_title));
			holder.setDateView((TextView) convertView.findViewById(R.id.chat_date));
			convertView.setTag(holder);
		} else {
			holder = (ChatsViewHolder) convertView.getTag();
		}
		Chat chat = chats.get(position);
		holder.getImgView().setImageResource(chat.getImgRes());
		holder.getTitleView().setText(chat.getChatTitle());
		holder.getDateView().setText(chat.getFormattedLastActiveDate());
		return convertView;
	}

	@Override
	public int getCount() {
		return chats.size();
	}

	@Override
	public Object getItem(int position) {
		return chats.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class ChatsViewHolder {
		private ImageView imgView;
		private TextView titleView;
		private TextView dateView;
		public ImageView getImgView() {
			return imgView;
		}
		public void setImgView(ImageView imgView) {
			this.imgView = imgView;
		}
		public TextView getTitleView() {
			return titleView;
		}
		public void setTitleView(TextView titleView) {
			this.titleView = titleView;
		}
		public TextView getDateView() {
			return dateView;
		}
		public void setDateView(TextView dateView) {
			this.dateView = dateView;
		}
	}
	
}
