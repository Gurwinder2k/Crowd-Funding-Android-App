package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {


    public TabAdapter(@NonNull DonationHistoryFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //this is used to change the tabs, by default the first tab is shown
        switch (position){
            case 0:
                return new DonationHistoryTab();
            case 1:
                return new SubscriptionHistoryTab();
            default:
                return new DonationHistoryTab();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
