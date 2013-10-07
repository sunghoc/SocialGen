package edu.cmu.socialgen.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import edu.cmu.socialgen.R;
import edu.cmu.socialgen.model.Message;

public class ChatAdapter extends BaseAdapter {

	private Context mContext;
	private List<Message> mMessages;
	
	public ChatAdapter(Context context, List<Message> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_row, parent, false);
			holder = new ChatViewHolder();
			holder.message = (TextView) convertView.findViewById(R.id.tvMessage);
			convertView.setTag(holder);
		} else {
			holder = (ChatViewHolder) convertView.getTag();
		}
		
		Message message = mMessages.get(position);
		holder.message.setText(message.getMessage());
		
		LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
		if (message.isStatusMessage()) {
			holder.message.setBackgroundColor(Color.TRANSPARENT);
			lp.gravity = Gravity.LEFT;
			holder.message.setTextColor(mContext.getResources().getColor(R.color.textFieldColor));
		} else {
			if (message.isMine()) {
				holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
				lp.gravity = Gravity.RIGHT;
			} else {
				holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
				lp.gravity = Gravity.LEFT;
			}
			holder.message.setLayoutParams(lp);
			holder.message.setTextColor(mContext.getResources().getColor(R.color.textColor));
		}
		return convertView;
	}
	
	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class ChatViewHolder {
		private TextView message;
	}
}
