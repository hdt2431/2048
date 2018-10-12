package com.hdt.a2048.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;

public class MyMenuDialogFragment extends ContextMenuDialogFragment {
    @Override
    public void show(FragmentManager manager, String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);

        if(fragment ==null || !fragment.isAdded()){

            super.show(manager,tag);

        }
    }
}
