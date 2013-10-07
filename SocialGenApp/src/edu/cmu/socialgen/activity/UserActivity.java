package edu.cmu.socialgen.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import edu.cmu.socialgen.R;
import edu.cmu.socialgen.model.User;

public class UserActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		Bundle bundle = getIntent().getExtras();
		User user = (User) bundle.getParcelable("user");
		TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
		tvUsername.setText(user.getUsername());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}

}
