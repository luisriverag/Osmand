package net.osmand.plus.plugins.audionotes;

import static net.osmand.plus.quickaction.QuickActionIds.TAKE_AUDIO_NOTE_ACTION_ID;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.data.LatLon;
import net.osmand.plus.plugins.PluginsHelper;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.quickaction.QuickAction;
import net.osmand.plus.quickaction.QuickActionType;
import net.osmand.plus.quickaction.actions.SelectMapLocationAction;

public class TakeAudioNoteAction extends SelectMapLocationAction {

	public static final QuickActionType TYPE = new QuickActionType(TAKE_AUDIO_NOTE_ACTION_ID,
			"audio.note", TakeAudioNoteAction.class).
			nameRes(R.string.quick_action_audio_note).iconRes(R.drawable.ic_action_micro_dark).nonEditable().
			category(QuickActionType.MY_PLACES).nameActionRes(R.string.shared_string_add);

	public TakeAudioNoteAction() {
		super(TYPE);
	}

	public TakeAudioNoteAction(QuickAction quickAction) {
		super(quickAction);
	}

	@Override
	protected void onLocationSelected(@NonNull MapActivity mapActivity, @NonNull LatLon latLon) {
		AudioVideoNotesPlugin plugin = PluginsHelper.getPlugin(AudioVideoNotesPlugin.class);
		if (plugin != null) {
			plugin.recordAudio(latLon.getLatitude(), latLon.getLongitude(), mapActivity);
		}
	}

	@Override
	@Nullable
	protected Object getLocationIcon(@NonNull MapActivity mapActivity) {
		AudioVideoNotesPlugin plugin = PluginsHelper.getPlugin(AudioVideoNotesPlugin.class);
		AudioNotesLayer layer = plugin != null ? plugin.getAudioNotesLayer() : null;
		return layer != null ? layer.getAudioNoteIcon() : null;
	}

	@Override
	public void drawUI(@NonNull ViewGroup parent, @NonNull MapActivity mapActivity) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.quick_action_with_text, parent, false);
		((TextView) view.findViewById(R.id.text)).setText(
				R.string.quick_action_take_audio_note_descr);
		parent.addView(view);
	}
}
