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
import edu.cmu.socialgen.activity.UserActivity;
import edu.cmu.socialgen.adapter.UsersAdapter;
import edu.cmu.socialgen.model.User;

public class UsersFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
 
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	ArrayList<User> users = new ArrayList<User>();
    	users.add(new User("SampleUser#1", R.drawable.android));
    	users.add(new User("SampleUser#2", R.drawable.android));
    	users.add(new User("SampleUser#3", R.drawable.android));
    	users.add(new User("SampleUser#4", R.drawable.android));
    	users.add(new User("SampleUser#5", R.drawable.android));
    	users.add(new User("SampleUser#6", R.drawable.android));
    	users.add(new User("SampleUser#7", R.drawable.android));
    	users.add(new User("SampleUser#8", R.drawable.android));
    	users.add(new User("SampleUser#9", R.drawable.android));
    	users.add(new User("SampleUser#10", R.drawable.android));
    	setListAdapter(new UsersAdapter(getActivity(), R.layout.users_row, users));
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	User user = (User) getListView().getItemAtPosition(position);
    	Intent intent = new Intent(getActivity(), UserActivity.class);
    	intent.putExtra("user", user);
    	startActivity(intent);
    }
}
