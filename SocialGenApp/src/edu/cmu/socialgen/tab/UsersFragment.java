package edu.cmu.socialgen.tab;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import edu.cmu.socialgen.ControlComManager;
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
    	Object[] userIdArray = ControlComManager.neighHashMap.values().toArray();
    	int neighNum = userIdArray.length;
    	for (int i=0; i<neighNum; i++) {
    		users.add(new User("("+(i+1)+") "+(String)(userIdArray[i]), R.drawable.android));
    	}
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
