package moe.feng.materialcompat.sample;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import moe.feng.materialcompat.preference.ListPreferenceCompat;
import moe.feng.materialcompat.preference.MultiSelectListPreferenceCompat;
import moe.feng.materialcompat.preference.SwitchPreferenceCompat;

public class HomeFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

	SwitchPreferenceCompat mSwitchPref;
	ListPreferenceCompat mListPref;
	MultiSelectListPreferenceCompat mMultiSelectListPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_home);

		mSwitchPref = (SwitchPreferenceCompat) findPreference("loli_is_justice");
		mListPref = (ListPreferenceCompat) findPreference("loli_choice");
		mMultiSelectListPref = (MultiSelectListPreferenceCompat) findPreference("type_choice");

		mSwitchPref.setOnPreferenceChangeListener(this);
		mListPref.setOnPreferenceChangeListener(this);
		mMultiSelectListPref.setOnPreferenceChangeListener(this);

		mListPref.setSummary(mListPref.getEntry());
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object o) {
		if (preference == mSwitchPref) {
			Boolean b = (Boolean) o;
			mSwitchPref.setSummary(b ? R.string.pref_loli_is_justice_summary_on
					: R.string.pref_loli_is_justice_summary_off);
			return true;
		}
		if (preference == mListPref) {
			Toast.makeText(
					getActivity().getApplicationContext(),
					"You selected No." + mListPref.findIndexOfValue(mListPref.getValue()) + " LORI" +
					", value: " + mListPref.getValue() + ", entry of value: " + mListPref.getEntry(),
					Toast.LENGTH_SHORT
			).show();
			mListPref.setSummary(mListPref.getEntry());
			return true;
		}
		if (preference == mMultiSelectListPref) {
			String showValues = listArray(mMultiSelectListPref.getValues().toArray());
			String showEntries = "";
			for (int i = 0; i < mMultiSelectListPref.getEntryValues().length; i++) {
				if (mMultiSelectListPref.getValues().contains(mMultiSelectListPref.getEntryValues()[i])) {
					showEntries += mMultiSelectListPref.getEntries() [i] + ",";
				}
			}
			if (showEntries.lastIndexOf(",") != -1) {
				showEntries = showEntries.substring(0, showEntries.lastIndexOf(","));
			}
			Toast.makeText(
					getActivity().getApplicationContext(),
					"You selected values: " + showValues +
					", selected entries: " + showEntries,
					Toast.LENGTH_SHORT
			).show();
			mMultiSelectListPref.setSummary(showEntries);
			return true;
		}
		return false;
	}

	private String listArray(Object[] array) {
		StringBuffer sb = new StringBuffer();
		for (Object obj : array) {
			sb.append(obj);
			sb.append(",");
		}
		if (sb.lastIndexOf(",") != -1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

}
