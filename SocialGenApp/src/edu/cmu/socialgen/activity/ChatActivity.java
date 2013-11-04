package edu.cmu.socialgen.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import edu.cmu.socialgen.DataComSender;
import edu.cmu.socialgen.R;
import edu.cmu.socialgen.UserMsg;
import edu.cmu.socialgen.adapter.ChatAdapter;
import edu.cmu.socialgen.model.Chat;
import edu.cmu.socialgen.model.Message;

public class ChatActivity extends ListActivity {

	List<Message> messages;
	ChatAdapter adapter;
	EditText etMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		this.overridePendingTransition(R.anim.animation_enter, R.anim.animation_exit);
		Bundle bundle = getIntent().getExtras();
		Chat chat = (Chat) bundle.getParcelable("chat");
		this.setTitle(chat.getChatTitle() + "(" + chat.getNumUsers() + ")");
		etMessage = (EditText) findViewById(R.id.etMessage);
		messages = this.getMessages();
		adapter = new ChatAdapter(this, messages);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.animation_exit, R.anim.left_to_right);
	}
	
	public void sendMessage(View v) {
		String newMessage = etMessage.getText().toString().trim();
		if (newMessage.length() > 0) {
			etMessage.setText("");
			/* build message */
			UserMsg msg = new UserMsg(null, "ElecPig", newMessage);
			android.os.Message osMsg = android.os.Message.obtain();
			osMsg.obj = msg;
			/* send message */
			DataComSender.userMsgHandler.sendMessage(osMsg);
			
			/* show in the text box */
			addNewMessage(new Message(newMessage, true));
		}
	}
	
	private void addNewMessage(Message msg) {
		messages.add(msg);
		adapter.notifyDataSetChanged();
		getListView().setSelection(messages.size() - 1);
	}
	
	private List<Message> getMessages() {
		List<Message> messages = new ArrayList<Message>();
		messages.add(new Message("Hello", false));
		messages.add(new Message("Hi!", true));
		messages.add(new Message("Wassup??", false));
		messages.add(new Message("nothing much, working on speech bubbles.", true));
		messages.add(new Message("you say!", true));
		messages.add(new Message("oh thats great. how are you showing them", false));
		messages.add(new Message("Hello", false));
		messages.add(new Message("Hi!", true));
		messages.add(new Message("Wassup??", false));
		messages.add(new Message("nothing much, working on speech bubbles.", true));
		messages.add(new Message("you say!", true));
		messages.add(new Message("oh thats great. how are you showing them", false));
		return messages;
	}

}
