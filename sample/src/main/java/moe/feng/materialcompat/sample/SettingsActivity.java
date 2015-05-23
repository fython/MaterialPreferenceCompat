package moe.feng.materialcompat.sample;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

	private int flag;

	public static final int FLAG_HOME = 0;
	public static final String EXTRA_FLAG = "flag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		Intent intent = getIntent();
		flag = intent.getIntExtra(EXTRA_FLAG, FLAG_HOME);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			toolbar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
		}
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Fragment fragment;
		switch (flag) {
			case FLAG_HOME:
			default:
				fragment = new HomeFragment();
				break;
		}
		getFragmentManager().beginTransaction()
				.replace(R.id.content, fragment)
				.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			super.onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
