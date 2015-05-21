package moe.feng.materialcompat.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SimpleAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import moe.feng.materialcompat.R;

/**
 * @author Fung Jichun 某烧饼 (fython@163.com)
 */
public class MultiSelectListPreferenceCompat extends MultiSelectListPreference {

	private Context mContext;
	private AlertDialog mDialog;

	public MultiSelectListPreferenceCompat(Context context) {
		this(context, null);
	}

	public MultiSelectListPreferenceCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
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
		boolean[] b = new boolean[getValues().size()];
		for (String s : getValues()) {
			int index = findIndexOfValue(s);
			if (index >= 0) {
				b [findIndexOfValue(s)] = true;
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
				.setTitle(getDialogTitle())
				.setMessage(getDialogMessage())
				.setIcon(getDialogIcon())
				.setNegativeButton(getNegativeButtonText(), null)
				.setPositiveButton(getPositiveButtonText(), null)
				.setMultiChoiceItems(getEntries(), b, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i, boolean b) {
						final Set<String> values = getValues();
						if (b) {
							values.add(getEntryValues()[i].toString());
						} else {
							values.remove(getEntryValues()[i].toString());
						}
						if (callChangeListener(values)) {
							setValues(values);
						}
					}
				})
				.setOnDismissListener(this);

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
		if (state != null)
			mDialog.onRestoreInstanceState(state);
		mDialog.show();
	}

	@Override
	public void onActivityDestroy() {
		super.onActivityDestroy();
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

}
