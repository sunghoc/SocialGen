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
import edu.cmu.socialgen.activity.HistoryActivity;
import edu.cmu.socialgen.adapter.ChatsAdapter;
import edu.cmu.socialgen.model.Chat;


public class HistoryFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return super.onCreateView(inflater, container, savedInstanceState);
    }
 
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	ArrayList<Chat> history = new ArrayList<Chat>();
    	history.add(new Chat("Sample History Title 1", R.drawable.android));
    	history.add(new Chat("Sample History Title 2", R.drawable.android));
    	history.add(new Chat("Sample History Title 3", R.drawable.android));
    	history.add(new Chat("Sample History Title 4", R.drawable.android));
    	history.add(new Chat("Sample History Title 5", R.drawable.android));
    	history.add(new Chat("Sample History Title 6", R.drawable.android));
    	history.add(new Chat("Sample History Title 7", R.drawable.android));
    	history.add(new Chat("Sample History Title 8", R.drawable.android));
    	history.add(new Chat("Sample History Title 9", R.drawable.android));
    	history.add(new Chat("Sample History Title 10", R.drawable.android));
    	setListAdapter(new ChatsAdapter(getActivity(), R.layout.chats_row, history));
    }
	
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Chat oldChat = (Chat) getListView().getItemAtPosition(position);
    	Intent intent = new Intent(getActivity(), HistoryActivity.class);
    	intent.putExtra("oldChat", oldChat);
    	startActivity(intent);
    }
}
