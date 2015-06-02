package cz.skaut.warehousemanager.fragment;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;
import cz.skaut.warehousemanager.R;
import cz.skaut.warehousemanager.helper.C;
import timber.log.Timber;

public class SettingsFragment extends BaseFragment {

    @InjectView(R.id.settingsTimeframe)
    TextView timeFrameText;

    @InjectView(R.id.settingsTimeframeLayout)
    LinearLayout timeFrameLayout;

    private AlertDialog dialog;

    private EditText input;

    private long period;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showUpButton();
        setTitle(R.string.settings);
        setSubtitle("");

        Timber.d("OnViewCreated");

        period = prefs.getLong(C.INVENTORIZE_PERIOD_DAYS, 0);
        timeFrameText.setText(String.valueOf(period));

        input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(period));

        dialog = createDialog();
    }

    @OnClick(R.id.settingsTimeframeLayout)
    void showDialog() {
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            if (input.getText().length() > 0) {
                updatePreference(Long.valueOf(input.getText().toString()));
                timeFrameText.setText(input.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Období inventarizace")
                .setMessage("Počet dní mezi jednotlivými inventarizacemi")
                .setView(input)
                .setPositiveButton(R.string.save, null) // listener is set later to enable not dismissing the dialog
                .setNegativeButton(R.string.cancel, (dialogInterface, position) -> {
                    input.setText(String.valueOf(period));
                    Timber.d("canceled");
                });
        return builder.create();
    }

    private void updatePreference(long newPeriod) {
        prefs.edit().putLong(C.INVENTORIZE_PERIOD_DAYS, newPeriod).apply();
        period = newPeriod;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_settings;
    }
}
