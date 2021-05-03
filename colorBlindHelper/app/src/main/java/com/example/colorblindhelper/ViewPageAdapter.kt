package com.example.colorblindhelper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.colorblindhelper.ui.Tabs.LiveChangeFragment
import com.example.colorblindhelper.ui.Tabs.settings
import com.example.colorblindhelper.ui.Tabs.uploadImageFragment

class ViewPageAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {

    var mCountTabs = behavior
    init{
        this.mCountTabs = behavior
    }
    override fun getCount(): Int {
        return mCountTabs;
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> LiveChangeFragment()
            1 -> uploadImageFragment()
            2 -> settings()
            else -> LiveChangeFragment()
        }
    }
}