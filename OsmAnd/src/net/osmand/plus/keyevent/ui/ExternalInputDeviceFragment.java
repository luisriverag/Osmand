package net.osmand.plus.keyevent.ui;

import static net.osmand.plus.utils.UiUtilities.CompoundButtonType.TOOLBAR;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.keyevent.InputDeviceHelper;
import net.osmand.plus.keyevent.devices.InputDeviceProfile;
import net.osmand.plus.settings.backend.ApplicationMode;
import net.osmand.plus.settings.fragments.BaseSettingsFragment;
import net.osmand.plus.settings.preferences.ListPreferenceEx;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.utils.ColorUtilities;
import net.osmand.plus.utils.UiUtilities;

import java.util.List;

public class ExternalInputDeviceFragment extends BaseSettingsFragment {

	private static final String TYPE_PREF_ID = "input_device_type_id";
	private static final String BINDING_PREF_ID = "input_device_type_bindings_id";

	private InputDeviceHelper deviceHelper;

	@Override
	protected void setupPreferences() {
		Context context = getContext();
		PreferenceScreen screen = getPreferenceScreen();
		if (context != null && screen != null) {
			OsmandApplication app = requireMyApplication();
			deviceHelper = app.getInputDeviceHelper();
			screen.addPreference(createPreference(context, R.layout.list_item_divider));
			if (isInputDeviceEnabled()) {
				screen.addPreference(createTypePreference(context));
				screen.addPreference(createBindingPreference(context));
			} else {
				screen.addPreference(createPreference(context,
						R.layout.card_external_input_device_empty_banner));
			}
			screen.addPreference(createPreference(context, R.layout.card_bottom_divider));
		}
	}

	@Override
	protected void createToolbar(@NonNull LayoutInflater inflater, @NonNull View view) {
		super.createToolbar(inflater, view);

		view.findViewById(R.id.toolbar_switch_container).setOnClickListener(v -> {
			boolean newState = !isInputDeviceEnabled();
			ApplicationMode appMode = getSelectedAppMode();
			settings.EXTERNAL_INPUT_DEVICE_ENABLED.setModeValue(appMode, newState);
			updateToolbarSwitch(view);
			updateAllSettings();
		});
	}

	protected void updateToolbar() {
		View view = getView();
		if (view != null) {
			AndroidUiHelper.setVisibility(View.GONE,
					view.findViewById(R.id.profile_icon),
					view.findViewById(R.id.toolbar_subtitle));
			updateToolbarSwitch(view);
		}
	}

	private void updateToolbarSwitch(View view) {
		boolean checked = isInputDeviceEnabled();
		View switchContainer = view.findViewById(R.id.toolbar_switch_container);

		int disabledColor = ColorUtilities.getColor(app, R.color.preference_top_switch_off);
		int color = checked ? getActiveProfileColor() : disabledColor;
		AndroidUtils.setBackground(switchContainer, new ColorDrawable(color));

		SwitchCompat compoundButton = switchContainer.findViewById(R.id.switchWidget);
		compoundButton.setChecked(checked);
		UiUtilities.setupCompoundButton(compoundButton, isNightMode(), TOOLBAR);

		TextView title = switchContainer.findViewById(R.id.switchButtonText);
		title.setText(checked ? R.string.shared_string_enabled : R.string.shared_string_disabled);
	}

	private Preference createTypePreference(@NonNull Context context) {
		List<InputDeviceProfile> devices = app.getInputDeviceHelper().getAllDevices();
		String[] entries = new String[devices.size()];
		String[] values = new String[devices.size()];
		for (int i = 0; i < devices.size(); i++) {
			InputDeviceProfile device = devices.get(i);
			entries[i] = device.toHumanString(app);
			values[i] = device.getId();
		}

		ListPreferenceEx uiPreference = createListPreferenceEx(
				settings.EXTERNAL_INPUT_DEVICE.getId(), entries, values,
				R.string.shared_string_type, R.layout.preference_with_descr_and_divider);

		InputDeviceProfile inputDevice = deviceHelper.getSelectedDevice(getSelectedAppMode());
		if (inputDevice != null) {
//			uiPreference.setKey(TYPE_PREF_ID);
			uiPreference.setLayoutResource(R.layout.preference_with_descr_and_divider);
			uiPreference.setTitle(R.string.shared_string_type);
			uiPreference.setDescription(R.string.external_input_device_descr);
			uiPreference.setSummary(inputDevice.toHumanString(context));
			uiPreference.setIcon(getContentIcon(R.drawable.ic_action_keyboard));
			uiPreference.setSelectable(true);
		}
		return uiPreference;
	}

	private Preference createBindingPreference(@NonNull Context context) {
		Preference uiPreference = new Preference(context);
		InputDeviceProfile inputDevice = deviceHelper.getSelectedDevice(getSelectedAppMode());
		if (inputDevice != null) {
			uiPreference.setKey(BINDING_PREF_ID);
			uiPreference.setLayoutResource(R.layout.preference_with_descr);
			uiPreference.setTitle(R.string.key_bindings);
			uiPreference.setSummary(String.valueOf(inputDevice.getCommandsCount()));
			uiPreference.setIcon(getContentIcon(R.drawable.ic_quick_action));
			uiPreference.setSelectable(true);
		}
		return uiPreference;
	}

	private Preference createPreference(@NonNull Context context, @LayoutRes int layoutResId) {
		Preference preference = new Preference(context);
		preference.setLayoutResource(layoutResId);
		preference.setSelectable(false);
		return preference;
	}

	private boolean isInputDeviceEnabled() {
		return deviceHelper.getEnabledDevice(getSelectedAppMode()) != null;
	}

	@ColorRes
	protected int getBackgroundColorRes() {
		return ColorUtilities.getActivityBgColorId(isNightMode());
	}

}
