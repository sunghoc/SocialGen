package edu.cmu.socialgen.tab;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import edu.cmu.socialgen.R;
import edu.cmu.socialgen.activity.ChatActivity;
import edu.cmu.socialgen.adapter.ChatsAdapter;
import edu.cmu.socialgen.model.Chat;

public class ChatsFragment extends ListFragment {
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	ArrayList<Chat> chats = new ArrayList<Chat>();
    	chats.add(new Chat("Sample Chat Title 1", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 2", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 3", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 4", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 5", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 6", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 7", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 8", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 9", R.drawable.android));
    	chats.add(new Chat("Sample Chat Title 10", R.drawable.android));
    	setListAdapter(new ChatsAdapter(getActivity(), R.layout.chats_row, chats));
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Chat chat = (Chat) getListView().getItemAtPosition(position);
    	Intent intent = new Intent(getActivity(), ChatActivity.class);
    	intent.putExtra("chat", chat);
    	startActivity(intent);
    }
    
}
