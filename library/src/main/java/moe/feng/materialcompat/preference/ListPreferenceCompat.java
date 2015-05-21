package moe.feng.materialcompat.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SimpleAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import moe.feng.materialcompat.R;

/**
 * @author Fung Jichun 某烧饼 (fython@163.com)
 */
public class ListPreferenceCompat extends ListPreference {

	private Context mContext;
	private AlertDialog mDialog;

	public ListPreferenceCompat(Context context) {
		super(context);
		mContext = context;
	}

	public ListPreferenceCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	public void setEntries(CharSequence[] entries) {
		super.setEntries(entries);
		if (mDialog != null) {
			ArrayList<HashMap<String, CharSequence>> listItems = new ArrayList<>();
			for (CharSequence entry : entries) {
				HashMap<String, CharSequence> map = new HashMap<>();
				map.put("item", entry);
				listItems.add(map);
			}
			mDialog.getListView().setAdapter(new SimpleAdapter(
					mContext,
					listItems,
					R.layout.select_dialog_singlechoice_material,
					new String[] {"item"},
					new int[]{android.R.id.text1}
			));
		}
	}

	@Override
	public Dialog getDialog() {
		return mDialog;
	}

	@Override
	protected void showDialog(Bundle state) {
		if (getEntries() == null || getEntryValues() == null) {
			throw new IllegalStateException("ListPreferenceCompat requires an entries array and an entryValues array.");
		}

		int preselect = findIndexOfValue(getValue());
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
				.setTitle(getDialogTitle())
				.setMessage(getDialogMessage())
				.setIcon(getDialogIcon())
				.setNegativeButton(getNegativeButtonText(), null)
				.setSingleChoiceItems(getEntries(), preselect, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (i >= 0 && getEntryValues() != null) {
							String value = getEntryValues()[i].toString();
							if (callChangeListener(value) && isPersistent()) {
								setValue(value);
							}
						}
						dialogInterface.dismiss();
					}
				});

		final View contentView = onCreateDialogView();
		if (contentView != null) {
			onBindDialogView(contentView);
			builder.setView(contentView);
		} else {
			builder.setMessage(getDialogMessage());
		}

		PreferenceManager pm = getPreferenceManager();
		try {
			Method method = pm.getClass().getDeclaredMethod(
					"registerOnActivityDestroyListener",
					PreferenceManager.OnActivityDestroyListener.class);
			method.setAccessible(true);
			method.invoke(pm, this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mDialog = builder.create();
		if (state != null) {
			mDialog.onRestoreInstanceState(state);
		}
		mDialog.show();
	}

	@Override
	public void onActivityDestroy() {
		super.onActivityDestroy();
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	public void setValue(String value) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			super.setValue(value);
		} else {
			String oldValue = getValue();
			super.setValue(value);
			if (!TextUtils.equals(value, oldValue)) {
				notifyChanged();
			}
		}
	}
	
}
